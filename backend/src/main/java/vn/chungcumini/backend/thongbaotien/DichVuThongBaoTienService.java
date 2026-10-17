package vn.chungcumini.backend.thongbaotien;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.banggia.BangGiaService;
import vn.chungcumini.backend.domain.ChiSo;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.HopDongDichVu;
import vn.chungcumini.backend.domain.LoaiCongTo;
import vn.chungcumini.backend.domain.LoaiKhoan;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;
import vn.chungcumini.backend.domain.LoaiThongBao;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.hopdong.HopDongDichVuService;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.repository.ChiSoRepository;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;
import vn.chungcumini.backend.repository.ToaNhaRepository;

/**
 * DichVuThongBaoTien — lớp dịch vụ không lưu CSDL (sheet 1_ThucThe). Chưa cộng phí phát sinh từ
 * SuCo (BR-SC-03) vì SuCo chưa cài (GĐ6); noCu/BR-TT-03 đã tính được vì không phụ thuộc SuCo.
 */
@Service
public class DichVuThongBaoTienService {

    private final HopDongRepository hopDongRepository;
    private final ChiSoRepository chiSoRepository;
    private final ThongBaoTienRepository thongBaoTienRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final HopDongService hopDongService;
    private final HopDongDichVuService hopDongDichVuService;
    private final BangGiaService bangGiaService;
    private final ThongBaoTienService thongBaoTienService;

    public DichVuThongBaoTienService(
            HopDongRepository hopDongRepository,
            ChiSoRepository chiSoRepository,
            ThongBaoTienRepository thongBaoTienRepository,
            ToaNhaRepository toaNhaRepository,
            HopDongService hopDongService,
            HopDongDichVuService hopDongDichVuService,
            BangGiaService bangGiaService,
            ThongBaoTienService thongBaoTienService) {
        this.hopDongRepository = hopDongRepository;
        this.chiSoRepository = chiSoRepository;
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.hopDongService = hopDongService;
        this.hopDongDichVuService = hopDongDichVuService;
        this.bangGiaService = bangGiaService;
        this.thongBaoTienService = thongBaoTienService;
    }

    /** DichVuThongBaoTien.lapThongBaoHangLoat (BR-HD-01, BR-HD-02, BR-HD-03, BR-TT-03). */
    @Transactional
    public LapThongBaoHangLoatResult lapThongBaoHangLoat(String kyThang, String maTaiKhoanThucHien) {
        YearMonth thangHienTai = YearMonth.parse(kyThang);
        LocalDate ngayLap = LocalDate.now();

        List<String> daLap = new ArrayList<>();
        List<String> phongThieuChiSo = new ArrayList<>();

        for (HopDong hd : hopDongRepository.findByTrangThaiWithPhong(TrangThaiHD.HIEU_LUC)) {
            boolean daCoRoi = thongBaoTienRepository.findByHopDong_MaHopDongAndTrangThaiNot(hd.getMaHopDong(), TrangThaiTBT.DA_HUY)
                    .stream()
                    .anyMatch(t -> t.getLoaiThongBao() == LoaiThongBao.HANG_THANG && t.getKyThang().equals(kyThang));
            if (daCoRoi) continue;

            var dien = chiSoRepository.findByHopDong_MaHopDongAndLoaiCongToAndKyThangAndLoaiGhi(
                    hd.getMaHopDong(), LoaiCongTo.DIEN, kyThang, vn.chungcumini.backend.domain.LoaiGhi.HANG_THANG);
            var nuoc = hd.getPhong().isCoCongToNuoc()
                    ? chiSoRepository.findByHopDong_MaHopDongAndLoaiCongToAndKyThangAndLoaiGhi(
                            hd.getMaHopDong(), LoaiCongTo.NUOC, kyThang, vn.chungcumini.backend.domain.LoaiGhi.HANG_THANG)
                    : java.util.Optional.<ChiSo>empty();
            if (dien.isEmpty() || (hd.getPhong().isCoCongToNuoc() && nuoc.isEmpty())) {
                phongThieuChiSo.add(hd.getPhong().getMaPhong());
                continue;
            }

            BigDecimal tyLeNgayO = tyLeNgayOTrongThang(hd, thangHienTai);
            BigDecimal noCu = thongBaoTienRepository.timTruocKy(hd.getMaHopDong(), kyThang).stream()
                    .findFirst()
                    .map(t -> t.getTongTien().subtract(t.getSoDaThu()).max(BigDecimal.ZERO))
                    .orElse(BigDecimal.ZERO);

            LocalDate hanThanhToan = thangHienTai.plusMonths(1).atDay(
                    Math.min(toaNhaRepository.findById("TOA01").orElseThrow().getHanThanhToan(), thangHienTai.plusMonths(1).lengthOfMonth()));

            ThongBaoTien tb = thongBaoTienService.taoNhap(hd, LoaiThongBao.HANG_THANG, kyThang, hanThanhToan, noCu);

            ChiSo csDien = dien.get();
            thongBaoTienService.themDong(
                    tb.getMaThongBaoTien(), LoaiKhoanTBT.DIEN, "Điện kỳ " + kyThang, null, null,
                    BigDecimal.valueOf(csDien.getChiSoMoi() - csDien.getChiSoCu()),
                    bangGiaService.layDonGiaTai(LoaiKhoan.DIEN, ngayLap), BigDecimal.ONE, csDien.getMaChiSo(), null);

            if (hd.getPhong().isCoCongToNuoc()) {
                ChiSo csNuoc = nuoc.get();
                thongBaoTienService.themDong(
                        tb.getMaThongBaoTien(), LoaiKhoanTBT.NUOC, "Nước kỳ " + kyThang, null, null,
                        BigDecimal.valueOf(csNuoc.getChiSoMoi() - csNuoc.getChiSoCu()),
                        bangGiaService.layDonGiaTai(LoaiKhoan.NUOC, ngayLap), BigDecimal.ONE, csNuoc.getMaChiSo(), null);
            } else {
                thongBaoTienService.themDong(
                        tb.getMaThongBaoTien(), LoaiKhoanTBT.NUOC, "Nước theo người kỳ " + kyThang,
                        thangHienTai.atDay(1), thangHienTai.atEndOfMonth(),
                        BigDecimal.valueOf(hopDongService.demSoNguoiO(hd.getMaHopDong())),
                        bangGiaService.layDonGiaTai(LoaiKhoan.NUOC_THEO_NGUOI, ngayLap), tyLeNgayO, null, null);
            }

            for (HopDongDichVu hddv : hopDongDichVuService.layTheoHopDong(hd.getMaHopDong())) {
                thongBaoTienService.themDong(
                        tb.getMaThongBaoTien(), LoaiKhoanTBT.DICH_VU, hddv.getBangGia().getTenKhoan() + " kỳ " + kyThang,
                        thangHienTai.atDay(1), thangHienTai.atEndOfMonth(),
                        BigDecimal.valueOf(hddv.getSoLuong()), hddv.getMucPhi(), tyLeNgayO, null, null);
            }

            YearMonth thangSau = thangHienTai.plusMonths(1);
            LocalDate tuNgayPhong = thangSau.atDay(1);
            LocalDate denNgayPhong = thangSau.atEndOfMonth();
            BigDecimal tienPhong = hopDongService.tinhTienPhong(hd.getMaHopDong(), tuNgayPhong, denNgayPhong);
            thongBaoTienService.themDong(
                    tb.getMaThongBaoTien(), LoaiKhoanTBT.TIEN_PHONG, "Tiền phòng tháng " + thangSau,
                    tuNgayPhong, denNgayPhong, BigDecimal.ONE, tienPhong, BigDecimal.ONE, null, null);

            daLap.add(tb.getMaThongBaoTien());
        }

        return new LapThongBaoHangLoatResult(daLap, phongThieuChiSo);
    }

    /** DichVuThongBaoTien.phatHanhHangLoat. */
    @Transactional
    public PhatHanhHangLoatResult phatHanhHangLoat(List<String> dsMaThongBaoTien, String maTaiKhoanThucHien) {
        List<String> thanhCong = new ArrayList<>();
        List<String> canhBaoHetHan = new ArrayList<>();
        List<String> loi = new ArrayList<>();
        for (String ma : dsMaThongBaoTien) {
            try {
                boolean canhBao = thongBaoTienService.phatHanh(ma, maTaiKhoanThucHien);
                thanhCong.add(ma);
                if (canhBao) canhBaoHetHan.add(ma);
            } catch (IllegalArgumentException e) {
                loi.add(ma + ": " + e.getMessage());
            }
        }
        return new PhatHanhHangLoatResult(thanhCong, canhBaoHetHan, loi);
    }

    /** DichVuThongBaoTien.bangTongHop: bảng tổng hợp cả tòa để rà soát trước khi phát hành. */
    @Transactional
    public List<TongHopKyResponse> bangTongHop(String kyThang) {
        List<TongHopKyResponse> ketQua = new ArrayList<>();
        for (ThongBaoTien tb : thongBaoTienRepository.timTheoKyThang(kyThang)) {
            BigDecimal dien = BigDecimal.ZERO, nuoc = BigDecimal.ZERO, dichVu = BigDecimal.ZERO;
            BigDecimal tienPhong = BigDecimal.ZERO, phatSinh = BigDecimal.ZERO;
            for (var dong : thongBaoTienService.layDongTheoThongBao(tb.getMaThongBaoTien())) {
                switch (dong.getLoaiKhoan()) {
                    case DIEN -> dien = dien.add(dong.getThanhTien());
                    case NUOC -> nuoc = nuoc.add(dong.getThanhTien());
                    case DICH_VU -> dichVu = dichVu.add(dong.getThanhTien());
                    case TIEN_PHONG -> tienPhong = tienPhong.add(dong.getThanhTien());
                    case PHI_PHAT_SINH -> phatSinh = phatSinh.add(dong.getThanhTien());
                    default -> {
                    }
                }
            }
            ketQua.add(new TongHopKyResponse(
                    tb.getMaThongBaoTien(), tb.getHopDong().getPhong().getMaPhong(),
                    dien, nuoc, dichVu, tienPhong, phatSinh, tb.getNoCu(), tb.getTongTien(), tb.getTrangThai()));
        }
        return ketQua;
    }

    /** Tỷ lệ ngày ở trong tháng = số ngày HĐ hiệu lực trong tháng / số ngày của tháng. */
    private static BigDecimal tyLeNgayOTrongThang(HopDong hopDong, YearMonth thang) {
        LocalDate dauThang = thang.atDay(1);
        LocalDate cuoiThang = thang.atEndOfMonth();
        LocalDate batDau = maxDate(dauThang, hopDong.getNgayBatDau());
        LocalDate ketThuc = minDate(cuoiThang, hopDong.getNgayHetHan());
        if (batDau.isAfter(ketThuc)) return BigDecimal.ZERO;
        long soNgay = ketThuc.toEpochDay() - batDau.toEpochDay() + 1;
        return BigDecimal.valueOf(soNgay).divide(BigDecimal.valueOf(thang.lengthOfMonth()), 6, RoundingMode.HALF_UP);
    }

    private static LocalDate maxDate(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalDate minDate(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }
}
