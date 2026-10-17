package vn.chungcumini.backend.quyettoan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.banggia.BangGiaService;
import vn.chungcumini.backend.domain.BanGiaoTaiSan;
import vn.chungcumini.backend.domain.ChiSo;
import vn.chungcumini.backend.domain.DongQuyetToan;
import vn.chungcumini.backend.domain.DongThongBaoTien;
import vn.chungcumini.backend.domain.HinhThucTT;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.HopDongDichVu;
import vn.chungcumini.backend.domain.LoaiBanGiao;
import vn.chungcumini.backend.domain.LoaiCongTo;
import vn.chungcumini.backend.domain.LoaiGhi;
import vn.chungcumini.backend.domain.LoaiKhoan;
import vn.chungcumini.backend.domain.LoaiKhoanQT;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;
import vn.chungcumini.backend.domain.LoaiThongBao;
import vn.chungcumini.backend.domain.PhieuChi;
import vn.chungcumini.backend.domain.QuyetToan;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiQT;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.hopdong.HopDongDichVuService;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.phieuchi.PhieuChiIdGenerator;
import vn.chungcumini.backend.repository.BanGiaoTaiSanRepository;
import vn.chungcumini.backend.repository.ChiSoRepository;
import vn.chungcumini.backend.repository.DongQuyetToanRepository;
import vn.chungcumini.backend.repository.DongThongBaoTienRepository;
import vn.chungcumini.backend.repository.PhieuChiRepository;
import vn.chungcumini.backend.repository.QuyetToanRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;

/**
 * QuyetToan — quyết toán cọc khi trả phòng (BR-TL-01, BR-TL-02, BR-TL-03, BR-TT-04). Gửi ThongBao
 * (QUYET_TOAN_DA_GUI, KHACH_PHAN_HOI_QT) chưa cài — module Thông báo chưa được xây.
 */
@Service
public class QuyetToanService {

    /** Các khoản trừ (−) — phần còn lại (TIEN_COC, HOAN_TIEN_PHONG) là khoản cộng (+), hệ thống tự quản lý. */
    private static final Set<LoaiKhoanQT> LOAI_TRU = Set.of(
            LoaiKhoanQT.DIEN, LoaiKhoanQT.NUOC, LoaiKhoanQT.DICH_VU, LoaiKhoanQT.NO_CHUA_TRA,
            LoaiKhoanQT.BOI_THUONG_HU_HONG, LoaiKhoanQT.PHAT_TRA_TRUOC_HAN, LoaiKhoanQT.KHAC);

    private final QuyetToanRepository quyetToanRepository;
    private final DongQuyetToanRepository dongQuyetToanRepository;
    private final PhieuChiRepository phieuChiRepository;
    private final ChiSoRepository chiSoRepository;
    private final BanGiaoTaiSanRepository banGiaoTaiSanRepository;
    private final ThongBaoTienRepository thongBaoTienRepository;
    private final DongThongBaoTienRepository dongThongBaoTienRepository;
    private final HopDongService hopDongService;
    private final HopDongDichVuService hopDongDichVuService;
    private final BangGiaService bangGiaService;
    private final ThongBaoTienService thongBaoTienService;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public QuyetToanService(
            QuyetToanRepository quyetToanRepository,
            DongQuyetToanRepository dongQuyetToanRepository,
            PhieuChiRepository phieuChiRepository,
            ChiSoRepository chiSoRepository,
            BanGiaoTaiSanRepository banGiaoTaiSanRepository,
            ThongBaoTienRepository thongBaoTienRepository,
            DongThongBaoTienRepository dongThongBaoTienRepository,
            HopDongService hopDongService,
            HopDongDichVuService hopDongDichVuService,
            BangGiaService bangGiaService,
            ThongBaoTienService thongBaoTienService,
            NhatKyThaoTacService nhatKyThaoTacService) {
        this.quyetToanRepository = quyetToanRepository;
        this.dongQuyetToanRepository = dongQuyetToanRepository;
        this.phieuChiRepository = phieuChiRepository;
        this.chiSoRepository = chiSoRepository;
        this.banGiaoTaiSanRepository = banGiaoTaiSanRepository;
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.dongThongBaoTienRepository = dongThongBaoTienRepository;
        this.hopDongService = hopDongService;
        this.hopDongDichVuService = hopDongDichVuService;
        this.bangGiaService = bangGiaService;
        this.thongBaoTienService = thongBaoTienService;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    public QuyetToan timHoacNem(String maQuyetToan) {
        return quyetToanRepository.timChiTiet(maQuyetToan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy quyết toán \"" + maQuyetToan + "\"."));
    }

    public QuyetToan timTheoHopDong(String maHopDong) {
        return quyetToanRepository.timTheoHopDong(maHopDong)
                .orElseThrow(() -> new IllegalArgumentException("Hợp đồng \"" + maHopDong + "\" chưa lập quyết toán."));
    }

    public List<DongQuyetToan> layDongTheoQuyetToan(String maQuyetToan) {
        return dongQuyetToanRepository.findByQuyetToan_MaQuyetToan(maQuyetToan);
    }

    /** KT-03.02: khách xem quyết toán của phòng mình (Mục 11). */
    public QuyetToan timTheoTaiKhoanKhach(String maTaiKhoan) {
        return quyetToanRepository.timTheoTaiKhoanKhach(maTaiKhoan)
                .orElseThrow(() -> new IllegalArgumentException("Phòng của bạn chưa có quyết toán."));
    }

    /**
     * QuyetToan.tinhQuyetToan (BR-TL-01, BR-TT-04): (lại) sinh toàn bộ dòng hệ thống — TIEN_COC(+),
     * HOAN_TIEN_PHONG(+), DIEN/NUOC/DICH_VU đến ngày trả(−), NO_CHUA_TRA(−), BOI_THUONG_HU_HONG(−),
     * PHAT_TRA_TRUOC_HAN(− nếu trả trước hạn). Gọi lại khi đang NHAP/KHACH_KHONG_DONG_Y sẽ tính lại
     * từ đầu — các khoản trừ chủ nhà tự thêm qua themKhoanTru() cũng bị xóa và cần thêm lại.
     */
    @Transactional
    public QuyetToan tinhQuyetToan(String maHopDong, String maTaiKhoanThucHien) {
        HopDong hopDong = hopDongService.timHoacNem(maHopDong);
        if (hopDong.getTrangThai() != TrangThaiHD.HIEU_LUC) {
            throw new IllegalArgumentException("Chỉ lập quyết toán cho hợp đồng đang hiệu lực.");
        }
        if (hopDong.getNgayTraDuKien() == null) {
            throw new IllegalArgumentException("Hợp đồng chưa báo trả phòng.");
        }
        LocalDate ngayTra = hopDong.getNgayTraDuKien();

        ChiSo dienCuoiKy = chiSoRepository
                .findByHopDong_MaHopDongAndLoaiCongToAndLoaiGhi(maHopDong, LoaiCongTo.DIEN, LoaiGhi.CUOI_KY)
                .orElseThrow(() -> new IllegalArgumentException("Chưa ghi chỉ số điện cuối kỳ."));
        boolean coCongToNuoc = hopDong.getPhong().isCoCongToNuoc();
        ChiSo nuocCuoiKy = null;
        if (coCongToNuoc) {
            nuocCuoiKy = chiSoRepository
                    .findByHopDong_MaHopDongAndLoaiCongToAndLoaiGhi(maHopDong, LoaiCongTo.NUOC, LoaiGhi.CUOI_KY)
                    .orElseThrow(() -> new IllegalArgumentException("Chưa ghi chỉ số nước cuối kỳ."));
        }
        if (banGiaoTaiSanRepository.findByHopDong_MaHopDongAndLoaiBanGiao(maHopDong, LoaiBanGiao.TRA_PHONG).isEmpty()) {
            throw new IllegalArgumentException("Chưa kiểm tra bàn giao tài sản lúc trả phòng.");
        }

        QuyetToan qt = quyetToanRepository.timTheoHopDong(maHopDong).orElse(null);
        if (qt != null && qt.getTrangThai() != TrangThaiQT.NHAP && qt.getTrangThai() != TrangThaiQT.KHACH_KHONG_DONG_Y) {
            throw new IllegalArgumentException("Chỉ tính lại quyết toán khi đang Nháp hoặc Khách không đồng ý.");
        }
        if (qt == null) {
            qt = new QuyetToan();
            qt.setMaQuyetToan("QT" + maHopDong);
            qt.setHopDong(hopDong);
            qt.setTienCoc(BigDecimal.ZERO);
            qt.setSoTienQuyetToan(BigDecimal.ZERO);
        } else {
            dongQuyetToanRepository.deleteByQuyetToan_MaQuyetToan(qt.getMaQuyetToan());
        }
        qt.setNgayTra(ngayTra);
        qt = quyetToanRepository.save(qt);

        LocalDate ngayLap = LocalDate.now();

        themDongHeThong(qt, LoaiKhoanQT.TIEN_COC, hopDong.getTienCoc(), "Hoàn tiền cọc", null);

        BigDecimal hoanTienPhong = tinhHoanTienPhong(maHopDong, ngayTra);
        if (hoanTienPhong.signum() > 0) {
            themDongHeThong(
                    qt, LoaiKhoanQT.HOAN_TIEN_PHONG, hoanTienPhong,
                    "Tiền phòng đã thu cho các ngày từ " + ngayTra + " trở đi", null);
        }

        int tieuThuDien = dienCuoiKy.getChiSoMoi() - dienCuoiKy.getChiSoCu();
        if (tieuThuDien > 0) {
            BigDecimal tienDien = BigDecimal.valueOf(tieuThuDien).multiply(bangGiaService.layDonGiaTai(LoaiKhoan.DIEN, ngayLap));
            themDongHeThong(qt, LoaiKhoanQT.DIEN, tienDien, "Điện đến ngày trả (" + tieuThuDien + " kWh)", dienCuoiKy.getAnhCongTo());
        }

        if (coCongToNuoc) {
            int tieuThuNuoc = nuocCuoiKy.getChiSoMoi() - nuocCuoiKy.getChiSoCu();
            if (tieuThuNuoc > 0) {
                BigDecimal tienNuoc = BigDecimal.valueOf(tieuThuNuoc).multiply(bangGiaService.layDonGiaTai(LoaiKhoan.NUOC, ngayLap));
                themDongHeThong(
                        qt, LoaiKhoanQT.NUOC, tienNuoc, "Nước đến ngày trả (" + tieuThuNuoc + " khối)", nuocCuoiKy.getAnhCongTo());
            }
        } else {
            BigDecimal tyLeChuaLap = tyLeChuaLapHoaDon(hopDong, maHopDong, ngayTra);
            if (tyLeChuaLap.signum() > 0) {
                BigDecimal soNguoi = BigDecimal.valueOf(hopDongService.demSoNguoiO(maHopDong));
                BigDecimal tienNuoc = soNguoi.multiply(bangGiaService.layDonGiaTai(LoaiKhoan.NUOC_THEO_NGUOI, ngayLap)).multiply(tyLeChuaLap);
                themDongHeThong(qt, LoaiKhoanQT.NUOC, tienNuoc, "Nước theo người đến ngày trả", null);
            }
        }

        BigDecimal tyLeChuaLapDV = tyLeChuaLapHoaDon(hopDong, maHopDong, ngayTra);
        if (tyLeChuaLapDV.signum() > 0) {
            for (HopDongDichVu hddv : hopDongDichVuService.layTheoHopDong(maHopDong)) {
                BigDecimal tien = hddv.getMucPhi().multiply(BigDecimal.valueOf(hddv.getSoLuong())).multiply(tyLeChuaLapDV);
                themDongHeThong(qt, LoaiKhoanQT.DICH_VU, tien, hddv.getBangGia().getTenKhoan() + " đến ngày trả", null);
            }
        }

        BigDecimal noChuaTra = thongBaoTienRepository.tongNoChuaTra(maHopDong);
        if (noChuaTra.signum() > 0) {
            themDongHeThong(qt, LoaiKhoanQT.NO_CHUA_TRA, noChuaTra, "Nợ tiền thuê/điện nước/dịch vụ chưa thu đủ", null);
        }

        BigDecimal huHong = banGiaoTaiSanRepository.tongChiPhiHuHong(maHopDong);
        if (huHong.signum() > 0) {
            themDongHeThong(qt, LoaiKhoanQT.BOI_THUONG_HU_HONG, huHong, "Bồi thường hư hỏng tài sản lúc trả phòng", null);
        }

        if (!ngayTra.isAfter(hopDong.getNgayHetHan()) && hopDong.getMucPhatTraTruoc().signum() > 0) {
            themDongHeThong(
                    qt, LoaiKhoanQT.PHAT_TRA_TRUOC_HAN, hopDong.getMucPhatTraTruoc(), "Phạt trả phòng trước hạn hợp đồng", null);
        }

        tinhLaiTong(qt);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "QuyetToan", qt.getMaQuyetToan(), "TAO", "Tính quyết toán cọc trả phòng.");
        return quyetToanRepository.timChiTiet(qt.getMaQuyetToan()).orElseThrow();
    }

    /**
     * Tiền phòng đã thu cho các ngày từ ngayTra trở đi: với mỗi dòng TIEN_PHONG (của thông báo chưa
     * hủy) có denNgay ≥ ngayTra, hoàn phần ngày từ ngayTra tới denNgay, theo đúng tỷ lệ đã thu của
     * thông báo đó (soDaThu/tongTien) — vì hệ thống không lưu tỷ lệ đã thu riêng theo từng dòng.
     */
    private BigDecimal tinhHoanTienPhong(String maHopDong, LocalDate ngayTra) {
        BigDecimal tong = BigDecimal.ZERO;
        for (DongThongBaoTien d : dongThongBaoTienRepository.timDongTienPhongTuNgayTra(maHopDong, ngayTra)) {
            ThongBaoTien tb = d.getThongBaoTien();
            if (tb.getTongTien().signum() == 0 || d.getTuNgay() == null || d.getDenNgay() == null) {
                continue;
            }
            LocalDate batDauHoan = d.getTuNgay().isAfter(ngayTra) ? d.getTuNgay() : ngayTra;
            long tongNgayDong = d.getDenNgay().toEpochDay() - d.getTuNgay().toEpochDay() + 1;
            long ngayHoan = d.getDenNgay().toEpochDay() - batDauHoan.toEpochDay() + 1;
            BigDecimal tyLeHoanNgay = BigDecimal.valueOf(ngayHoan).divide(BigDecimal.valueOf(tongNgayDong), 6, RoundingMode.HALF_UP);
            BigDecimal tyLeDaThu = tb.getSoDaThu().divide(tb.getTongTien(), 6, RoundingMode.HALF_UP).min(BigDecimal.ONE);
            tong = tong.add(d.getThanhTien().multiply(tyLeHoanNgay).multiply(tyLeDaThu));
        }
        return tong.setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * Tỷ lệ số ngày (trên tổng số ngày của tháng chứa ngayTra) kể từ sau kỳ HANG_THANG đã lập gần
     * nhất (điện/nước/dịch vụ của kỳ đó đã được tính rồi) cho tới ngayTra — phần "đến ngày trả" chưa
     * được lập hóa đơn. Giả định đơn giản: khoảng trống không vượt quá 1 tháng (lập hóa đơn hàng
     * tháng đều đặn trước khi trả phòng xảy ra).
     */
    private BigDecimal tyLeChuaLapHoaDon(HopDong hopDong, String maHopDong, LocalDate ngayTra) {
        LocalDate batDau = thongBaoTienRepository
                .findFirstByHopDong_MaHopDongAndLoaiThongBaoAndTrangThaiNotOrderByKyThangDesc(
                        maHopDong, LoaiThongBao.HANG_THANG, TrangThaiTBT.DA_HUY)
                .map(tb -> YearMonth.parse(tb.getKyThang()).atEndOfMonth().plusDays(1))
                .orElse(hopDong.getNgayBatDau());
        if (batDau.isAfter(ngayTra)) {
            return BigDecimal.ZERO;
        }
        long soNgay = ngayTra.toEpochDay() - batDau.toEpochDay() + 1;
        return BigDecimal.valueOf(soNgay).divide(BigDecimal.valueOf(YearMonth.from(ngayTra).lengthOfMonth()), 6, RoundingMode.HALF_UP);
    }

    private void themDongHeThong(QuyetToan qt, LoaiKhoanQT loaiKhoan, BigDecimal soTien, String ghiChu, String anhBangChung) {
        DongQuyetToan d = new DongQuyetToan();
        d.setMaDong("DQT" + String.format("%06d", dongQuyetToanRepository.count() + 1));
        d.setQuyetToan(qt);
        d.setLoaiKhoan(loaiKhoan);
        d.setSoTien(soTien.setScale(2, RoundingMode.HALF_UP));
        d.setGhiChu(ghiChu);
        d.setAnhBangChung(anhBangChung);
        dongQuyetToanRepository.save(d);
    }

    /**
     * QuyetToan.themKhoanTru (BR-TL-02): chỉ thêm khoản trừ thủ công; TIEN_COC/HOAN_TIEN_PHONG do hệ
     * thống tự quản lý qua tinhQuyetToan().
     */
    @Transactional
    public DongQuyetToan themKhoanTru(
            String maQuyetToan,
            LoaiKhoanQT loaiKhoan,
            BigDecimal soTien,
            String ghiChu,
            String anhBangChung,
            String lyDoDieuChinh,
            String maTaiKhoanThucHien) {
        QuyetToan qt = timHoacNem(maQuyetToan);
        kiemTraDangSua(qt);
        if (!LOAI_TRU.contains(loaiKhoan)) {
            throw new IllegalArgumentException("Chỉ thêm được khoản trừ.");
        }
        if (soTien == null || soTien.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        }
        if (!coBangChung(loaiKhoan, ghiChu, anhBangChung)) {
            throw new IllegalArgumentException("Khoản trừ phải có ghi chú và ảnh bằng chứng (BR-TL-02).");
        }
        capNhatLyDoNeuDangDieuChinh(qt, lyDoDieuChinh);

        DongQuyetToan d = new DongQuyetToan();
        d.setMaDong("DQT" + String.format("%06d", dongQuyetToanRepository.count() + 1));
        d.setQuyetToan(qt);
        d.setLoaiKhoan(loaiKhoan);
        d.setSoTien(soTien);
        d.setGhiChu(ghiChu);
        d.setAnhBangChung(anhBangChung);
        d = dongQuyetToanRepository.save(d);

        tinhLaiTong(qt);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "QuyetToan", maQuyetToan, "SUA", "Thêm khoản trừ \"" + loaiKhoan + "\".");
        return d;
    }

    /** QuyetToan.suaKhoanTru (BR-TL-02, BR-TL-03). */
    @Transactional
    public DongQuyetToan suaKhoanTru(
            String maQuyetToan,
            String maDong,
            BigDecimal soTienMoi,
            String ghiChuMoi,
            String anhBangChungMoi,
            String lyDo,
            String maTaiKhoanThucHien) {
        QuyetToan qt = timHoacNem(maQuyetToan);
        kiemTraDangSua(qt);
        DongQuyetToan d = layDongThuocQuyetToan(maQuyetToan, maDong);
        if (soTienMoi == null || soTienMoi.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        }
        if (!coBangChung(d.getLoaiKhoan(), ghiChuMoi, anhBangChungMoi)) {
            throw new IllegalArgumentException("Khoản trừ phải có ghi chú và ảnh bằng chứng (BR-TL-02).");
        }
        capNhatLyDoNeuDangDieuChinh(qt, lyDo);

        d.setSoTien(soTienMoi);
        d.setGhiChu(ghiChuMoi);
        d.setAnhBangChung(anhBangChungMoi);
        d = dongQuyetToanRepository.save(d);

        tinhLaiTong(qt);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "QuyetToan", maQuyetToan, "SUA", "Sửa khoản trừ \"" + maDong + "\".");
        return d;
    }

    /** QuyetToan.xoaKhoanTru (BR-TL-03). */
    @Transactional
    public void xoaKhoanTru(String maQuyetToan, String maDong, String lyDo, String maTaiKhoanThucHien) {
        QuyetToan qt = timHoacNem(maQuyetToan);
        kiemTraDangSua(qt);
        DongQuyetToan d = layDongThuocQuyetToan(maQuyetToan, maDong);
        capNhatLyDoNeuDangDieuChinh(qt, lyDo);

        dongQuyetToanRepository.delete(d);
        tinhLaiTong(qt);
        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien, "QuyetToan", maQuyetToan, "SUA", "Xóa khoản trừ \"" + maDong + "\": " + lyDo + ".");
    }

    private void kiemTraDangSua(QuyetToan qt) {
        if (qt.getTrangThai() != TrangThaiQT.NHAP && qt.getTrangThai() != TrangThaiQT.KHACH_KHONG_DONG_Y) {
            throw new IllegalArgumentException("Chỉ sửa khoản trừ khi quyết toán đang Nháp hoặc Khách không đồng ý.");
        }
    }

    private DongQuyetToan layDongThuocQuyetToan(String maQuyetToan, String maDong) {
        DongQuyetToan d = dongQuyetToanRepository.findById(maDong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy dòng \"" + maDong + "\"."));
        if (!d.getQuyetToan().getMaQuyetToan().equals(maQuyetToan)) {
            throw new IllegalArgumentException("Dòng \"" + maDong + "\" không thuộc quyết toán \"" + maQuyetToan + "\".");
        }
        if (d.getLoaiKhoan() == LoaiKhoanQT.TIEN_COC || d.getLoaiKhoan() == LoaiKhoanQT.HOAN_TIEN_PHONG) {
            throw new IllegalArgumentException("Không thể sửa/xóa dòng tiền cọc hoặc hoàn tiền phòng (hệ thống tự quản lý).");
        }
        return d;
    }

    private void capNhatLyDoNeuDangDieuChinh(QuyetToan qt, String lyDoDieuChinh) {
        if (qt.getTrangThai() == TrangThaiQT.KHACH_KHONG_DONG_Y) {
            if (lyDoDieuChinh == null || lyDoDieuChinh.isBlank()) {
                throw new IllegalArgumentException("Bắt buộc ghi lý do điều chỉnh khi khách không đồng ý (BR-TL-03).");
            }
            qt.setLyDoDieuChinh(lyDoDieuChinh);
        }
    }

    /** DongQuyetToan.coBangChung: true khi là dòng cộng, hoặc dòng trừ có ghiChu và anhBangChung. */
    private static boolean coBangChung(LoaiKhoanQT loaiKhoan, String ghiChu, String anhBangChung) {
        return !LOAI_TRU.contains(loaiKhoan)
                || (ghiChu != null && !ghiChu.isBlank() && anhBangChung != null && !anhBangChung.isBlank());
    }

    private void tinhLaiTong(QuyetToan qt) {
        List<DongQuyetToan> dong = dongQuyetToanRepository.findByQuyetToan_MaQuyetToan(qt.getMaQuyetToan());
        BigDecimal tienCoc = tongTheoLoai(dong, LoaiKhoanQT.TIEN_COC);
        BigDecimal tienPhongHoanLai = tongTheoLoai(dong, LoaiKhoanQT.HOAN_TIEN_PHONG);
        BigDecimal tongKhauTru = dong.stream()
                .filter(d -> LOAI_TRU.contains(d.getLoaiKhoan()))
                .map(DongQuyetToan::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        qt.setTienCoc(tienCoc.setScale(0, RoundingMode.HALF_UP));
        qt.setTienPhongHoanLai(tienPhongHoanLai.setScale(0, RoundingMode.HALF_UP));
        qt.setTongKhauTru(tongKhauTru.setScale(0, RoundingMode.HALF_UP));
        qt.setSoTienQuyetToan(tienCoc.add(tienPhongHoanLai).subtract(tongKhauTru).setScale(0, RoundingMode.HALF_UP));
        quyetToanRepository.save(qt);
    }

    private static BigDecimal tongTheoLoai(List<DongQuyetToan> dong, LoaiKhoanQT loai) {
        return dong.stream().filter(d -> d.getLoaiKhoan() == loai).map(DongQuyetToan::getSoTien).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** QuyetToan.guiKhach (BR-TL-03). */
    @Transactional
    public void guiKhach(String maQuyetToan, String maTaiKhoanThucHien) {
        QuyetToan qt = timHoacNem(maQuyetToan);
        if (qt.getTrangThai() != TrangThaiQT.NHAP && qt.getTrangThai() != TrangThaiQT.KHACH_KHONG_DONG_Y) {
            throw new IllegalArgumentException("Chỉ gửi quyết toán khi đang Nháp hoặc Khách không đồng ý.");
        }
        qt.setTrangThai(TrangThaiQT.CHO_PHAN_HOI);
        qt.setKhachDongY(null);
        qt.setYKienKhach(null);
        qt.setThoiDiemPhanHoi(null);
        quyetToanRepository.save(qt);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "QuyetToan", maQuyetToan, "SUA", "Gửi quyết toán cho khách xem và phản hồi.");
    }

    /** QuyetToan.khachPhanHoi (BR-TL-03): dongY=true → DA_CHOT ngay; dongY=false → KHACH_KHONG_DONG_Y, bắt buộc yKien. */
    @Transactional
    public void khachPhanHoi(String maQuyetToan, boolean dongY, String yKien, String maTaiKhoanKhach) {
        QuyetToan qt = timHoacNem(maQuyetToan);
        if (qt.getTrangThai() != TrangThaiQT.CHO_PHAN_HOI) {
            throw new IllegalArgumentException("Quyết toán không ở trạng thái chờ khách phản hồi.");
        }
        if (!maTaiKhoanKhach.equals(qt.getHopDong().getMaTaiKhoan())) {
            throw new IllegalArgumentException("Quyết toán không thuộc phòng của bạn.");
        }
        if (!dongY && (yKien == null || yKien.isBlank())) {
            throw new IllegalArgumentException("Bắt buộc ghi ý kiến khi không đồng ý.");
        }

        qt.setKhachDongY(dongY);
        qt.setYKienKhach(dongY ? null : yKien);
        qt.setThoiDiemPhanHoi(LocalDateTime.now());
        qt.setTrangThai(dongY ? TrangThaiQT.DA_CHOT : TrangThaiQT.KHACH_KHONG_DONG_Y);
        quyetToanRepository.save(qt);

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanKhach, "QuyetToan", maQuyetToan, dongY ? "XAC_NHAN" : "TU_CHOI",
                dongY ? "Khách đồng ý quyết toán." : "Khách không đồng ý quyết toán: " + yKien);
    }

    /** QuyetToan.chotQuyetToan (BR-TL-03): chủ nhà chốt khi khách không đồng ý hoặc không phản hồi. */
    @Transactional
    public void chotQuyetToan(String maQuyetToan, String lyDo, String maTaiKhoanThucHien) {
        QuyetToan qt = timHoacNem(maQuyetToan);
        if (qt.getTrangThai() != TrangThaiQT.CHO_PHAN_HOI && qt.getTrangThai() != TrangThaiQT.KHACH_KHONG_DONG_Y) {
            throw new IllegalArgumentException("Chỉ chốt quyết toán khi đang chờ phản hồi hoặc khách không đồng ý.");
        }
        if (lyDo == null || lyDo.isBlank()) {
            throw new IllegalArgumentException("Bắt buộc ghi lý do chốt (BR-TL-03).");
        }
        qt.setLyDoDieuChinh(lyDo);
        qt.setTrangThai(TrangThaiQT.DA_CHOT);
        quyetToanRepository.save(qt);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "QuyetToan", maQuyetToan, "SUA", "Chốt quyết toán: " + lyDo);
    }

    /** QuyetToan.lapPhieuChi: chỉ khi số hoàn > 0. */
    @Transactional
    public PhieuChi lapPhieuChi(String maQuyetToan, HinhThucTT hinhThuc, String maTaiKhoanThucHien) {
        QuyetToan qt = timHoacNem(maQuyetToan);
        if (qt.getTrangThai() != TrangThaiQT.DA_CHOT) {
            throw new IllegalArgumentException("Chỉ lập phiếu chi khi quyết toán đã chốt.");
        }
        if (qt.getSoTienQuyetToan().signum() <= 0) {
            throw new IllegalArgumentException("Chỉ lập phiếu chi khi số quyết toán dương (có hoàn cọc).");
        }

        LocalDate homNay = LocalDate.now();
        PhieuChi pc = new PhieuChi();
        pc.setSoPhieuChi(PhieuChiIdGenerator.sinh(phieuChiRepository, homNay));
        pc.setQuyetToan(qt);
        pc.setNgayChi(homNay);
        pc.setNguoiNhan(qt.getHopDong().getKhachThue().getHoTen());
        pc.setSoTien(qt.getSoTienQuyetToan());
        pc.setHinhThuc(hinhThuc);
        pc = phieuChiRepository.save(pc);

        qt.setTrangThai(TrangThaiQT.HOAN_TAT);
        quyetToanRepository.save(qt);

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien, "PhieuChi", pc.getSoPhieuChi(), "TAO", "Lập phiếu chi hoàn cọc cho quyết toán \"" + maQuyetToan + "\".");
        return pc;
    }

    /** QuyetToan.lapThongBaoThuThem: chỉ khi số hoàn < 0 (khách phải trả thêm). */
    @Transactional
    public ThongBaoTien lapThongBaoThuThem(String maQuyetToan, String maTaiKhoanThucHien) {
        QuyetToan qt = timHoacNem(maQuyetToan);
        if (qt.getTrangThai() != TrangThaiQT.DA_CHOT) {
            throw new IllegalArgumentException("Chỉ lập thông báo thu thêm khi quyết toán đã chốt.");
        }
        if (qt.getSoTienQuyetToan().signum() >= 0) {
            throw new IllegalArgumentException("Chỉ lập thông báo thu thêm khi số quyết toán âm (khách phải trả thêm).");
        }

        BigDecimal soTien = qt.getSoTienQuyetToan().abs();
        ThongBaoTien tb = thongBaoTienService.taoNhap(
                qt.getHopDong(), LoaiThongBao.THU_THEM_QUYET_TOAN, YearMonth.now().toString(), LocalDate.now(), BigDecimal.ZERO);
        thongBaoTienService.themDong(
                tb.getMaThongBaoTien(), LoaiKhoanTBT.PHI_PHAT_SINH, "Thu thêm theo quyết toán \"" + maQuyetToan + "\".",
                null, null, BigDecimal.ONE, soTien, BigDecimal.ONE, null, null);
        thongBaoTienService.ganQuyetToan(tb.getMaThongBaoTien(), maQuyetToan);
        thongBaoTienService.phatHanh(tb.getMaThongBaoTien(), maTaiKhoanThucHien);

        qt.setTrangThai(TrangThaiQT.HOAN_TAT);
        quyetToanRepository.save(qt);
        return thongBaoTienService.timHoacNem(tb.getMaThongBaoTien());
    }
}
