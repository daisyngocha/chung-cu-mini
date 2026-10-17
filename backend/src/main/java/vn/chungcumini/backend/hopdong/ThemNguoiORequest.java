package vn.chungcumini.backend.hopdong;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ThemNguoiORequest(@NotBlank String hoTen, String soCCCD, String soDienThoai, LocalDate ngayVao) {
}
