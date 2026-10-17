package vn.chungcumini.backend.khachthue;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.chungcumini.backend.hopdong.HopDongSummaryResponse;

@RestController
@RequestMapping("/api/khach-thue")
@PreAuthorize("hasRole('CHU_NHA')")
public class KhachThueController {

    private final KhachThueService khachThueService;

    public KhachThueController(KhachThueService khachThueService) {
        this.khachThueService = khachThueService;
    }

    @GetMapping
    public Optional<KhachThueResponse> timTheoSdt(@RequestParam String sdt) {
        return khachThueService.timTheoSDT(sdt).map(KhachThueResponse::tu);
    }

    @PostMapping
    public KhachThueResponse capNhatHoSo(@Valid @RequestBody CapNhatHoSoRequest req) {
        return KhachThueResponse.tu(khachThueService.capNhatHoSo(
                req.soDienThoai(), req.hoTen(), req.ngaySinh(), req.soCCCD(), req.anhCCCD(), req.queQuan(), req.noiLamViec()));
    }

    @GetMapping("/{maKhach}/lich-su-thue")
    public List<HopDongSummaryResponse> lichSuThue(@PathVariable String maKhach) {
        return khachThueService.layLichSuThue(maKhach).stream().map(HopDongSummaryResponse::tu).toList();
    }
}
