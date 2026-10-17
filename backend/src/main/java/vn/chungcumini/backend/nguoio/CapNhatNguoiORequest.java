package vn.chungcumini.backend.nguoio;

import jakarta.validation.constraints.NotBlank;

public record CapNhatNguoiORequest(@NotBlank String hoTen, String soCCCD, String soDienThoai) {
}
