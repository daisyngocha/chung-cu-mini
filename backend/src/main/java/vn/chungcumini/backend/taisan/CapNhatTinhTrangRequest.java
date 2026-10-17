package vn.chungcumini.backend.taisan;

import jakarta.validation.constraints.NotBlank;

public record CapNhatTinhTrangRequest(@NotBlank String tinhTrang) {
}
