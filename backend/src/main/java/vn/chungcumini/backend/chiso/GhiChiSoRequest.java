package vn.chungcumini.backend.chiso;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import vn.chungcumini.backend.domain.LoaiCongTo;
import vn.chungcumini.backend.domain.LoaiGhi;

public record GhiChiSoRequest(
        @NotNull LoaiCongTo loaiCongTo,
        @NotNull LoaiGhi loaiGhi,
        @NotNull String kyThang,
        @NotNull @Min(0) Integer chiSoMoi,
        String anhCongTo) {
}
