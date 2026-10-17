package vn.chungcumini.backend.khoitao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.BangGia;
import vn.chungcumini.backend.domain.ChiSo;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.HopDongDichVu;
import vn.chungcumini.backend.domain.KhachThue;
import vn.chungcumini.backend.domain.LoaiCongTo;
import vn.chungcumini.backend.domain.LoaiGhi;
import vn.chungcumini.backend.domain.LoaiThongBao;
import vn.chungcumini.backend.domain.NguoiO;
import vn.chungcumini.backend.domain.Phong;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiPhong;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.khachthue.KhachThueService;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.repository.BangGiaRepository;
import vn.chungcumini.backend.repository.ChiSoRepository;
import vn.chungcumini.backend.repository.HopDongDichVuRepository;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.NguoiORepository;
import vn.chungcumini.backend.repository.PhongRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;
import vn.chungcumini.backend.taikhoan.TaiKhoanService;
import vn.chungcumini.backend.taikhoan.TaoTaiKhoanKhachResult;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;

/**
 * DichVuKhoiTao — nhập dữ liệu ban đầu của tòa đang vận hành (P00 bước 6, CN-09.04). Chạy 1 lần khi
 * bàn giao. Nhận theo cấu trúc gộp mỗi hợp đồng một mục (KhoiTaoHopDongItem) thay vì các danh sách
 * rời dsKhach/dsNguoiO/dsHopDong/dsDichVu/dsChiSo/dsNoTon cần khóa tạm để nối — tương đương nội dung,
 * chắc chắn đúng liên kết hơn.
 */
@Service
public class DichVuKhoiTaoService {

    private final KhachThueService khachThueService;
    private final PhongRepository phongRepository;
    private final HopDongRepository hopDongRepository;
    private final HopDongDichVuRepository hopDongDichVuRepository;
    private final BangGiaRepository bangGiaRepository;
    private final NguoiORepository nguoiORepository;
    private final ChiSoRepository chiSoRepository;
    private final ThongBaoTienRepository thongBaoTienRepository;
    private final ThongBaoTienService thongBaoTienService;
    private final HopDongService hopDongService;
    private final TaiKhoanService taiKhoanService;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public DichVuKhoiTaoService(
            KhachThueService khachThueService,
            PhongRepository phongRepository,
            HopDongRepository hopDongRepository,
            HopDongDichVuRepository hopDongDichVuRepository,
            BangGiaRepository bangGiaRepository,
            NguoiORepository nguoiORepository,
            ChiSoRepository chiSoRepository,
            ThongBaoTienRepository thongBaoTienRepository,
            ThongBaoTienService thongBaoTienService,
            HopDongService hopDongService,
            TaiKhoanService taiKhoanService,
            NhatKyThaoTacService nhatKyThaoTacService) {
        this.khachThueService = khachThueService;
        this.phongRepository = phongRepository;
        this.hopDongRepository = hopDongRepository;
        this.hopDongDichVuRepository = hopDongDichVuRepository;
        this.bangGiaRepository = bangGiaRepository;
        this.nguoiORepository = nguoiORepository;
        this.chiSoRepository = chiSoRepository;
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.thongBaoTienService = thongBaoTienService;
        this.hopDongService = hopDongService;
        this.taiKhoanService = taiKhoanService;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    /**
     * DichVuKhoiTao.nhapDuLieuDangVanHanh: tạo KhachThue, NguoiO, HopDong HIEU_LUC (bỏ qua bước thu
     * tiền ký HĐ), HopDongDichVu, Phong → DANG_THUE, ChiSo DAU_KY = chỉ số gần nhất; nợ tồn đưa vào
     * noCu của thông báo đầu tiên (qua cơ chế BR-TT-03 có sẵn — tạo 1 thông báo kỳ trước mang đúng số
     * nợ, để lapThongBaoHangLoat() của kỳ đầu tiên thật tự cuốn vào noCu). Bước cuối: cấp tài khoản
     * khách cho từng HĐ, trả danh sách mật khẩu tạm.
     */
    @Transactional
    public List<KhoiTaoKetQuaItem> nhapDuLieuDangVanHanh(List<KhoiTaoHopDongItem> dsHopDong, String maTaiKhoanThucHien) {
        List<KhoiTaoKetQuaItem> ketQua = new ArrayList<>();
        for (KhoiTaoHopDongItem item : dsHopDong) {
            ketQua.add(nhapMotHopDong(item, maTaiKhoanThucHien));
        }
        return ketQua;
    }

    private KhoiTaoKetQuaItem nhapMotHopDong(KhoiTaoHopDongItem item, String maTaiKhoanThucHien) {
        Phong phong = phongRepository.findById(item.maPhong())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng \"" + item.maPhong() + "\"."));
        if (phong.getTrangThai() != TrangThaiPhong.TRONG) {
            throw new IllegalArgumentException("Phòng \"" + item.maPhong() + "\" không ở trạng thái Trống.");
        }
        if (!hopDongService.kiemTraThoiHan(item.ngayBatDau(), item.ngayHetHan())) {
            throw new IllegalArgumentException("Thời hạn hợp đồng tối thiểu 3 tháng.");
        }

        KhachThue khach = khachThueService.capNhatHoSo(
                item.khach().soDienThoai(), item.khach().hoTen(), item.khach().ngaySinh(), item.khach().soCCCD(),
                item.khach().anhCCCD(), item.khach().queQuan(), item.khach().noiLamViec());

        HopDong hopDong = new HopDong();
        hopDong.setMaHopDong("HD" + item.ngayBatDau().toString().replace("-", "") + phong.getMaPhong());
        hopDong.setPhong(phong);
        hopDong.setKhachThue(khach);
        hopDong.setNgayBatDau(item.ngayBatDau());
        hopDong.setNgayHetHan(item.ngayHetHan());
        hopDong.setGiaThue(item.giaThue());
        hopDong.setTienCoc(item.tienCoc());
        hopDong.setTrangThai(TrangThaiHD.HIEU_LUC);
        hopDong.setNgayNhanPhong(item.ngayBatDau());
        hopDong = hopDongRepository.save(hopDong);

        phong.setTrangThai(TrangThaiPhong.DANG_THUE);
        phongRepository.save(phong);

        if (item.dsDichVu() != null) {
            for (KhoiTaoDichVuItem dv : item.dsDichVu()) {
                BangGia khoan = bangGiaRepository.findById(dv.maKhoan())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoản phí \"" + dv.maKhoan() + "\"."));
                HopDongDichVu hddv = new HopDongDichVu();
                hddv.setHopDong(hopDong);
                hddv.setBangGia(khoan);
                hddv.setSoLuong(dv.soLuong() != null ? dv.soLuong() : 1);
                hddv.setMucPhi(khoan.getDonGia());
                hddv.setNgayApDung(item.ngayBatDau());
                hopDongDichVuRepository.save(hddv);
            }
        }

        List<NguoiO> dsNguoiOMoi = new ArrayList<>();
        if (item.dsNguoiO() != null) {
            for (KhoiTaoNguoiOItem n : item.dsNguoiO()) {
                NguoiO nguoiO = new NguoiO();
                nguoiO.setMaNguoiO("NO" + String.format("%06d", nguoiORepository.count() + 1));
                nguoiO.setHopDong(hopDong);
                nguoiO.setHoTen(n.hoTen());
                nguoiO.setSoCCCD(n.soCCCD());
                nguoiO.setSoDienThoai(n.soDienThoai());
                nguoiO.setNgayVao(n.ngayVao() != null ? n.ngayVao() : item.ngayBatDau());
                dsNguoiOMoi.add(nguoiORepository.save(nguoiO));
            }
        }

        String kyHienTai = YearMonth.now().toString();
        ghiChiSoDauKy(hopDong, LoaiCongTo.DIEN, item.chiSoDien(), kyHienTai);
        if (phong.isCoCongToNuoc()) {
            if (item.chiSoNuoc() == null) {
                throw new IllegalArgumentException("Phòng \"" + item.maPhong() + "\" có công tơ nước riêng — bắt buộc chỉ số nước.");
            }
            ghiChiSoDauKy(hopDong, LoaiCongTo.NUOC, item.chiSoNuoc(), kyHienTai);
        }

        if (item.noTon() != null && item.noTon().signum() > 0) {
            ghiNoTon(hopDong, item.noTon(), kyHienTai);
        }

        NguoiO nguoiOCapTK = null;
        if (item.viTriNguoiOCapTaiKhoan() != null) {
            int viTri = item.viTriNguoiOCapTaiKhoan();
            if (viTri < 0 || viTri >= dsNguoiOMoi.size()) {
                throw new IllegalArgumentException("viTriNguoiOCapTaiKhoan \"" + viTri + "\" không hợp lệ cho danh sách người ở của HĐ \""
                        + hopDong.getMaHopDong() + "\".");
            }
            nguoiOCapTK = dsNguoiOMoi.get(viTri);
        }
        TaoTaiKhoanKhachResult taiKhoan = taiKhoanService.taoTaiKhoanKhach(hopDong, nguoiOCapTK, maTaiKhoanThucHien);

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien, "HopDong", hopDong.getMaHopDong(), "TAO",
                "Nhập dữ liệu đầu kỳ (tòa đang vận hành). Tài khoản khách: " + taiKhoan.tenDangNhap() + ", mật khẩu tạm: "
                        + taiKhoan.matKhauTam() + ".");

        return new KhoiTaoKetQuaItem(hopDong.getMaHopDong(), phong.getMaPhong(), taiKhoan.maTaiKhoan(), taiKhoan.tenDangNhap(), taiKhoan.matKhauTam());
    }

    private void ghiChiSoDauKy(HopDong hopDong, LoaiCongTo loaiCongTo, KhoiTaoChiSoItem chiSoItem, String kyThang) {
        ChiSo chiSo = new ChiSo();
        chiSo.setMaChiSo("CS" + String.format("%06d", chiSoRepository.count() + 1));
        chiSo.setHopDong(hopDong);
        chiSo.setLoaiCongTo(loaiCongTo);
        chiSo.setKyThang(kyThang);
        chiSo.setLoaiGhi(LoaiGhi.DAU_KY);
        chiSo.setNgayGhi(LocalDate.now());
        chiSo.setChiSoCu(chiSoItem.chiSoGanNhat());
        chiSo.setChiSoMoi(chiSoItem.chiSoGanNhat());
        chiSo.setAnhCongTo(chiSoItem.anhCongTo());
        chiSoRepository.save(chiSo);
    }

    /** BR-TT-03: tạo 1 thông báo (đã phát hành, kỳ trước) mang đúng số nợ tồn để lần lập thông báo đầu tiên tự cuốn vào noCu. */
    private void ghiNoTon(HopDong hopDong, BigDecimal noTon, String kyHienTai) {
        String kyTruoc = YearMonth.parse(kyHienTai).minusMonths(1).toString();
        ThongBaoTien tbNoTon = thongBaoTienService.taoNhap(hopDong, LoaiThongBao.KY_HOP_DONG, kyTruoc, hopDong.getNgayBatDau(), BigDecimal.ZERO);
        tbNoTon.setTrangThai(TrangThaiTBT.DA_PHAT_HANH);
        tbNoTon.setNgayPhatHanh(hopDong.getNgayBatDau());
        tbNoTon.setTongTien(noTon);
        thongBaoTienRepository.save(tbNoTon);
    }
}
