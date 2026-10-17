package vn.chungcumini.backend.hopdong;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GiaHanRequest(@NotNull LocalDate ngayHetHanMoi, BigDecimal giaMoi, @NotBlank String anhPhuLuc) {
}
