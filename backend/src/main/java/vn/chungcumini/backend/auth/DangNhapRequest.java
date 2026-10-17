package vn.chungcumini.backend.auth;

import jakarta.validation.constraints.NotBlank;

public record DangNhapRequest(@NotBlank String tenDangNhap, @NotBlank String matKhau) {
}
