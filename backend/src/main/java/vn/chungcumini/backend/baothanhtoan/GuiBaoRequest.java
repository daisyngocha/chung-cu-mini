package vn.chungcumini.backend.baothanhtoan;

import jakarta.validation.constraints.NotBlank;

public record GuiBaoRequest(@NotBlank String maThongBaoTien, String anhBienLai) {
}
