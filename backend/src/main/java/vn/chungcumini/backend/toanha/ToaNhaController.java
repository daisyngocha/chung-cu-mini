package vn.chungcumini.backend.toanha;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/toa-nha")
@PreAuthorize("hasRole('CHU_NHA')")
public class ToaNhaController {

    private final ToaNhaService toaNhaService;

    public ToaNhaController(ToaNhaService toaNhaService) {
        this.toaNhaService = toaNhaService;
    }

    @GetMapping("/mac-dinh")
    public ToaNhaResponse macDinh() {
        return ToaNhaResponse.tu(toaNhaService.layMacDinh());
    }

    /** CN-09.01: thông tin chung của tòa. */
    @PatchMapping("/mac-dinh/thong-tin")
    public ToaNhaResponse capNhatThongTin(@Valid @RequestBody CapNhatThongTinRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return ToaNhaResponse.tu(toaNhaService.capNhatThongTin(req, maTaiKhoan));
    }

    /** CN-09.02: tài khoản nhận tiền dùng để sinh mã VietQR và in trên phiếu thu. */
    @PatchMapping("/mac-dinh/tai-khoan-nhan-tien")
    public ToaNhaResponse capNhatTaiKhoanNhanTien(
            @Valid @RequestBody CapNhatTaiKhoanNhanTienRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return ToaNhaResponse.tu(toaNhaService.capNhatTaiKhoanNhanTien(req, maTaiKhoan));
    }
}
