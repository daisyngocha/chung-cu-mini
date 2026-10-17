package vn.chungcumini.backend.bohengio;

import java.time.LocalDate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** BoHenGio.chayHangNgay chạy tự động 07:00 hằng ngày — endpoint này để chủ nhà chạy tay khi cần (vd kiểm tra, bù chạy). */
@RestController
@RequestMapping("/api/bo-hen-gio")
@PreAuthorize("hasRole('CHU_NHA')")
public class BoHenGioController {

    private final BoHenGioService boHenGioService;

    public BoHenGioController(BoHenGioService boHenGioService) {
        this.boHenGioService = boHenGioService;
    }

    @PostMapping("/chay")
    public KetQuaChayHangNgay chayTay(@RequestParam(required = false) LocalDate ngay) {
        return boHenGioService.chayHangNgay(ngay != null ? ngay : LocalDate.now());
    }
}
