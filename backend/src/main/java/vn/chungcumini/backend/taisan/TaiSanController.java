package vn.chungcumini.backend.taisan;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phong/{maPhong}/tai-san")
@PreAuthorize("hasRole('CHU_NHA')")
public class TaiSanController {

    private final TaiSanService taiSanService;

    public TaiSanController(TaiSanService taiSanService) {
        this.taiSanService = taiSanService;
    }

    @GetMapping
    public List<TaiSanResponse> danhSach(@PathVariable String maPhong) {
        return taiSanService.layDanhSach(maPhong);
    }

    @PostMapping
    public TaiSanResponse them(@PathVariable String maPhong, @Valid @RequestBody ThemTaiSanRequest request) {
        return taiSanService.them(maPhong, request);
    }

    @PatchMapping("/{maTaiSan}/tinh-trang")
    public TaiSanResponse capNhatTinhTrang(
            @PathVariable String maPhong, @PathVariable String maTaiSan, @Valid @RequestBody CapNhatTinhTrangRequest request) {
        return taiSanService.capNhatTinhTrang(maTaiSan, request.tinhTrang());
    }
}
