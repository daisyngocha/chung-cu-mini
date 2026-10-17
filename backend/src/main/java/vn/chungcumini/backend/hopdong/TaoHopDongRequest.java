package vn.chungcumini.backend.hopdong;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TaoHopDongRequest(
        @NotBlank String maPhong,
        @NotBlank String maKhach,
        @NotNull LocalDate ngayBatDau,
        @NotNull LocalDate ngayHetHan,
        BigDecimal giaThue,
        BigDecimal tienCoc,
        List<DichVuDangKy> dsDichVu,
        @DecimalMin("0") BigDecimal mucPhatTraTruoc,
        String anhHopDong) {
}
