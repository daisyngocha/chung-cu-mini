package vn.chungcumini.backend.bangiaotaisan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import vn.chungcumini.backend.domain.LoaiBanGiao;

public record GhiNhanTinhTrangRequest(
        @NotBlank String maTaiSan, @NotNull LoaiBanGiao loaiBanGiao, @NotBlank String tinhTrang, String anh, BigDecimal chiPhiHuHong) {
}
