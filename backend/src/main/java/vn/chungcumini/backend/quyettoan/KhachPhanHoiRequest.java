package vn.chungcumini.backend.quyettoan;

import jakarta.validation.constraints.NotNull;

public record KhachPhanHoiRequest(@NotNull Boolean dongY, String yKien) {
}
