package vn.chungcumini.backend.nguoio;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RoiDiRequest(@NotNull LocalDate ngayRa) {
}
