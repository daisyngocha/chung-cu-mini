package vn.chungcumini.backend.thongbao;

import jakarta.validation.constraints.NotBlank;

public record GuiThongBaoChungRequest(@NotBlank String tieuDe, @NotBlank String noiDung) {
}
