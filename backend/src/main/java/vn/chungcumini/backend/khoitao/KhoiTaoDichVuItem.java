package vn.chungcumini.backend.khoitao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KhoiTaoDichVuItem(@NotBlank String maKhoan, @NotNull Integer soLuong) {
}
