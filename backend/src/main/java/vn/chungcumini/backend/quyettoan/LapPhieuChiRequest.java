package vn.chungcumini.backend.quyettoan;

import jakarta.validation.constraints.NotNull;
import vn.chungcumini.backend.domain.HinhThucTT;

public record LapPhieuChiRequest(@NotNull HinhThucTT hinhThuc) {
}
