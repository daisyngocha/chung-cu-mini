package vn.chungcumini.backend.phong;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PatchMapping("/{maPhong}/gia-niem-yet")
    public PhongResponse capNhatGiaNiemYet(@PathVariable String maPhong, @Valid @RequestBody CapNhatGiaRequest request) {
        return phongService.capNhatGiaNiemYet(maPhong, request.giaNiemYet());
    }

    @PatchMapping("/{maPhong}/trang-thai")
    public PhongResponse chuyenTrangThai(@PathVariable String maPhong, @Valid @RequestBody ChuyenTrangThaiRequest request) {
        return phongService.chuyenTrangThai(maPhong, request.trangThai());
    }

    @DeleteMapping("/{maPhong}")
    public void xoa(@PathVariable String maPhong, @AuthenticationPrincipal String maTaiKhoan) {
        phongService.xoa(maPhong, maTaiKhoan);
    }
}
