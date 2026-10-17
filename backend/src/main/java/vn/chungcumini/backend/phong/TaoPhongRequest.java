package vn.chungcumini.backend.phong;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;

public record TaoPhongRequest(
        @NotNull @Pattern(regexp = "^[A-Z0-9]{2,10}$", message = "Mã phòng chỉ gồm chữ in hoa và số, 2–10 ký tự.") String maPhong,
        @NotNull @Min(0) @Max(20) Integer tang,
        @NotNull @DecimalMin(value = "0", inclusive = false) @DecimalMax("200") BigDecimal dienTich,
        @NotNull @Min(1) @Max(10) Integer soNguoiToiDa,
        @NotNull @DecimalMin(value = "0", inclusive = false) @DecimalMax("100000000") BigDecimal giaNiemYet,
        boolean coCongToNuoc) {
}
