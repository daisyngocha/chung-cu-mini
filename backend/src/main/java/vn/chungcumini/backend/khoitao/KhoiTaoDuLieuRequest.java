package vn.chungcumini.backend.khoitao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record KhoiTaoDuLieuRequest(@NotEmpty @Valid List<KhoiTaoHopDongItem> dsHopDong) {
}
