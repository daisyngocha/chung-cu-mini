package vn.chungcumini.backend.phong;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.Phong;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiPhong;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.PhongRepository;
import vn.chungcumini.backend.repository.TaiSanRepository;
import vn.chungcumini.backend.repository.ToaNhaRepository;

@Service
public class PhongService {

    private final PhongRepository phongRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final HopDongRepository hopDongRepository;
    private final TaiSanRepository taiSanRepository;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public PhongService(
            PhongRepository phongRepository,
            ToaNhaRepository toaNhaRepository,
            HopDongRepository hopDongRepository,
            TaiSanRepository taiSanRepository,
            NhatKyThaoTacService nhatKyThaoTacService) {
        this.phongRepository = phongRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.hopDongRepository = hopDongRepository;
        this.taiSanRepository = taiSanRepository;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    public List<PhongResponse> layDanhSach() {
        return phongRepository.findAll().stream()
                .sorted((a, b) -> {
                    int sTang = Integer.compare(a.getTang(), b.getTang());
                    return sTang != 0 ? sTang : a.getMaPhong().compareTo(b.getMaPhong());
                })
                .map(this::toResponse)
                .toList();
    }

    private PhongResponse toResponse(Phong p) {
        HopDong hd = layHopDongHieuLuc(p.getMaPhong()).orElse(null);
        boolean sapTra = laSapTra(hd);
        return PhongResponse.tu(p, sapTra, sapTra ? hd.getNgayTraDuKien() : null);
    }

    @Transactional
    public PhongResponse taoPhong(TaoPhongRequest req) {
        if (phongRepository.existsById(req.maPhong())) {
            throw new IllegalArgumentException("Mã phòng \"" + req.maPhong() + "\" đã tồn tại.");
        }

        Phong phong = new Phong();
        phong.setMaPhong(req.maPhong());
        phong.setToaNha(toaNhaRepository.findById("TOA01").orElseThrow());
        phong.setTang(req.tang());
        phong.setDienTich(req.dienTich());
        phong.setSoNguoiToiDa(req.soNguoiToiDa());
        phong.setGiaNiemYet(req.giaNiemYet());
        phong.setCoCongToNuoc(req.coCongToNuoc());

        try {
            return toResponse(phongRepository.save(phong));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Mã phòng \"" + req.maPhong() + "\" đã tồn tại.");
        }
    }

    /** BR-DM-02: đổi giá niêm yết không ảnh hưởng giá trên HĐ đang hiệu lực (HopDong.giaThue không bị đổi). */
    @Transactional
    public PhongResponse capNhatGiaNiemYet(String maPhong, java.math.BigDecimal giaMoi) {
        if (giaMoi == null || giaMoi.signum() <= 0) {
            throw new IllegalArgumentException("Giá niêm yết phải lớn hơn 0.");
        }
        Phong phong = timHoacNem(maPhong);
        phong.setGiaNiemYet(giaMoi);
        return toResponse(phongRepository.save(phong));
    }

    /**
     * BR-DM-04: chủ nhà chỉ chuyển Trống <-> Ngừng khai thác; Trống <-> Đang thuê chỉ do hệ thống
     * (HopDong.xacNhanNhanPhong / thanhLy). Không chuyển Ngừng khai thác khi phòng có HĐ chưa thanh lý.
     */
    @Transactional
    public PhongResponse chuyenTrangThai(String maPhong, TrangThaiPhong denTrangThai) {
        Phong phong = timHoacNem(maPhong);
        TrangThaiPhong tu = phong.getTrangThai();

        boolean hopLe = (tu == TrangThaiPhong.TRONG && denTrangThai == TrangThaiPhong.NGUNG_KHAI_THAC)
                || (tu == TrangThaiPhong.NGUNG_KHAI_THAC && denTrangThai == TrangThaiPhong.TRONG);
        if (!hopLe) {
            throw new IllegalArgumentException(
                    "Không thể chuyển phòng từ " + tu + " sang " + denTrangThai + " bằng thao tác này.");
        }
        if (denTrangThai == TrangThaiPhong.NGUNG_KHAI_THAC && !coTheXoaHoacNgungKhaiThac(maPhong)) {
            throw new IllegalArgumentException("Phòng còn hợp đồng chưa thanh lý, không thể ngừng khai thác.");
        }

        phong.setTrangThai(denTrangThai);
        return toResponse(phongRepository.save(phong));
    }

    /** Phong.kiemTraSoNguoi: true nếu soNguoi (1 người đứng tên + số NguoiO đang ở) không vượt soNguoiToiDa (BR-CT-04). */
    public boolean kiemTraSoNguoi(String maPhong, int soNguoi) {
        return soNguoi <= timHoacNem(maPhong).getSoNguoiToiDa();
    }

    /** Phong.layHopDongHieuLuc: HĐ CHO_NHAN_PHONG hoặc HIEU_LUC hiện hành của phòng (BR-CT-03: tối đa 1). */
    public java.util.Optional<HopDong> layHopDongHieuLuc(String maPhong) {
        return hopDongRepository.timHopDongHieuLucCua(maPhong);
    }

    /** Phong.coTheXoa: true chỉ khi phòng chưa từng có hợp đồng (BR-DM-03). */
    public boolean coTheXoa(String maPhong) {
        return !hopDongRepository.existsByMaPhong(maPhong);
    }

    private boolean coTheXoaHoacNgungKhaiThac(String maPhong) {
        return hopDongRepository.timHopDongHieuLucCua(maPhong).isEmpty();
    }

    /** Phong.laSapTra: true khi HĐ hiệu lực có ngayBaoTra != null và chưa trả thực tế. */
    private boolean laSapTra(HopDong hd) {
        return hd != null
                && hd.getTrangThai() == TrangThaiHD.HIEU_LUC
                && hd.getNgayBaoTra() != null
                && hd.getNgayTraThucTe() == null;
    }

    /** Phong.xoa: chỉ phòng chưa từng có hợp đồng (BR-DM-03, CN-02.01). Xóa Phong và TaiSan của phòng; ghi nhật ký. */
    @Transactional
    public void xoa(String maPhong, String maTaiKhoanThucHien) {
        Phong phong = timHoacNem(maPhong);
        if (!coTheXoa(maPhong)) {
            throw new IllegalArgumentException("Phòng \"" + maPhong + "\" đã từng có hợp đồng, chỉ có thể ngừng khai thác.");
        }
        taiSanRepository.deleteByPhong_MaPhong(maPhong);
        phongRepository.delete(phong);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "Phong", maPhong, "HUY", "Xóa phòng chưa từng có hợp đồng.");
    }

    private Phong timHoacNem(String maPhong) {
        return phongRepository.findById(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng \"" + maPhong + "\"."));
    }
}
