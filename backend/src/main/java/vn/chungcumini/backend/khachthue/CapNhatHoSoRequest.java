package vn.chungcumini.backend.khachthue;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record CapNhatHoSoRequest(
        @NotBlank @Pattern(regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ.") String soDienThoai,
        @NotBlank String hoTen,
        @NotNull LocalDate ngaySinh,
        @NotBlank @Pattern(regexp = "^\\d{12}$", message = "Số CCCD phải gồm 12 chữ số.") String soCCCD,
        String anhCCCD,
        String queQuan,
        String noiLamViec) {
}
