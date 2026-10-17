package vn.chungcumini.backend.phong;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/phong")
@PreAuthorize("hasRole('CHU_NHA')")
public class PhongController {

    private final PhongService phongService;

    public PhongController(PhongService phongService) {
        this.phongService = phongService;
    }

    @GetMapping
    public List<PhongResponse> danhSach() {
        return phongService.layDanhSach();
    }

    @PostMapping
    public PhongResponse taoPhong(@Valid @RequestBody TaoPhongRequest request) {
        return phongService.taoPhong(request);
    }

    @PatchMapping("/{maPhong}/trang-thai")
    public PhongResponse chuyenTrangThai(@PathVariable String maPhong) {
        return phongService.chuyenTrangThai(maPhong);
    }
}
