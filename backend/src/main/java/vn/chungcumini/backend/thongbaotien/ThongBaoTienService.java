package vn.chungcumini.backend.thongbaotien;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.ChiSo;
import vn.chungcumini.backend.domain.DongThongBaoTien;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;
import vn.chungcumini.backend.domain.LoaiSuKien;
import vn.chungcumini.backend.domain.LoaiThongBao;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.domain.HinhThucTT;
import vn.chungcumini.backend.domain.PhieuThu;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.phieuthu.PhieuThuIdGenerator;
import vn.chungcumini.backend.repository.ChiSoRepository;
import vn.chungcumini.backend.repository.DongThongBaoTienRepository;
import vn.chungcumini.backend.repository.PhieuThuRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;
import vn.chungcumini.backend.thongbao.ThongBaoService;
import vn.chungcumini.backend.toanha.ToaNhaService;

@Service
public class ThongBaoTienService {

    private final ThongBaoTienRepository thongBaoTienRepository;
    private final DongThongBaoTienRepository dongThongBaoTienRepository;
    private final ChiSoRepository chiSoRepository;
    private final PhieuThuRepository phieuThuRepository;
    private final ToaNhaService toaNhaService;
    private final NhatKyThaoTacService nhatKyThaoTacService;
    private final ThongBaoService thongBaoService;

    public ThongBaoTienService(
            ThongBaoTienRepository thongBaoTienRepository,
            DongThongBaoTienRepository dongThongBaoTienRepository,
            ChiSoRepository chiSoRepository,
            PhieuThuRepository phieuThuRepository,
            ToaNhaService toaNhaService,
            NhatKyThaoTacService nhatKyThaoTacService,
            ThongBaoService thongBaoService) {
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.dongThongBaoTienRepository = dongThongBaoTienRepository;
        this.chiSoRepository = chiSoRepository;
        this.phieuThuRepository = phieuThuRepository;
        this.toaNhaService = toaNhaService;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
        this.thongBaoService = thongBaoService;
    }

    public ThongBaoTien timHoacNem(String maThongBaoTien) {
        return thongBaoTienRepository.timChiTiet(maThongBaoTien)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông báo tiền \"" + maThongBaoTien + "\"."));
    }

    public List<DongThongBaoTien> layDongTheoThongBao(String maThongBaoTien) {
        return dongThongBaoTienRepository.findByThongBaoTien_MaThongBaoTien(maThongBaoTien);
    }

    /** QuyetToan.lapThongBaoThuThem: gắn thông báo THU_THEM_QUYET_TOAN vào quyết toán gốc. */
    @Transactional
    public void ganQuyetToan(String maThongBaoTien, String maQuyetToan) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        tb.setMaQuyetToan(maQuyetToan);
        thongBaoTienRepository.save(tb);
    }

    /** Mục 11: danh sách thông báo tiền của khách đang đăng nhập — lọc theo phòng đang thuê. */
    public List<ThongBaoTien> layTheoTaiKhoanKhach(String maTaiKhoan) {
        return thongBaoTienRepository.timTheoTaiKhoanKhach(maTaiKhoan);
    }

    /** BR-CT-05: HopDong.xacNhanNhanPhong chỉ bỏ qua yêu cầu lyDo khi thông báo ký HĐ đã thu đủ. */
    public boolean kyHopDongDaThuDu(String maHopDong) {
        return thongBaoTienRepository.findByHopDong_MaHopDongAndLoaiThongBao(maHopDong, LoaiThongBao.KY_HOP_DONG)
                .map(tb -> tb.getTrangThai() == TrangThaiTBT.DA_THU_DU)
                .orElse(false);
    }

    /** Màn hình Nhận phòng (CN-03.03): tìm thông báo ký HĐ của một hợp đồng, nếu đã lập. */
    public java.util.Optional<ThongBaoTien> timThongBaoKyHopDong(String maHopDong) {
        return thongBaoTienRepository.findByHopDong_MaHopDongAndLoaiThongBao(maHopDong, LoaiThongBao.KY_HOP_DONG);
    }

    /** Tạo bản ghi NHAP — CRUD cơ bản dùng chung cho lapThongBaoKyHopDong (HopDong) và lapThongBaoHangLoat. */
    @Transactional
    public ThongBaoTien taoNhap(HopDong hopDong, LoaiThongBao loaiThongBao, String kyThang, LocalDate hanThanhToan, BigDecimal noCu) {
        String maGoc = "TB" + kyThangThanhMa(kyThang) + hopDong.getPhong().getMaPhong();
        long soLanLap = thongBaoTienRepository.countByMaThongBaoTienStartingWith(maGoc);

        ThongBaoTien tb = new ThongBaoTien();
        tb.setMaThongBaoTien(soLanLap == 0 ? maGoc : maGoc + "R" + soLanLap);
        tb.setHopDong(hopDong);
        tb.setLoaiThongBao(loaiThongBao);
        tb.setKyThang(kyThang);
        tb.setHanThanhToan(hanThanhToan);
        tb.setNoCu(noCu != null ? noCu : BigDecimal.ZERO);
        tb.setTongTien(tb.getNoCu());
        return thongBaoTienRepository.save(tb);
    }

    private static String kyThangThanhMa(String kyThang) {
        return kyThang.substring(2, 4) + kyThang.substring(5, 7);
    }

    /**
     * ThongBaoTien.themDong. Dòng DIEU_CHINH bắt buộc maThongBaoTienGoc (TB DA_THU_DU/DA_THU_MOT_PHAN
     * cùng HĐ) và dienGiai nêu lý do (BR-HD-04).
     */
    @Transactional
    public DongThongBaoTien themDong(
            String maThongBaoTien,
            LoaiKhoanTBT loaiKhoan,
            String dienGiai,
            LocalDate tuNgay,
            LocalDate denNgay,
            BigDecimal soLuong,
            BigDecimal donGia,
            BigDecimal tyLeNgayO,
            String maChiSo,
            String maThongBaoTienGoc) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        if (tb.getTrangThai() != TrangThaiTBT.NHAP) {
            throw new IllegalArgumentException("Chỉ thêm dòng khi thông báo đang ở trạng thái Nháp.");
        }

        DongThongBaoTien dong = new DongThongBaoTien();
        dong.setMaDong("DTB" + String.format("%06d", dongThongBaoTienRepository.count() + 1));
        dong.setThongBaoTien(tb);
        dong.setLoaiKhoan(loaiKhoan);
        dong.setDienGiai(dienGiai);
        dong.setTuNgay(tuNgay);
        dong.setDenNgay(denNgay);
        dong.setSoLuong(soLuong != null ? soLuong : BigDecimal.ONE);
        dong.setDonGia(donGia);
        dong.setTyLeNgayO(tyLeNgayO != null ? tyLeNgayO : BigDecimal.ONE);
        dong.setThanhTien(ThanhTienCalculator.tinh(loaiKhoan, dong.getSoLuong(), dong.getDonGia(), dong.getTyLeNgayO()));

        if (maChiSo != null) {
            ChiSo chiSo = chiSoRepository.findById(maChiSo)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chỉ số \"" + maChiSo + "\"."));
            dong.setChiSo(chiSo);
        }
        if (loaiKhoan == LoaiKhoanTBT.DIEU_CHINH) {
            if (maThongBaoTienGoc == null || dienGiai == null || dienGiai.isBlank()) {
                throw new IllegalArgumentException("Dòng điều chỉnh bắt buộc có thông báo gốc và diễn giải lý do.");
            }
            ThongBaoTien tbGoc = thongBaoTienRepository.findById(maThongBaoTienGoc)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông báo gốc \"" + maThongBaoTienGoc + "\"."));
            if (!tbGoc.getHopDong().getMaHopDong().equals(tb.getHopDong().getMaHopDong())
                    || (tbGoc.getTrangThai() != TrangThaiTBT.DA_THU_DU && tbGoc.getTrangThai() != TrangThaiTBT.DA_THU_MOT_PHAN)) {
                throw new IllegalArgumentException("Thông báo gốc phải cùng hợp đồng và đã thu đủ/một phần.");
            }
            dong.setThongBaoTienGoc(tbGoc);
        }

        dong = dongThongBaoTienRepository.save(dong);
        tinhTong(maThongBaoTien);
        return dong;
    }

    /** ThongBaoTien.tinhTong: làm tròn ở tổng đến 1.000đ, không làm tròn từng dòng (BR-HD-05). */
    @Transactional
    public BigDecimal tinhTong(String maThongBaoTien) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        BigDecimal tongDong = dongThongBaoTienRepository.findByThongBaoTien_MaThongBaoTien(maThongBaoTien).stream()
                .map(DongThongBaoTien::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tong = tongDong.add(tb.getNoCu())
                .divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000));
        tb.setTongTien(tong);
        thongBaoTienRepository.save(tb);
        return tong;
    }

    /**
     * ThongBaoTien.phatHanh. Trả về true nếu cần cảnh báo HĐ hết hạn trong tháng kế tiếp mà chưa gia hạn
     * (BR-HD-03) — chỉ cảnh báo, không chặn phát hành.
     */
    @Transactional
    public boolean phatHanh(String maThongBaoTien, String maTaiKhoanThucHien) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        if (tb.getTrangThai() != TrangThaiTBT.NHAP) {
            throw new IllegalArgumentException("Chỉ phát hành thông báo đang ở trạng thái Nháp.");
        }
        if (tb.getLoaiThongBao() == LoaiThongBao.HANG_THANG) {
            List<DongThongBaoTien> dong = dongThongBaoTienRepository.findByThongBaoTien_MaThongBaoTien(maThongBaoTien);
            boolean coDien = dong.stream().anyMatch(d -> d.getLoaiKhoan() == LoaiKhoanTBT.DIEN);
            boolean coNuoc = !tb.getHopDong().getPhong().isCoCongToNuoc()
                    || dong.stream().anyMatch(d -> d.getLoaiKhoan() == LoaiKhoanTBT.NUOC);
            if (!coDien || !coNuoc) {
                throw new IllegalArgumentException("Thiếu dòng điện/nước, không thể phát hành (BR-HD-02).");
            }
        }

        tb.setTrangThai(TrangThaiTBT.DA_PHAT_HANH);
        tb.setNgayPhatHanh(LocalDate.now());
        thongBaoTienRepository.save(tb);

        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "ThongBaoTien", maThongBaoTien, "PHAT_HANH", "Phát hành thông báo tiền.");

        if (tb.getHopDong().getMaTaiKhoan() != null) {
            thongBaoService.guiDen(
                    LoaiSuKien.TB_TIEN_MOI, "Thông báo tiền mới",
                    "Phòng " + tb.getHopDong().getPhong().getMaPhong() + " có thông báo tiền kỳ " + tb.getKyThang()
                            + ", hạn thanh toán " + tb.getHanThanhToan() + ".",
                    "ThongBaoTien:" + maThongBaoTien, List.of(tb.getHopDong().getMaTaiKhoan()));
        }

        // BR-HD-03: tiền phòng của thông báo HANG_THANG luôn cho tháng kế tiếp kyThang (xem
        // lapThongBaoHangLoat) — cảnh báo nếu HĐ hết hạn trong đúng tháng đó. Không áp dụng cho
        // KY_HOP_DONG (tiền phòng của nó là phần còn lại của tháng ký, không phải tháng sau).
        if (tb.getLoaiThongBao() != LoaiThongBao.HANG_THANG) {
            return false;
        }
        LocalDate hetHan = tb.getHopDong().getNgayHetHan();
        java.time.YearMonth thangKeTiep = java.time.YearMonth.parse(tb.getKyThang()).plusMonths(1);
        return !hetHan.isAfter(thangKeTiep.atEndOfMonth()) && !hetHan.isBefore(thangKeTiep.atDay(1));
    }

    /** ThongBaoTien.huy: NHAP thì xóa thẳng, không qua DA_HUY; đã phát hành thì chỉ hủy khi chưa thu (BR-HD-04). */
    @Transactional
    public void huy(String maThongBaoTien, String lyDo, String maTaiKhoanThucHien) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        if (tb.getTrangThai() == TrangThaiTBT.NHAP) {
            dongThongBaoTienRepository.findByThongBaoTien_MaThongBaoTien(maThongBaoTien).forEach(dongThongBaoTienRepository::delete);
            thongBaoTienRepository.delete(tb);
            return;
        }
        if (tb.getTrangThai() != TrangThaiTBT.DA_PHAT_HANH && tb.getTrangThai() != TrangThaiTBT.QUA_HAN) {
            throw new IllegalArgumentException("Chỉ hủy thông báo đã phát hành hoặc quá hạn.");
        }
        if (tb.getSoDaThu().signum() != 0) {
            throw new IllegalArgumentException("Thông báo đã có khoản thu, không thể hủy — ghi điều chỉnh vào tháng sau.");
        }
        if (lyDo == null || lyDo.isBlank()) {
            throw new IllegalArgumentException("Bắt buộc ghi lý do hủy.");
        }

        tb.setTrangThai(TrangThaiTBT.DA_HUY);
        tb.setLyDoHuy(lyDo);
        thongBaoTienRepository.save(tb);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "ThongBaoTien", maThongBaoTien, "HUY", lyDo);
    }

    /** ThongBaoTien.taoMaVietQR: QR cho số tiền còn phải trả. */
    public String taoMaVietQR(String maThongBaoTien) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        if (tb.getTrangThai() == TrangThaiTBT.NHAP || tb.getTrangThai() == TrangThaiTBT.DA_HUY
                || tb.getTrangThai() == TrangThaiTBT.DA_THU_DU) {
            throw new IllegalArgumentException("Thông báo chưa phát hành, đã hủy hoặc đã thu đủ — không cần mã QR.");
        }
        BigDecimal conPhaiTra = tb.getTongTien().subtract(tb.getSoDaThu());
        return toaNhaService.taoMaVietQR(conPhaiTra, maThongBaoTien);
    }

    /** ThongBaoTien.danhDauQuaHan (Bộ hẹn giờ — gọi tay ở đây, tự động hóa thuộc GĐ8). */
    @Transactional
    public void danhDauQuaHan(String maThongBaoTien, LocalDate ngay) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        if (!ngay.isAfter(tb.getHanThanhToan())) {
            throw new IllegalArgumentException("Chưa quá hạn thanh toán.");
        }
        if (tb.getTrangThai() != TrangThaiTBT.DA_PHAT_HANH && tb.getTrangThai() != TrangThaiTBT.DA_THU_MOT_PHAN) {
            throw new IllegalArgumentException("Chỉ đánh dấu quá hạn khi đã phát hành hoặc đã thu một phần.");
        }
        tb.setTrangThai(TrangThaiTBT.QUA_HAN);
        thongBaoTienRepository.save(tb);

        if (tb.getHopDong().getMaTaiKhoan() != null) {
            thongBaoService.guiDen(
                    LoaiSuKien.QUA_HAN_TT, "Quá hạn thanh toán",
                    "Thông báo tiền kỳ " + tb.getKyThang() + " của phòng " + tb.getHopDong().getPhong().getMaPhong()
                            + " đã quá hạn thanh toán (" + tb.getHanThanhToan() + ").",
                    "ThongBaoTien:" + maThongBaoTien, List.of(tb.getHopDong().getMaTaiKhoan()));
        }
    }

    /**
     * ThongBaoTien.capNhatTrangThaiThu: soDaThu = Σ PhieuThu HIEU_LUC. 0 → DA_PHAT_HANH (QUA_HAN nếu
     * quá hạn); 0 &lt; soDaThu &lt; tongTien → DA_THU_MOT_PHAN; soDaThu ≥ tongTien → DA_THU_DU.
     * Cũng chính là cách tái tạo "trạng thái trước khi báo" cho BaoThanhToan.tuChoi() mà không cần
     * một cột lưu riêng (sheet 2_ThuocTinh không có cột đó).
     */
    @Transactional
    public void capNhatTrangThaiThu(String maThongBaoTien) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        BigDecimal soDaThu = phieuThuRepository.tongDaThuHieuLuc(maThongBaoTien);
        tb.setSoDaThu(soDaThu);

        if (soDaThu.compareTo(tb.getTongTien()) >= 0) {
            tb.setTrangThai(TrangThaiTBT.DA_THU_DU);
        } else if (soDaThu.signum() > 0) {
            tb.setTrangThai(TrangThaiTBT.DA_THU_MOT_PHAN);
        } else {
            tb.setTrangThai(LocalDate.now().isAfter(tb.getHanThanhToan()) ? TrangThaiTBT.QUA_HAN : TrangThaiTBT.DA_PHAT_HANH);
        }
        thongBaoTienRepository.save(tb);
    }

    /**
     * ThongBaoTien.ghiNhanThu: chủ nhà ghi nhận thu trực tiếp (tiền mặt, hoặc chuyển khoản khách
     * không báo qua hệ thống). maBaoTT luôn NULL (BR-TT-02, BR-TT-03).
     */
    @Transactional
    public PhieuThu ghiNhanThu(
            String maThongBaoTien, BigDecimal soTien, HinhThucTT hinhThuc, String nguoiNop, LocalDate ngayThu, String maTaiKhoanThucHien) {
        ThongBaoTien tb = timHoacNem(maThongBaoTien);
        if (soTien == null || soTien.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        }
        if (tb.getTrangThai() != TrangThaiTBT.DA_PHAT_HANH
                && tb.getTrangThai() != TrangThaiTBT.DA_THU_MOT_PHAN
                && tb.getTrangThai() != TrangThaiTBT.QUA_HAN) {
            throw new IllegalArgumentException(
                    "Chỉ ghi nhận thu cho thông báo chưa thu đủ (đang chờ xác nhận báo chuyển khoản thì xử lý báo đó trước).");
        }

        PhieuThu phieuThu = new PhieuThu();
        LocalDate ngay = ngayThu != null ? ngayThu : LocalDate.now();
        phieuThu.setSoPhieuThu(PhieuThuIdGenerator.sinh(phieuThuRepository, ngay));
        phieuThu.setThongBaoTien(tb);
        phieuThu.setBaoThanhToan(null);
        phieuThu.setNgayThu(ngay);
        phieuThu.setNguoiNop(nguoiNop != null ? nguoiNop : tb.getHopDong().getKhachThue().getHoTen());
        phieuThu.setSoTien(soTien);
        phieuThu.setHinhThuc(hinhThuc);
        phieuThu = phieuThuRepository.save(phieuThu);

        capNhatTrangThaiThu(maThongBaoTien);

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien, "PhieuThu", phieuThu.getSoPhieuThu(), "TAO",
                "Ghi nhận thu trực tiếp " + soTien + " (" + hinhThuc + ") cho thông báo \"" + maThongBaoTien + "\".");
        return phieuThu;
    }
}
