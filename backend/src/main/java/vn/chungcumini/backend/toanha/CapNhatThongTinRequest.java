package vn.chungcumini.backend.toanha;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CapNhatThongTinRequest(
        @NotBlank String tenToa,
        @NotBlank String diaChi,
        String noiQuy,
        @NotNull @Min(1) @Max(31) Integer ngayChotChiSo,
        @NotNull @Min(1) @Max(31) Integer hanThanhToan,
        @NotBlank String tenChuNha,
        @NotBlank String sdtChuNha,
        String diaChiChuNha) {
}
