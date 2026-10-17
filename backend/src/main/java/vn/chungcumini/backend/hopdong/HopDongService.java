package vn.chungcumini.backend.hopdong;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.BangGia;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.HopDongDichVu;
import vn.chungcumini.backend.domain.KhachThue;
import vn.chungcumini.backend.domain.LoaiKetThuc;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;
import vn.chungcumini.backend.domain.LoaiThayDoi;
import vn.chungcumini.backend.domain.LoaiThongBao;
import vn.chungcumini.backend.domain.NguoiO;
import vn.chungcumini.backend.domain.Phong;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiPhong;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.phong.PhongService;
import vn.chungcumini.backend.repository.BangGiaRepository;
import vn.chungcumini.backend.repository.HopDongDichVuRepository;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.KhachThueRepository;
import vn.chungcumini.backend.repository.LichSuHopDongRepository;
import vn.chungcumini.backend.repository.NguoiORepository;
import vn.chungcumini.backend.repository.PhongRepository;
import vn.chungcumini.backend.taikhoan.TaiKhoanService;
import vn.chungcumini.backend.taikhoan.TaoTaiKhoanKhachResult;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;

/**
 * thanhLy() và phần kiểm tra "đã thu đủ tiền ký HĐ" của xacNhanNhanPhong() CHƯA cài — cần module
 * QuyetToan (GĐ5) và PhieuThu (GĐ4, để soDaThu của ThongBaoTien lên được DA_THU_DU). xacNhanNhanPhong()
 * ở đây luôn bắt buộc lyDo thay vì tự kiểm tra trạng thái thu tiền, cho tới khi PhieuThu tồn tại.
 */
@Service
public class HopDongService {

    private final HopDongRepository hopDongRepository;
    private final PhongRepository phongRepository;
    private final KhachThueRepository khachThueRepository;
    private final HopDongDichVuRepository hopDongDichVuRepository;
    private final BangGiaRepository bangGiaRepository;
    private final NguoiORepository nguoiORepository;
    private final LichSuHopDongRepository lichSuHopDongRepository;
    private final ThongBaoTienService thongBaoTienService;
    private final LichSuHopDongService lichSuHopDongService;
    private final PhongService phongService;
    private final TaiKhoanService taiKhoanService;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public HopDongService(
            HopDongRepository hopDongRepository,
            PhongRepository phongRepository,
            KhachThueRepository khachThueRepository,
            HopDongDichVuRepository hopDongDichVuRepository,
            BangGiaRepository bangGiaRepository,
            NguoiORepository nguoiORepository,
            LichSuHopDongRepository lichSuHopDongRepository,
            ThongBaoTienService thongBaoTienService,
            LichSuHopDongService lichSuHopDongService,
            PhongService phongService,
            TaiKhoanService taiKhoanService,
            NhatKyThaoTacService nhatKyThaoTacService) {
        this.hopDongRepository = hopDongRepository;
        this.phongRepository = phongRepository;
        this.khachThueRepository = khachThueRepository;
        this.hopDongDichVuRepository = hopDongDichVuRepository;
        this.bangGiaRepository = bangGiaRepository;
        this.nguoiORepository = nguoiORepository;
        this.lichSuHopDongRepository = lichSuHopDongRepository;
        this.thongBaoTienService = thongBaoTienService;
        this.lichSuHopDongService = lichSuHopDongService;
        this.phongService = phongService;
        this.taiKhoanService = taiKhoanService;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    public HopDong timHoacNem(String maHopDong) {
        return hopDongRepository.timChiTiet(maHopDong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng \"" + maHopDong + "\"."));
    }

    /** HĐ hiệu lực của tài khoản khách đang đăng nhập (Mục 11: khách chỉ thao tác trên phòng mình). */
    public HopDong layHopDongHieuLucCuaTaiKhoan(String maTaiKhoan) {
        return hopDongRepository.findHieuLucByMaTaiKhoan(maTaiKhoan)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không có hợp đồng đang hiệu lực."));
    }

    /** HopDong.kiemTraThoiHan: thời hạn tối thiểu 3 tháng (BR-CT-02). */
    public boolean kiemTraThoiHan(LocalDate ngayBatDau, LocalDate ngayHetHan) {
        return !ngayHetHan.isBefore(ngayBatDau.plusMonths(3).minusDays(1));
    }

    /**
     * HopDong.taoMoi: lưu hợp đồng giấy lên hệ thống (BR-CT-02, BR-CT-03, BR-CT-04).
     * KHÔNG tự gọi lapThongBaoKyHopDong() — cần module ThongBaoTien (GĐ3).
     */
    @Transactional
    public HopDong taoMoi(
            String maPhong,
            String maKhach,
            LocalDate ngayBatDau,
            LocalDate ngayHetHan,
            BigDecimal giaThue,
            BigDecimal tienCoc,
            List<DichVuDangKy> dsDichVu,
            BigDecimal mucPhatTraTruoc,
            String anhHopDong,
            String maTaiKhoanThucHien) {
        Phong phong = phongRepository.findById(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng \"" + maPhong + "\"."));
        if (phong.getTrangThai() != TrangThaiPhong.TRONG) {
            throw new IllegalArgumentException("Phòng \"" + maPhong + "\" không ở trạng thái Trống.");
        }
        if (hopDongRepository.timHopDongHieuLucCua(maPhong).isPresent()) {
            throw new IllegalArgumentException("Phòng \"" + maPhong + "\" đã có hợp đồng chờ nhận phòng hoặc hiệu lực.");
        }
        if (!kiemTraThoiHan(ngayBatDau, ngayHetHan)) {
            throw new IllegalArgumentException("Thời hạn hợp đồng tối thiểu 3 tháng.");
        }
        if (!phongService.kiemTraSoNguoi(maPhong, 1)) {
            throw new IllegalArgumentException("Phòng \"" + maPhong + "\" không đủ chỗ cho người đứng tên.");
        }
        KhachThue khachThue = khachThueRepository.findById(maKhach)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách thuê \"" + maKhach + "\"."));

        HopDong hopDong = new HopDong();
        hopDong.setMaHopDong("HD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + maPhong);
        hopDong.setPhong(phong);
        hopDong.setKhachThue(khachThue);
        hopDong.setNgayBatDau(ngayBatDau);
        hopDong.setNgayHetHan(ngayHetHan);
        hopDong.setGiaThue(giaThue != null ? giaThue : phong.getGiaNiemYet());
        hopDong.setTienCoc(tienCoc != null ? tienCoc : hopDong.getGiaThue());
        hopDong.setMucPhatTraTruoc(mucPhatTraTruoc != null ? mucPhatTraTruoc : BigDecimal.ZERO);
        hopDong.setAnhHopDong(anhHopDong);
        hopDong.setTrangThai(TrangThaiHD.CHO_NHAN_PHONG);
        hopDong = hopDongRepository.save(hopDong);

        if (dsDichVu != null) {
            for (DichVuDangKy dv : dsDichVu) {
                BangGia khoan = bangGiaRepository.findById(dv.maKhoan())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoản phí \"" + dv.maKhoan() + "\"."));
                HopDongDichVu hddv = new HopDongDichVu();
                hddv.setHopDong(hopDong);
                hddv.setBangGia(khoan);
                hddv.setSoLuong(dv.soLuong() != null ? dv.soLuong() : 1);
                hddv.setMucPhi(khoan.getDonGia());
                hddv.setNgayApDung(ngayBatDau);
                hopDongDichVuRepository.save(hddv);
            }
        }

        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "HopDong", hopDong.getMaHopDong(), "TAO", "Lưu hợp đồng giấy lên hệ thống.");
        lapThongBaoKyHopDong(hopDong, maTaiKhoanThucHien);
        return hopDong;
    }

    /**
     * HopDong.lapThongBaoKyHopDong: cọc + tiền phòng từ ngày vào đến hết tháng, phát hành ngay.
     * Tiền điều kiện: trangThai = CHO_NHAN_PHONG (chỉ gọi nội bộ từ taoMoi()).
     */
    private void lapThongBaoKyHopDong(HopDong hopDong, String maTaiKhoanThucHien) {
        LocalDate cuoiThang = hopDong.getNgayBatDau().withDayOfMonth(hopDong.getNgayBatDau().lengthOfMonth());
        BigDecimal tienPhongKyDau = tinhTienPhong(hopDong.getMaHopDong(), hopDong.getNgayBatDau(), cuoiThang);

        var tb = thongBaoTienService.taoNhap(
                hopDong,
                LoaiThongBao.KY_HOP_DONG,
                hopDong.getNgayBatDau().toString().substring(0, 7),
                hopDong.getNgayBatDau(),
                BigDecimal.ZERO);

        thongBaoTienService.themDong(
                tb.getMaThongBaoTien(),
                LoaiKhoanTBT.TIEN_COC,
                "Tiền cọc",
                null, null, BigDecimal.ONE, hopDong.getTienCoc(), BigDecimal.ONE, null, null);
        thongBaoTienService.themDong(
                tb.getMaThongBaoTien(),
                LoaiKhoanTBT.TIEN_PHONG,
                "Tiền phòng từ " + hopDong.getNgayBatDau() + " đến " + cuoiThang,
                hopDong.getNgayBatDau(), cuoiThang, BigDecimal.ONE, tienPhongKyDau, BigDecimal.ONE, null, null);

        thongBaoTienService.phatHanh(tb.getMaThongBaoTien(), maTaiKhoanThucHien);
    }

    /** HopDong.demSoNguoiO: 1 (người đứng tên) + số NguoiO có ngayRa = null. */
    public int demSoNguoiO(String maHopDong) {
        return 1 + nguoiORepository.countByHopDong_MaHopDongAndNgayRaIsNull(maHopDong);
    }

    /** HopDong.themNguoiO (BR-CT-04). */
    @Transactional
    public NguoiO themNguoiO(String maHopDong, String hoTen, String soCCCD, String soDienThoai, LocalDate ngayVao) {
        HopDong hopDong = timHoacNem(maHopDong);
        if (!phongService.kiemTraSoNguoi(hopDong.getPhong().getMaPhong(), demSoNguoiO(maHopDong) + 1)) {
            throw new IllegalArgumentException("Phòng đã đủ số người tối đa.");
        }

        NguoiO nguoiO = new NguoiO();
        nguoiO.setMaNguoiO("NO" + String.format("%06d", nguoiORepository.count() + 1));
        nguoiO.setHopDong(hopDong);
        nguoiO.setHoTen(hoTen);
        nguoiO.setSoCCCD(soCCCD);
        nguoiO.setSoDienThoai(soDienThoai);
        nguoiO.setNgayVao(ngayVao != null ? ngayVao : (hopDong.getNgayNhanPhong() != null ? hopDong.getNgayNhanPhong() : LocalDate.now()));
        nguoiO = nguoiORepository.save(nguoiO);

        if (hopDong.getTrangThai() == TrangThaiHD.HIEU_LUC) {
            lichSuHopDongService.ghiNhan(
                    hopDong, LoaiThayDoi.THEM_NGUOI_O, "nguoiO", null, nguoiO.getHoTen(), LocalDate.now(), null);
        }
        return nguoiO;
    }

    /** HopDong.giaHan: tối thiểu 3 tháng kể từ hạn cũ; bắt buộc ảnh phụ lục. */
    @Transactional
    public HopDong giaHan(String maHopDong, LocalDate ngayHetHanMoi, BigDecimal giaMoi, String anhPhuLuc) {
        HopDong hopDong = timHoacNem(maHopDong);
        if (hopDong.getTrangThai() != TrangThaiHD.HIEU_LUC) {
            throw new IllegalArgumentException("Chỉ gia hạn hợp đồng đang hiệu lực.");
        }
        if (hopDong.getNgayBaoTra() != null) {
            throw new IllegalArgumentException("Hợp đồng đã báo trả, không thể gia hạn.");
        }
        if (ngayHetHanMoi.isBefore(hopDong.getNgayHetHan().plusMonths(3))) {
            throw new IllegalArgumentException("Hạn mới phải cách hạn cũ tối thiểu 3 tháng.");
        }
        if (anhPhuLuc == null || anhPhuLuc.isBlank()) {
            throw new IllegalArgumentException("Bắt buộc ảnh phụ lục khi gia hạn.");
        }

        LocalDate ngayApDung = hopDong.getNgayHetHan().plusDays(1);
        LocalDate ngayHetHanCu = hopDong.getNgayHetHan();
        hopDong.setNgayHetHan(ngayHetHanMoi);
        lichSuHopDongService.ghiNhan(
                hopDong, LoaiThayDoi.GIA_HAN, "ngayHetHan", ngayHetHanCu.toString(), ngayHetHanMoi.toString(), ngayApDung, anhPhuLuc);

        if (giaMoi != null && giaMoi.compareTo(hopDong.getGiaThue()) != 0) {
            String giaCu = hopDong.getGiaThue().toString();
            hopDong.setGiaThue(giaMoi);
            lichSuHopDongService.ghiNhan(
                    hopDong, LoaiThayDoi.DIEU_CHINH_GIA, "giaThue", giaCu, giaMoi.toString(), ngayApDung, anhPhuLuc);
        }
        return hopDongRepository.save(hopDong);
    }

    /** HopDong.dieuChinhGia: ngayApDung phải là ngày 1 của tháng. */
    @Transactional
    public HopDong dieuChinhGia(String maHopDong, BigDecimal giaMoi, LocalDate ngayApDung, String anhPhuLuc) {
        HopDong hopDong = timHoacNem(maHopDong);
        if (hopDong.getTrangThai() != TrangThaiHD.HIEU_LUC) {
            throw new IllegalArgumentException("Chỉ điều chỉnh giá hợp đồng đang hiệu lực.");
        }
        if (ngayApDung.getDayOfMonth() != 1) {
            throw new IllegalArgumentException("Ngày áp dụng phải là ngày 1 của tháng.");
        }
        if (giaMoi == null || giaMoi.signum() <= 0) {
            throw new IllegalArgumentException("Giá thuê mới phải lớn hơn 0.");
        }

        String giaCu = hopDong.getGiaThue().toString();
        hopDong.setGiaThue(giaMoi);
        lichSuHopDongService.ghiNhan(hopDong, LoaiThayDoi.DIEU_CHINH_GIA, "giaThue", giaCu, giaMoi.toString(), ngayApDung, anhPhuLuc);
        return hopDongRepository.save(hopDong);
    }

    /** HopDong.baoTraPhong (BR-TL-01). Trả về true nếu trả trước hạn (sẽ áp mucPhatTraTruoc ở quyết toán — GĐ5). */
    @Transactional
    public boolean baoTraPhong(String maHopDong, LocalDate ngayTraDuKien, LoaiKetThuc loaiKetThuc) {
        HopDong hopDong = timHoacNem(maHopDong);
        if (hopDong.getTrangThai() != TrangThaiHD.HIEU_LUC) {
            throw new IllegalArgumentException("Chỉ báo trả hợp đồng đang hiệu lực.");
        }
        if (ngayTraDuKien.isBefore(LocalDate.now()) || ngayTraDuKien.isAfter(hopDong.getNgayHetHan().plusDays(1))) {
            throw new IllegalArgumentException("Ngày trả dự kiến không hợp lệ.");
        }

        hopDong.setNgayBaoTra(LocalDate.now());
        hopDong.setNgayTraDuKien(ngayTraDuKien);
        hopDong.setLoaiKetThuc(loaiKetThuc);
        hopDongRepository.save(hopDong);
        return ngayTraDuKien.isBefore(hopDong.getNgayHetHan());
    }

    /** HopDong.xacNhanThuHoiChiaKhoa. */
    @Transactional
    public HopDong xacNhanThuHoiChiaKhoa(String maHopDong, String maTaiKhoanThucHien) {
        HopDong hopDong = timHoacNem(maHopDong);
        if (hopDong.getTrangThai() != TrangThaiHD.HIEU_LUC || hopDong.getNgayBaoTra() == null) {
            throw new IllegalArgumentException("Hợp đồng chưa báo trả hoặc không còn hiệu lực.");
        }
        hopDong.setDaThuHoiChiaKhoa(true);
        hopDong = hopDongRepository.save(hopDong);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "HopDong", maHopDong, "SUA", "Xác nhận đã thu hồi chìa khóa.");
        return hopDong;
    }

    /**
     * HopDong.xacNhanNhanPhong (BR-CT-05, BR-DM-04). Vẫn bỏ qua 2 kiểm tra của tiền điều kiện:
     * "có ChiSo đầu kỳ" (ChiSo đã có từ GĐ3 nhưng chữ ký phương thức này chưa nhận tham số chỉ số —
     * để không đổi lại hợp đồng giao diện nhận phòng đã chốt ở GĐ2 trong lúc chỉ làm GĐ3) và "đã thu
     * đủ tiền ký HĐ" (ThongBaoTien.soDaThu luôn 0 vì PhieuThu — GĐ4 — chưa cài, nên không thể lên
     * DA_THU_DU). lyDo luôn bắt buộc để thay thế 2 kiểm tra đó.
     */
    @Transactional
    public HopDong xacNhanNhanPhong(String maHopDong, String lyDo, String maNguoiOCapTK, String maTaiKhoanThucHien) {
        HopDong hopDong = timHoacNem(maHopDong);
        if (hopDong.getTrangThai() != TrangThaiHD.CHO_NHAN_PHONG) {
            throw new IllegalArgumentException("Hợp đồng không ở trạng thái chờ nhận phòng.");
        }
        if (lyDo == null || lyDo.isBlank()) {
            throw new IllegalArgumentException(
                    "Bắt buộc ghi lý do khi xác nhận nhận phòng (chưa có module Phiếu thu để tự kiểm tra đã thu đủ).");
        }

        NguoiO nguoiOCapTK = null;
        if (maNguoiOCapTK != null) {
            nguoiOCapTK = nguoiORepository.findById(maNguoiOCapTK)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người ở \"" + maNguoiOCapTK + "\"."));
        }

        hopDong.setTrangThai(TrangThaiHD.HIEU_LUC);
        hopDong.setLyDoNhanPhongChuaThuDu(lyDo);
        hopDong.setNgayNhanPhong(LocalDate.now());
        hopDong = hopDongRepository.save(hopDong);

        Phong phong = hopDong.getPhong();
        phong.setTrangThai(TrangThaiPhong.DANG_THUE);
        phongRepository.save(phong);

        TaoTaiKhoanKhachResult taiKhoan = taiKhoanService.taoTaiKhoanKhach(hopDong, nguoiOCapTK, maTaiKhoanThucHien);

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien,
                "HopDong",
                maHopDong,
                "SUA",
                "Xác nhận nhận phòng. Tài khoản khách: " + taiKhoan.tenDangNhap() + ", mật khẩu tạm: " + taiKhoan.matKhauTam() + ".");
        return hopDong;
    }

    /**
     * HopDong.tinhTienPhong: cộng theo từng tháng dương lịch, giá áp dụng theo lịch sử LichSuHopDong
     * tại thời điểm từng đoạn. tuNgay/denNgay đều được tính (bao gồm 2 đầu mút); phía gọi tự trừ
     * ngày trả phòng nếu cần theo quy ước "ngày trả không tính" (BR-HD-03).
     */
    public BigDecimal tinhTienPhong(String maHopDong, LocalDate tuNgay, LocalDate denNgay) {
        HopDong hopDong = timHoacNem(maHopDong);
        LocalDate batDau = max(tuNgay, hopDong.getNgayBatDau());
        LocalDate ketThuc = min(denNgay, hopDong.getNgayHetHan());
        if (batDau.isAfter(ketThuc)) {
            return BigDecimal.ZERO;
        }

        List<vn.chungcumini.backend.domain.LichSuHopDong> lichSuGia = lichSuHopDongRepository.findByHopDong_MaHopDong(maHopDong)
                .stream()
                .filter(l -> l.getLoaiThayDoi() == LoaiThayDoi.DIEU_CHINH_GIA)
                .sorted((a, b) -> a.getNgayApDung().compareTo(b.getNgayApDung()))
                .toList();

        BigDecimal tong = BigDecimal.ZERO;
        LocalDate thangHienTai = batDau.withDayOfMonth(1);
        LocalDate thangCuoi = ketThuc.withDayOfMonth(1);
        while (!thangHienTai.isAfter(thangCuoi)) {
            LocalDate dauThang = thangHienTai;
            LocalDate cuoiThang = thangHienTai.withDayOfMonth(thangHienTai.lengthOfMonth());
            LocalDate segBatDau = max(batDau, dauThang);
            LocalDate segKetThuc = min(ketThuc, cuoiThang);
            long soNgay = segKetThuc.toEpochDay() - segBatDau.toEpochDay() + 1;
            int soNgayThang = thangHienTai.lengthOfMonth();

            BigDecimal gia = giaApDungTaiNgay(hopDong, lichSuGia, segBatDau);
            BigDecimal tyLe = BigDecimal.valueOf(soNgay).divide(BigDecimal.valueOf(soNgayThang), 10, java.math.RoundingMode.HALF_UP);
            tong = tong.add(gia.multiply(tyLe));

            thangHienTai = thangHienTai.plusMonths(1);
        }
        return tong;
    }

    private BigDecimal giaApDungTaiNgay(HopDong hopDong, List<vn.chungcumini.backend.domain.LichSuHopDong> lichSuGia, LocalDate ngay) {
        vn.chungcumini.backend.domain.LichSuHopDong apDungGanNhat = null;
        for (var l : lichSuGia) {
            if (!l.getNgayApDung().isAfter(ngay)) {
                apDungGanNhat = l;
            } else {
                break;
            }
        }
        if (apDungGanNhat != null) {
            return new BigDecimal(apDungGanNhat.getGiaTriMoi());
        }
        if (!lichSuGia.isEmpty()) {
            return new BigDecimal(lichSuGia.get(0).getGiaTriCu());
        }
        return hopDong.getGiaThue();
    }

    private static LocalDate max(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private static LocalDate min(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }

    public List<HopDong> layLichSuThueCuaKhach(String maKhach) {
        return hopDongRepository.findByKhachThue_MaKhachOrderByNgayBatDauDesc(maKhach);
    }

    public List<HopDong> layDanhSach() {
        return hopDongRepository.findAllChiTiet();
    }

    /**
     * HopDong.xuatDanhSachTamTru: người đứng tên + người ở cùng mới trong khoảng ngày (Mục 11, UC03.08).
     * "Ngày sinh"/"Quê quán" của người ở cùng để trống — NguoiO không có 2 thuộc tính này trong đặc tả.
     */
    @Transactional
    public byte[] xuatDanhSachTamTru(LocalDate tuNgay, LocalDate denNgay, String maTaiKhoanThucHien) {
        List<HopDong> nguoiDungTen = hopDongRepository.findByNgayNhanPhongBetween(tuNgay, denNgay);
        List<NguoiO> nguoiOCung = nguoiORepository.findByNgayVaoBetween(tuNgay, denNgay);
        byte[] file = TamTruExporter.xuat(nguoiDungTen, nguoiOCung);
        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien,
                "DanhSachTamTru",
                tuNgay.format(DateTimeFormatter.ofPattern("yyMMdd")) + "-" + denNgay.format(DateTimeFormatter.ofPattern("yyMMdd")),
                "XUAT_FILE",
                "Xuất danh sách tạm trú từ " + tuNgay + " đến " + denNgay + ".");
        return file;
    }
}
