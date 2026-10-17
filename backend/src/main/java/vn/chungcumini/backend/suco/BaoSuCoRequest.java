package vn.chungcumini.backend.suco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vn.chungcumini.backend.domain.LoaiSuCo;

public record BaoSuCoRequest(@NotNull LoaiSuCo loaiSuCo, @NotBlank String moTa, String anh) {
}
