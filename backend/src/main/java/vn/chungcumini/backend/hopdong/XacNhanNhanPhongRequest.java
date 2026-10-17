package vn.chungcumini.backend.hopdong;

import jakarta.validation.constraints.NotBlank;

public record XacNhanNhanPhongRequest(@NotBlank String lyDo, String maNguoiOCapTK) {
}
