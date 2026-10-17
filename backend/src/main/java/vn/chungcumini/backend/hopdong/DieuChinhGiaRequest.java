package vn.chungcumini.backend.hopdong;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DieuChinhGiaRequest(
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal giaMoi,
        @NotNull LocalDate ngayApDung,
        @NotBlank String anhPhuLuc) {
}
