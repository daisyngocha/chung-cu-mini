package vn.chungcumini.backend.khoitao;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record KhoiTaoKhachInfo(
        @NotBlank String soDienThoai, @NotBlank String hoTen, LocalDate ngaySinh, String soCCCD, String anhCCCD, String queQuan,
        String noiLamViec) {
}
