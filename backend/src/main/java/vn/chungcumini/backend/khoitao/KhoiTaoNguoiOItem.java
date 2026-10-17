package vn.chungcumini.backend.khoitao;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record KhoiTaoNguoiOItem(@NotBlank String hoTen, String soCCCD, String soDienThoai, LocalDate ngayVao) {
}
