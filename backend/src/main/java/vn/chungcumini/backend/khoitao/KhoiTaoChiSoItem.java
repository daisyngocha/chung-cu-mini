package vn.chungcumini.backend.khoitao;

import jakarta.validation.constraints.NotNull;

/** ChiSo DAU_KY = chỉ số gần nhất (chiSoCu = chiSoMoi = chiSoGanNhat, không có tiêu thụ). */
public record KhoiTaoChiSoItem(@NotNull Integer chiSoGanNhat, String anhCongTo) {
}
