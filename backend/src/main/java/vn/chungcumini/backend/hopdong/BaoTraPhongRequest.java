package vn.chungcumini.backend.hopdong;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.LoaiKetThuc;

public record BaoTraPhongRequest(@NotNull LocalDate ngayTraDuKien, @NotNull LoaiKetThuc loaiKetThuc) {
}
