package vn.chungcumini.backend.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoiMatKhauRequest(
        @NotBlank String matKhauCu,
        @NotBlank @Size(min = 6, message = "Mật khẩu mới phải có ít nhất 6 ký tự.") String matKhauMoi) {
}
