package vn.chungcumini.backend.khoitao;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** DichVuKhoiTao.nhapDuLieuDangVanHanh: nhập dữ liệu đầu kỳ (CN-09.04) — chỉ dùng khi bàn giao tòa đang vận hành. */
@RestController
@RequestMapping("/api/khoi-tao")
@PreAuthorize("hasRole('CHU_NHA')")
public class KhoiTaoController {

    private final DichVuKhoiTaoService dichVuKhoiTaoService;

    public KhoiTaoController(DichVuKhoiTaoService dichVuKhoiTaoService) {
        this.dichVuKhoiTaoService = dichVuKhoiTaoService;
    }

    @PostMapping
    public List<KhoiTaoKetQuaItem> nhapDuLieuDangVanHanh(@Valid @RequestBody KhoiTaoDuLieuRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return dichVuKhoiTaoService.nhapDuLieuDangVanHanh(req.dsHopDong(), maTaiKhoan);
    }
}
