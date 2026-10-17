package vn.chungcumini.backend.baocao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.DongThongBaoTien;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;
import vn.chungcumini.backend.domain.Phong;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiPhong;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.hopdong.HopDongResponse;
import vn.chungcumini.backend.phong.PhongService;
import vn.chungcumini.backend.repository.DongThongBaoTienRepository;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.PhongRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;

/**
 * DichVuBaoCao — các chỉ số báo cáo cho chủ nhà (UC08.05–UC08.09). suCoDangMo() (module SuCo — GĐ6,
 * nhánh riêng PR #9) chưa gộp vào nhánh này — bổ sung khi gộp vào main.
 */
@Service
public class DichVuBaoCaoService {

    private static final Set<LoaiKhoanTBT> KHOAN_DOANH_THU =
            Set.of(LoaiKhoanTBT.TIEN_PHONG, LoaiKhoanTBT.DIEN, LoaiKhoanTBT.NUOC, LoaiKhoanTBT.DICH_VU, LoaiKhoanTBT.PHI_PHAT_SINH);

    private final PhongRepository phongRepository;
    private final PhongService phongService;
    private final HopDongRepository hopDongRepository;
    private final ThongBaoTienRepository thongBaoTienRepository;
    private final DongThongBaoTienRepository dongThongBaoTienRepository;

    public DichVuBaoCaoService(
            PhongRepository phongRepository,
            PhongService phongService,
            HopDongRepository hopDongRepository,
            ThongBaoTienRepository thongBaoTienRepository,
            DongThongBaoTienRepository dongThongBaoTienRepository) {
        this.phongRepository = phongRepository;
        this.phongService = phongService;
        this.hopDongRepository = hopDongRepository;
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.dongThongBaoTienRepository = dongThongBaoTienRepository;
    }

    /** DichVuBaoCao.tinhTrangPhong: số phòng theo trạng thái, sơ đồ tòa theo tầng (thời gian thực). */
    public TinhTrangPhongResponse tinhTrangPhong() {
        var soDo = phongService.layDanhSach();
        Map<TrangThaiPhong, Long> soLuong = new EnumMap<>(TrangThaiPhong.class);
        for (TrangThaiPhong t : TrangThaiPhong.values()) {
            soLuong.put(t, 0L);
        }
        for (Phong p : phongRepository.findAll()) {
            soLuong.merge(p.getTrangThai(), 1L, Long::sum);
        }
        return new TinhTrangPhongResponse(soLuong, soDo);
    }

    /**
     * DichVuBaoCao.tyLeLapDay: số phòng Đang thuê / số phòng khai thác (không gồm Ngừng khai thác).
     * Hệ thống không lưu lịch sử trạng thái phòng theo ngày, nên luôn tính theo tình trạng hiện tại
     * (chỉ chính xác "tại cuối tháng" khi kyThang là tháng hiện tại).
     */
    public BigDecimal tyLeLapDay(String kyThang) {
        List<Phong> tatCa = phongRepository.findAll();
        long khaiThac = tatCa.stream().filter(p -> p.getTrangThai() != TrangThaiPhong.NGUNG_KHAI_THAC).count();
        if (khaiThac == 0) {
            return BigDecimal.ZERO;
        }
        long dangThue = tatCa.stream().filter(p -> p.getTrangThai() == TrangThaiPhong.DANG_THUE).count();
        return BigDecimal.valueOf(dangThue).divide(BigDecimal.valueOf(khaiThac), 4, RoundingMode.HALF_UP);
    }

    /** DichVuBaoCao.doanhThu: Σ thanhTien của thông báo đã phát hành (không DA_HUY), nhóm theo khoản; không gồm TIEN_COC. */
    public Map<LoaiKhoanTBT, BigDecimal> doanhThu(String kyThang) {
        Map<LoaiKhoanTBT, BigDecimal> ketQua = new EnumMap<>(LoaiKhoanTBT.class);
        for (LoaiKhoanTBT loai : KHOAN_DOANH_THU) {
            ketQua.put(loai, BigDecimal.ZERO);
        }
        for (ThongBaoTien tb : thongBaoTienRepository.timTheoKyThang(kyThang)) {
            if (tb.getTrangThai() == TrangThaiTBT.NHAP || tb.getTrangThai() == TrangThaiTBT.DA_HUY) {
                continue;
            }
            for (DongThongBaoTien dong : dongThongBaoTienRepository.findByThongBaoTien_MaThongBaoTien(tb.getMaThongBaoTien())) {
                if (KHOAN_DOANH_THU.contains(dong.getLoaiKhoan())) {
                    ketQua.merge(dong.getLoaiKhoan(), dong.getThanhTien(), BigDecimal::add);
                }
            }
        }
        return ketQua;
    }

    /** DichVuBaoCao.tyLeThu: đã thu / phải thu của các thông báo đã phát hành trong khoảng ngày phát hành. */
    public BigDecimal tyLeThu(LocalDate tuNgay, LocalDate denNgay) {
        List<ThongBaoTien> ds = thongBaoTienRepository.timDaPhatHanhTrongKhoang(tuNgay, denNgay);
        BigDecimal phaiThu = ds.stream().map(ThongBaoTien::getTongTien).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (phaiThu.signum() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal daThu = ds.stream().map(ThongBaoTien::getSoDaThu).reduce(BigDecimal.ZERO, BigDecimal::add);
        return daThu.divide(phaiThu, 4, RoundingMode.HALF_UP);
    }

    /** DichVuBaoCao.congNoQuaHan: thông báo QUA_HAN — phòng, số còn nợ, số ngày quá hạn. */
    public List<CongNoQuaHanItem> congNoQuaHan(LocalDate ngay) {
        List<CongNoQuaHanItem> ketQua = new ArrayList<>();
        for (ThongBaoTien tb : thongBaoTienRepository.findByTrangThaiIn(List.of(TrangThaiTBT.QUA_HAN))) {
            BigDecimal conNo = tb.getTongTien().subtract(tb.getSoDaThu());
            long soNgay = ngay.toEpochDay() - tb.getHanThanhToan().toEpochDay();
            ketQua.add(new CongNoQuaHanItem(tb.getHopDong().getPhong().getMaPhong(), tb.getMaThongBaoTien(), conNo, soNgay));
        }
        return ketQua;
    }

    /**
     * DichVuBaoCao.cocDangGiu (BR-TT-04): Σ HopDong.tienCoc của HĐ chưa có quyết toán HOAN_TAT. Module
     * QuyetToan (GĐ5, nhánh riêng PR #8) chưa gộp vào nhánh này; HopDong.thanhLy() chỉ chuyển khỏi
     * HIEU_LUC sau khi QuyetToan đã HOAN_TAT (xem GĐ5), nên "HĐ đang HIEU_LUC" ở main hiện tại tương
     * đương chính xác với "chưa có quyết toán HOAN_TAT".
     */
    public List<CocDangGiuItem> cocDangGiu() {
        return hopDongRepository.findByTrangThaiWithPhong(TrangThaiHD.HIEU_LUC).stream()
                .map(hd -> new CocDangGiuItem(hd.getPhong().getMaPhong(), hd.getMaHopDong(), hd.getTienCoc()))
                .collect(Collectors.toList());
    }

    /** DichVuBaoCao.hopDongSapHetHan: HĐ hiệu lực hết hạn trong N ngày tới. */
    public List<HopDongResponse> hopDongSapHetHan(int soNgay) {
        LocalDate homNay = LocalDate.now();
        return hopDongRepository.findAllChiTiet().stream()
                .filter((HopDong hd) -> hd.getTrangThai() == TrangThaiHD.HIEU_LUC
                        && !hd.getNgayHetHan().isBefore(homNay)
                        && !hd.getNgayHetHan().isAfter(homNay.plusDays(soNgay)))
                .map(HopDongResponse::tu)
                .toList();
    }
}
