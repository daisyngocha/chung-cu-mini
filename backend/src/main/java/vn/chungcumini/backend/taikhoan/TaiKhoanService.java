package vn.chungcumini.backend.taikhoan;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.NguoiO;
import vn.chungcumini.backend.domain.TaiKhoan;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiTK;
import vn.chungcumini.backend.domain.VaiTro;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.TaiKhoanRepository;

/** Quản lý tài khoản khách phía chủ nhà (CN-09.03). Đăng nhập/đổi mật khẩu của chính mình ở auth/AuthService. */
@Service
public class TaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final HopDongRepository hopDongRepository;
    private final PasswordEncoder passwordEncoder;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public TaiKhoanService(
            TaiKhoanRepository taiKhoanRepository,
            HopDongRepository hopDongRepository,
            PasswordEncoder passwordEncoder,
            NhatKyThaoTacService nhatKyThaoTacService) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.hopDongRepository = hopDongRepository;
        this.passwordEncoder = passwordEncoder;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    /**
     * TaiKhoan.taoTaiKhoanKhach: cấp tài khoản cho người đứng tên (nguoiO = null) hoặc một người ở cùng (BR-CT-01).
     * SĐT đã có tài khoản (khách thuê lại) → mở khóa + đặt lại mật khẩu thay vì tạo mới.
     */
    @Transactional
    public TaoTaiKhoanKhachResult taoTaiKhoanKhach(HopDong hopDong, NguoiO nguoiO, String maTaiKhoanThucHien) {
        boolean hopLe = hopDong.getTrangThai() == TrangThaiHD.CHO_NHAN_PHONG
                || (hopDong.getTrangThai() == TrangThaiHD.HIEU_LUC && hopDong.getMaTaiKhoan() == null);
        if (!hopLe) {
            throw new IllegalArgumentException("Hợp đồng \"" + hopDong.getMaHopDong() + "\" không ở trạng thái được cấp tài khoản.");
        }

        String sdt;
        String maNguoiOCapTK = null;
        if (nguoiO != null) {
            if (!nguoiO.getHopDong().getMaHopDong().equals(hopDong.getMaHopDong())) {
                throw new IllegalArgumentException("Người ở cùng không thuộc hợp đồng \"" + hopDong.getMaHopDong() + "\".");
            }
            if (nguoiO.getSoDienThoai() == null || !nguoiO.getSoDienThoai().matches("^0\\d{9}$")) {
                throw new IllegalArgumentException("Người được cấp tài khoản phải có số điện thoại hợp lệ.");
            }
            sdt = nguoiO.getSoDienThoai();
            maNguoiOCapTK = nguoiO.getMaNguoiO();
        } else {
            sdt = hopDong.getKhachThue().getSoDienThoai();
        }

        String matKhauTam = MatKhauTamGenerator.sinh();
        Optional<TaiKhoan> daCo = taiKhoanRepository.findByTenDangNhap(sdt);
        TaiKhoan taiKhoan;
        if (daCo.isPresent()) {
            taiKhoan = daCo.get();
            taiKhoan.setTrangThai(TrangThaiTK.HOAT_DONG);
            taiKhoan.setSoLanSai(0);
            taiKhoan.setKhoaDenLuc(null);
        } else {
            taiKhoan = new TaiKhoan();
            taiKhoan.setMaTaiKhoan("TK" + String.format("%04d", taiKhoanRepository.count() + 1));
            taiKhoan.setTenDangNhap(sdt);
            taiKhoan.setVaiTro(VaiTro.KHACH_THUE);
        }
        taiKhoan.setMatKhauHash(passwordEncoder.encode(matKhauTam));
        taiKhoan.setPhaiDoiMatKhau(true);
        taiKhoan = taiKhoanRepository.save(taiKhoan);

        hopDong.setMaTaiKhoan(taiKhoan.getMaTaiKhoan());
        hopDong.setMaNguoiOCapTK(maNguoiOCapTK);
        hopDongRepository.save(hopDong);

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien,
                "TaiKhoan",
                taiKhoan.getMaTaiKhoan(),
                "TAO",
                "Cấp tài khoản khách cho hợp đồng \"" + hopDong.getMaHopDong() + "\".");

        return new TaoTaiKhoanKhachResult(taiKhoan.getMaTaiKhoan(), taiKhoan.getTenDangNhap(), matKhauTam);
    }

    /** TaiKhoan.datLaiMatKhau: chỉ áp dụng cho tài khoản KHACH_THUE; trả mật khẩu tạm mới. */
    @Transactional
    public String datLaiMatKhau(String maTaiKhoan) {
        TaiKhoan taiKhoan = timHoacNem(maTaiKhoan);
        if (taiKhoan.getVaiTro() != VaiTro.KHACH_THUE) {
            throw new IllegalArgumentException("Chỉ đặt lại mật khẩu cho tài khoản khách thuê.");
        }
        String matKhauTam = MatKhauTamGenerator.sinh();
        taiKhoan.setMatKhauHash(passwordEncoder.encode(matKhauTam));
        taiKhoan.setPhaiDoiMatKhau(true);
        taiKhoan.setSoLanSai(0);
        if (taiKhoan.getTrangThai() == TrangThaiTK.KHOA_TAM) {
            taiKhoan.setTrangThai(TrangThaiTK.HOAT_DONG);
            taiKhoan.setKhoaDenLuc(null);
        }
        taiKhoanRepository.save(taiKhoan);
        return matKhauTam;
    }

    /** TaiKhoan.khoa: không khóa tài khoản chủ nhà. Gọi tự động khi thanh lý HĐ (GĐ5) hoặc chủ nhà khóa tay. */
    @Transactional
    public void khoa(String maTaiKhoan) {
        TaiKhoan taiKhoan = timHoacNem(maTaiKhoan);
        if (taiKhoan.getVaiTro() == VaiTro.CHU_NHA) {
            throw new IllegalArgumentException("Không thể khóa tài khoản chủ nhà.");
        }
        taiKhoan.setTrangThai(TrangThaiTK.DA_KHOA);
        taiKhoanRepository.save(taiKhoan);
    }

    /** TaiKhoan.moKhoa: dùng khi khách thuê lại. */
    @Transactional
    public void moKhoa(String maTaiKhoan) {
        TaiKhoan taiKhoan = timHoacNem(maTaiKhoan);
        if (taiKhoan.getTrangThai() == TrangThaiTK.HOAT_DONG) {
            throw new IllegalArgumentException("Tài khoản \"" + maTaiKhoan + "\" đang hoạt động.");
        }
        taiKhoan.setTrangThai(TrangThaiTK.HOAT_DONG);
        taiKhoan.setSoLanSai(0);
        taiKhoanRepository.save(taiKhoan);
    }

    private TaiKhoan timHoacNem(String maTaiKhoan) {
        return taiKhoanRepository.findById(maTaiKhoan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản \"" + maTaiKhoan + "\"."));
    }
}
