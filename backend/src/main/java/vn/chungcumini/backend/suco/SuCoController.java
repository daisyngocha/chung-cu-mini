package vn.chungcumini.backend.suco;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Chủ nhà: xem/tiếp nhận/từ chối/hoàn thành sự cố. Khách báo/mở lại — xem KhachSuCoController. */
@RestController
@RequestMapping("/api/su-co")
@PreAuthorize("hasRole('CHU_NHA')")
public class SuCoController {

    private final SuCoService suCoService;
    private final LichSuSuCoService lichSuSuCoService;

    public SuCoController(SuCoService suCoService, LichSuSuCoService lichSuSuCoService) {
        this.suCoService = suCoService;
        this.lichSuSuCoService = lichSuSuCoService;
    }

    @GetMapping
    public List<SuCoResponse> danhSach() {
        return suCoService.layDanhSach().stream().map(SuCoResponse::tu).toList();
    }

    @GetMapping("/dang-mo")
    public List<SuCoResponse> dangMo() {
        return suCoService.layDangMo().stream().map(SuCoResponse::tu).toList();
    }

    @GetMapping("/{maSuCo}")
    public SuCoResponse chiTiet(@PathVariable String maSuCo) {
        return SuCoResponse.tu(suCoService.timHoacNem(maSuCo));
    }

    @GetMapping("/{maSuCo}/lich-su")
    public List<LichSuSuCoResponse> lichSu(@PathVariable String maSuCo) {
        return lichSuSuCoService.layTheoSuCo(maSuCo).stream().map(LichSuSuCoResponse::tu).toList();
    }

    @PatchMapping("/{maSuCo}/tiep-nhan")
    public SuCoResponse tiepNhan(
            @PathVariable String maSuCo, @Valid @RequestBody TiepNhanRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return SuCoResponse.tu(suCoService.tiepNhan(maSuCo, req.lichHen(), maTaiKhoan));
    }

    @PatchMapping("/{maSuCo}/tu-choi")
    public SuCoResponse tuChoi(
            @PathVariable String maSuCo, @RequestBody TuChoiSuCoRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return SuCoResponse.tu(suCoService.tuChoi(maSuCo, req.lyDo(), maTaiKhoan));
    }

    @PatchMapping("/{maSuCo}/hoan-thanh")
    public SuCoResponse hoanThanh(
            @PathVariable String maSuCo, @Valid @RequestBody HoanThanhSuCoRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return SuCoResponse.tu(suCoService.hoanThanh(maSuCo, req.ketQua(), req.chiPhi(), req.doKhachGay(), req.anh(), maTaiKhoan));
    }
}
