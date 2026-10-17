package vn.chungcumini.backend.banggia;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DoiDonGiaRequest(@NotNull BigDecimal giaMoi, @NotNull LocalDate ngayApDung) {
}
