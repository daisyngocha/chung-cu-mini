package vn.chungcumini.backend.phong;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TaoPhongRequest(
        @NotBlank String maPhong,
        @NotNull Integer tang,
        @NotNull BigDecimal dienTich,
        @NotNull Integer soNguoiToiDa,
        @NotNull BigDecimal giaNiemYet,
        boolean coCongToNuoc) {
}
