package vn.chungcumini.backend.taisan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public record ThemTaiSanRequest(
        @NotBlank String tenTaiSan,
        @NotNull @Min(1) Integer soLuong,
        String tinhTrang,
        String ghiChu) {
}
