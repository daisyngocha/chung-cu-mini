package vn.chungcumini.backend.auth;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.TaiKhoan;
import vn.chungcumini.backend.domain.TrangThaiTK;
import vn.chungcumini.backend.repository.TaiKhoanRepository;
import vn.chungcumini.backend.security.JwtService;

@Service
public class AuthService {

    private static final int SO_LAN_SAI_TOI_DA = 5;
    private static final Duration THOI_GIAN_KHOA_TAM = Duration.ofMinutes(15);

    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            TaiKhoanRepository taiKhoanRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.taiKhoanRepository = taiKhoanRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public DangNhapResponse dangNhap(DangNhapRequest req) {
        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(req.tenDangNhap())
                .orElseThrow(() -> new AuthException("Sai tên đăng nhập hoặc mật khẩu."));

        if (taiKhoan.getTrangThai() == TrangThaiTK.DA_KHOA) {
            throw new AuthException("Tài khoản đã bị khóa. Vui lòng liên hệ chủ nhà.");
        }

        if (taiKhoan.getTrangThai() == TrangThaiTK.KHOA_TAM) {
            if (taiKhoan.getKhoaDenLuc() != null && taiKhoan.getKhoaDenLuc().isAfter(LocalDateTime.now())) {
                long phutConLai = Duration.between(LocalDateTime.now(), taiKhoan.getKhoaDenLuc()).toMinutes() + 1;
                throw new AuthException(
                        "Tài khoản tạm khóa do nhập sai mật khẩu nhiều lần. Thử lại sau " + phutConLai + " phút.");
            }
            // Hết thời gian khóa tạm: mở khóa trước khi kiểm tra mật khẩu.
            taiKhoan.setTrangThai(TrangThaiTK.HOAT_DONG);
            taiKhoan.setSoLanSai(0);
            taiKhoan.setKhoaDenLuc(null);
        }

        if (!passwordEncoder.matches(req.matKhau(), taiKhoan.getMatKhauHash())) {
            int soLanSaiMoi = taiKhoan.getSoLanSai() + 1;
            taiKhoan.setSoLanSai(soLanSaiMoi);
            if (soLanSaiMoi >= SO_LAN_SAI_TOI_DA) {
                taiKhoan.setTrangThai(TrangThaiTK.KHOA_TAM);
                taiKhoan.setKhoaDenLuc(LocalDateTime.now().plus(THOI_GIAN_KHOA_TAM));
                taiKhoanRepository.save(taiKhoan);
                throw new AuthException(
                        "Sai mật khẩu quá " + SO_LAN_SAI_TOI_DA + " lần. Tài khoản tạm khóa "
                                + THOI_GIAN_KHOA_TAM.toMinutes() + " phút.");
            }
            taiKhoanRepository.save(taiKhoan);
            throw new AuthException("Sai tên đăng nhập hoặc mật khẩu.");
        }

        if (taiKhoan.getSoLanSai() > 0) {
            taiKhoan.setSoLanSai(0);
        }
        taiKhoanRepository.save(taiKhoan);

        String token = jwtService.taoToken(taiKhoan);
        return new DangNhapResponse(token, taiKhoan.getVaiTro(), taiKhoan.isPhaiDoiMatKhau());
    }

    @Transactional
    public void doiMatKhau(String maTaiKhoan, DoiMatKhauRequest req) {
        TaiKhoan taiKhoan = taiKhoanRepository.findById(maTaiKhoan)
                .orElseThrow(() -> new AuthException("Không tìm thấy tài khoản."));

        if (!passwordEncoder.matches(req.matKhauCu(), taiKhoan.getMatKhauHash())) {
            throw new AuthException("Mật khẩu hiện tại không đúng.");
        }

        taiKhoan.setMatKhauHash(passwordEncoder.encode(req.matKhauMoi()));
        taiKhoan.setPhaiDoiMatKhau(false);
        taiKhoanRepository.save(taiKhoan);
    }
}
