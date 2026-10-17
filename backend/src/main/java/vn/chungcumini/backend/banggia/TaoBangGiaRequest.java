package vn.chungcumini.backend.banggia;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.LoaiKhoan;

public record TaoBangGiaRequest(
        @NotBlank String tenKhoan,
        @NotNull LoaiKhoan loaiKhoan,
        @NotNull @DecimalMin(value = "0", message = "Đơn giá không được âm.") BigDecimal donGia,
        @NotBlank String donViTinh,
        @NotNull LocalDate ngayApDung) {
}
