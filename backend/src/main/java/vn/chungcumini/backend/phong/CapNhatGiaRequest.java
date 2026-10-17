package vn.chungcumini.backend.phong;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CapNhatGiaRequest(@NotNull BigDecimal giaNiemYet) {
}
