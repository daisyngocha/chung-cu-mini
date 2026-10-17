package vn.chungcumini.backend.phong;

import jakarta.validation.constraints.NotNull;
import vn.chungcumini.backend.domain.TrangThaiPhong;

public record ChuyenTrangThaiRequest(@NotNull TrangThaiPhong trangThai) {
}
