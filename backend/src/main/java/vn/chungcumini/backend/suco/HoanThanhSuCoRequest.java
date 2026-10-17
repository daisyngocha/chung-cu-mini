package vn.chungcumini.backend.suco;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record HoanThanhSuCoRequest(@NotBlank String ketQua, BigDecimal chiPhi, boolean doKhachGay, String anh) {
}
