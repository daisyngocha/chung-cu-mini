package vn.chungcumini.backend.quyettoan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record SuaKhoanTruRequest(
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal soTien, String ghiChu, String anhBangChung, String lyDo) {
}
