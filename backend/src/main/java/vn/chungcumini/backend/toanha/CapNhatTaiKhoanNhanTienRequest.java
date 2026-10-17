package vn.chungcumini.backend.toanha;

import jakarta.validation.constraints.NotBlank;

public record CapNhatTaiKhoanNhanTienRequest(
        @NotBlank String nganHang, @NotBlank String soTaiKhoan, @NotBlank String chuTaiKhoan) {
}
