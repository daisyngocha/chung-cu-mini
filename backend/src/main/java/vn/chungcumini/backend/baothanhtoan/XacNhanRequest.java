package vn.chungcumini.backend.baothanhtoan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record XacNhanRequest(@NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal soTienThucNhan) {
}
