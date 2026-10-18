package vn.chungcumini.backend.khach;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BaoTraPhongKhachRequest(@NotNull LocalDate ngayTraDuKien) {
}
