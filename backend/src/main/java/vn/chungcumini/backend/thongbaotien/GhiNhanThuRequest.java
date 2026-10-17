package vn.chungcumini.backend.thongbaotien;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.HinhThucTT;

public record GhiNhanThuRequest(
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal soTien,
        @NotNull HinhThucTT hinhThuc,
        String nguoiNop,
        LocalDate ngayThu) {
}
