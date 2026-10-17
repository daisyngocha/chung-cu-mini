package vn.chungcumini.backend.suco;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.DongThongBaoTien;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;
import vn.chungcumini.backend.domain.LoaiSuCo;
import vn.chungcumini.backend.domain.LoaiThongBao;
import vn.chungcumini.backend.domain.Phong;
import vn.chungcumini.backend.domain.SuCo;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiSuCo;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.domain.VaiTro;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.repository.DongThongBaoTienRepository;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.SuCoRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;
import vn.chungcumini.backend.repository.ToaNhaRepository;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;

/** SuCo — sự cố khách báo và quá trình xử lý (BR-SC-01, BR-SC-02, BR-SC-03). */
@Service
public class SuCoService {

    private final SuCoRepository suCoRepository;
    private final HopDongRepository hopDongRepository;
    private final ThongBaoTienRepository thongBaoTienRepository;
    private final DongThongBaoTienRepository dongThongBaoTienRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final HopDongService hopDongService;
    private final ThongBaoTienService thongBaoTienService;
    private final LichSuSuCoService lichSuSuCoService;

    public SuCoService(
            SuCoRepository suCoRepository,
            HopDongRepository hopDongRepository,
            ThongBaoTienRepository thongBaoTienRepository,
            DongThongBaoTienRepository dongThongBaoTienRepository,
            ToaNhaRepository toaNhaRepository,
            HopDongService hopDongService,
            ThongBaoTienService thongBaoTienService,
            LichSuSuCoService lichSuSuCoService) {
        this.suCoRepository = suCoRepository;
        this.hopDongRepository = hopDongRepository;
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.dongThongBaoTienRepository = dongThongBaoTienRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.hopDongService = hopDongService;
        this.thongBaoTienService = thongBaoTienService;
        this.lichSuSuCoService = lichSuSuCoService;
    }

    public SuCo timHoacNem(String maSuCo) {
        return suCoRepository.timChiTiet(maSuCo)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự cố \"" + maSuCo + "\"."));
    }

    public List<SuCo> layDanhSach() {
        return suCoRepository.findAllChiTiet();
    }

    /** DichVuBaoCao.suCoDangMo / DichVuViecCanLam.layDanhSach: sự cố MOI, DANG_XU_LY, MO_LAI. */
    public List<SuCo> layDangMo() {
        return suCoRepository.findByTrangThaiIn(List.of(TrangThaiSuCo.MOI, TrangThaiSuCo.DANG_XU_LY, TrangThaiSuCo.MO_LAI));
    }

    /** BR-SC-01: khách chỉ xem sự cố của phòng mình, từ ngày nhận phòng của HĐ hiện tại trở đi. */
    public List<SuCo> layTheoTaiKhoanKhach(String maTaiKhoan) {
        HopDong hopDong = hopDongService.layHopDongHieuLucCuaTaiKhoan(maTaiKhoan);
        LocalDateTime tuNgay = hopDong.getNgayNhanPhong() != null ? hopDong.getNgayNhanPhong().atStartOfDay() : hopDong.getNgayBatDau().atStartOfDay();
        return suCoRepository.timTheoPhongTuNgay(hopDong.getPhong().getMaPhong(), tuNgay);
    }

    /** SuCo.baoSuCo (BR-SC-01): khách có HĐ hiệu lực; maPhong luôn lấy từ phòng của khách, không nhận từ client. */
    @Transactional
    public SuCo baoSuCo(String maTaiKhoanKhach, LoaiSuCo loaiSuCo, String moTa, String anh) {
        if (moTa == null || moTa.isBlank()) {
            throw new IllegalArgumentException("Mô tả không được rỗng.");
        }
        HopDong hopDong = hopDongService.layHopDongHieuLucCuaTaiKhoan(maTaiKhoanKhach);
        Phong phong = hopDong.getPhong();

        SuCo suCo = new SuCo();
        suCo.setMaSuCo("SC" + String.format("%06d", suCoRepository.count() + 1));
        suCo.setPhong(phong);
        suCo.setLoaiSuCo(loaiSuCo);
        suCo.setMoTa(moTa);
        suCo.setAnh(anh);
        suCo.setNgayBao(LocalDateTime.now());
        suCo = suCoRepository.save(suCo);

        lichSuSuCoService.ghiNhan(suCo, null, TrangThaiSuCo.MOI, VaiTro.KHACH_THUE, moTa);
        return suCo;
    }

    /** SuCo.tiepNhan: chủ nhà tiếp nhận và hẹn lịch. */
    @Transactional
    public SuCo tiepNhan(String maSuCo, LocalDateTime lichHen, String maTaiKhoanThucHien) {
        SuCo suCo = timHoacNem(maSuCo);
        if (suCo.getTrangThai() != TrangThaiSuCo.MOI && suCo.getTrangThai() != TrangThaiSuCo.MO_LAI) {
            throw new IllegalArgumentException("Chỉ tiếp nhận sự cố Mới hoặc Mở lại.");
        }
        if (lichHen == null || lichHen.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Lịch hẹn xử lý phải từ thời điểm tiếp nhận trở đi.");
        }

        TrangThaiSuCo truoc = suCo.getTrangThai();
        suCo.setTrangThai(TrangThaiSuCo.DANG_XU_LY);
        suCo.setLichHen(lichHen);
        suCo = suCoRepository.save(suCo);

        lichSuSuCoService.ghiNhan(suCo, truoc, TrangThaiSuCo.DANG_XU_LY, VaiTro.CHU_NHA, "Lịch hẹn: " + lichHen);
        return suCo;
    }

    /** SuCo.tuChoi (BR-SC-02): từ chối sự cố không thuộc trách nhiệm chủ nhà. */
    @Transactional
    public SuCo tuChoi(String maSuCo, String lyDo, String maTaiKhoanThucHien) {
        SuCo suCo = timHoacNem(maSuCo);
        if (suCo.getTrangThai() != TrangThaiSuCo.MOI) {
            throw new IllegalArgumentException("Chỉ từ chối sự cố đang ở trạng thái Mới.");
        }
        if (lyDo == null || lyDo.isBlank()) {
            throw new IllegalArgumentException("Bắt buộc ghi lý do từ chối (BR-SC-02).");
        }

        suCo.setTrangThai(TrangThaiSuCo.TU_CHOI);
        suCo.setLyDoTuChoi(lyDo);
        suCo = suCoRepository.save(suCo);

        lichSuSuCoService.ghiNhan(suCo, TrangThaiSuCo.MOI, TrangThaiSuCo.TU_CHOI, VaiTro.CHU_NHA, lyDo);
        return suCo;
    }

    /** SuCo.hoanThanh (BR-SC-03): ghi kết quả xử lý; doKhachGay = true → gọi tinhPhiChoKhach(). */
    @Transactional
    public SuCo hoanThanh(String maSuCo, String ketQua, BigDecimal chiPhi, boolean doKhachGay, String anh, String maTaiKhoanThucHien) {
        SuCo suCo = timHoacNem(maSuCo);
        if (suCo.getTrangThai() != TrangThaiSuCo.DANG_XU_LY) {
            throw new IllegalArgumentException("Chỉ hoàn thành sự cố đang xử lý.");
        }
        if (ketQua == null || ketQua.isBlank()) {
            throw new IllegalArgumentException("Bắt buộc ghi kết quả xử lý.");
        }

        suCo.setTrangThai(TrangThaiSuCo.DA_XU_LY);
        suCo.setKetQua(ketQua);
        suCo.setChiPhi(chiPhi != null ? chiPhi : BigDecimal.ZERO);
        suCo.setDoKhachGay(doKhachGay);
        if (anh != null) {
            suCo.setAnh(anh);
        }
        suCo = suCoRepository.save(suCo);

        lichSuSuCoService.ghiNhan(suCo, TrangThaiSuCo.DANG_XU_LY, TrangThaiSuCo.DA_XU_LY, VaiTro.CHU_NHA, ketQua);

        if (doKhachGay && suCo.getChiPhi().signum() > 0) {
            tinhPhiChoKhach(suCo);
        }
        return suCo;
    }

    /** SuCo.moLai: khách báo chưa khắc phục. */
    @Transactional
    public SuCo moLai(String maSuCo, String ghiChu, String maTaiKhoanKhach) {
        SuCo suCo = timHoacNem(maSuCo);
        if (suCo.getTrangThai() != TrangThaiSuCo.DA_XU_LY) {
            throw new IllegalArgumentException("Chỉ mở lại sự cố đã xử lý.");
        }
        HopDong hopDong = hopDongService.layHopDongHieuLucCuaTaiKhoan(maTaiKhoanKhach);
        if (!hopDong.getPhong().getMaPhong().equals(suCo.getPhong().getMaPhong())) {
            throw new IllegalArgumentException("Sự cố không thuộc phòng của bạn.");
        }

        suCo.setTrangThai(TrangThaiSuCo.MO_LAI);
        suCo = suCoRepository.save(suCo);

        lichSuSuCoService.ghiNhan(suCo, TrangThaiSuCo.DA_XU_LY, TrangThaiSuCo.MO_LAI, VaiTro.KHACH_THUE, ghiChu);
        return suCo;
    }

    /**
     * SuCo.tinhPhiChoKhach (BR-SC-03, BR-HD-01): gắn DongThongBaoTien PHI_PHAT_SINH (maSuCo) vào
     * thông báo NHAP của kỳ hiện tại của HĐ đang hiệu lực tại phòng này, tạo chờ nếu chưa lập. Nếu
     * lapThongBaoHangLoat() của kỳ đó chạy sau, sẽ thấy đã có thông báo (không HUY) nên bỏ qua phòng
     * này — chủ nhà cần tự thêm điện/nước/dịch vụ/tiền phòng còn lại cho kỳ này vào đúng bản NHAP này.
     */
    private DongThongBaoTien tinhPhiChoKhach(SuCo suCo) {
        HopDong hopDong = hopDongRepository
                .findByMaPhongAndTrangThaiIn(suCo.getPhong().getMaPhong(), List.of(TrangThaiHD.HIEU_LUC))
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Phòng \"" + suCo.getPhong().getMaPhong() + "\" không có hợp đồng hiệu lực."));

        String kyThang = YearMonth.now().toString();
        ThongBaoTien tb = thongBaoTienRepository
                .findByHopDong_MaHopDongAndKyThangAndTrangThai(hopDong.getMaHopDong(), kyThang, TrangThaiTBT.NHAP)
                .orElseGet(() -> taoThongBaoNhapChoKy(hopDong, kyThang));

        DongThongBaoTien dong = thongBaoTienService.themDong(
                tb.getMaThongBaoTien(), LoaiKhoanTBT.PHI_PHAT_SINH, "Phí sự cố \"" + suCo.getMaSuCo() + "\" do khách gây ra",
                null, null, BigDecimal.ONE, suCo.getChiPhi(), BigDecimal.ONE, null, null);
        dong.setMaSuCo(suCo.getMaSuCo());
        return dongThongBaoTienRepository.save(dong);
    }

    private ThongBaoTien taoThongBaoNhapChoKy(HopDong hopDong, String kyThang) {
        YearMonth thang = YearMonth.parse(kyThang);
        BigDecimal noCu = thongBaoTienRepository.timTruocKy(hopDong.getMaHopDong(), kyThang).stream()
                .findFirst()
                .map(t -> t.getTongTien().subtract(t.getSoDaThu()).max(BigDecimal.ZERO))
                .orElse(BigDecimal.ZERO);
        LocalDate hanThanhToan = thang.plusMonths(1)
                .atDay(Math.min(toaNhaRepository.findById("TOA01").orElseThrow().getHanThanhToan(), thang.plusMonths(1).lengthOfMonth()));
        return thongBaoTienService.taoNhap(hopDong, LoaiThongBao.HANG_THANG, kyThang, hanThanhToan, noCu);
    }
}
