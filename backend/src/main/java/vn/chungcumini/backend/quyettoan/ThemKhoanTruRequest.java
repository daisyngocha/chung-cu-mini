package vn.chungcumini.backend.quyettoan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import vn.chungcumini.backend.domain.LoaiKhoanQT;

public record ThemKhoanTruRequest(
        @NotNull LoaiKhoanQT loaiKhoan,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal soTien,
        String ghiChu,
        String anhBangChung,
        String lyDoDieuChinh) {
}
