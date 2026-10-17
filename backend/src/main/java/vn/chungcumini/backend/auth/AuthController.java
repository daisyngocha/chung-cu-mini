package vn.chungcumini.backend.auth;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/dang-nhap")
    public DangNhapResponse dangNhap(@Valid @RequestBody DangNhapRequest request) {
        return authService.dangNhap(request);
    }

    @PatchMapping("/doi-mat-khau")
    public void doiMatKhau(
            @AuthenticationPrincipal String maTaiKhoan, @Valid @RequestBody DoiMatKhauRequest request) {
        authService.doiMatKhau(maTaiKhoan, request);
    }
}
