package vn.chungcumini.backend.banggia;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** GET dùng chung cho cả Chủ nhà (Cài đặt bảng giá) và Khách thuê (bảng giá áp dụng ở trang chủ) — chỉ sửa mới giới hạn Chủ nhà. */
@RestController
@RequestMapping("/api/bang-gia")
public class BangGiaController {

    private final BangGiaService bangGiaService;

    public BangGiaController(BangGiaService bangGiaService) {
        this.bangGiaService = bangGiaService;
    }

    @GetMapping
    public List<BangGiaResponse> danhSach() {
        return bangGiaService.layDanhSach();
    }

    @PreAuthorize("hasRole('CHU_NHA')")
    @PostMapping
    public BangGiaResponse taoKhoan(@Valid @RequestBody TaoBangGiaRequest request) {
        return bangGiaService.taoKhoan(request);
    }

    @PreAuthorize("hasRole('CHU_NHA')")
    @PatchMapping("/{maKhoan}/doi-don-gia")
    public BangGiaResponse doiDonGia(
            @PathVariable String maKhoan, @Valid @RequestBody DoiDonGiaRequest request, @AuthenticationPrincipal String maTaiKhoan) {
        return bangGiaService.doiDonGia(maKhoan, request.giaMoi(), request.ngayApDung(), maTaiKhoan);
    }
}
