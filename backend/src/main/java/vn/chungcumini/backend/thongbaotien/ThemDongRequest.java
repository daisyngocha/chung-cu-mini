package vn.chungcumini.backend.thongbaotien;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;

public record ThemDongRequest(
        @NotNull LoaiKhoanTBT loaiKhoan,
        @NotBlank String dienGiai,
        LocalDate tuNgay,
        LocalDate denNgay,
        @NotNull BigDecimal soLuong,
        @NotNull BigDecimal donGia,
        BigDecimal tyLeNgayO,
        String maChiSo,
        String maThongBaoTienGoc) {
}
