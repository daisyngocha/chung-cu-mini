package vn.chungcumini.backend.chiso;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hop-dong/{maHopDong}/chi-so")
@PreAuthorize("hasRole('CHU_NHA')")
public class ChiSoController {

    private final ChiSoService chiSoService;

    public ChiSoController(ChiSoService chiSoService) {
        this.chiSoService = chiSoService;
    }

    @GetMapping
    public List<ChiSoResponse> danhSach(@PathVariable String maHopDong) {
        return chiSoService.layTheoHopDong(maHopDong).stream().map(ChiSoResponse::tu).toList();
    }

    @PostMapping
    public ChiSoResponse ghiChiSo(
            @PathVariable String maHopDong, @Valid @RequestBody GhiChiSoRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return ChiSoResponse.tu(chiSoService.ghiChiSo(
                maHopDong, req.loaiCongTo(), req.loaiGhi(), req.kyThang(), req.chiSoMoi(), req.anhCongTo(), maTaiKhoan));
    }
}
