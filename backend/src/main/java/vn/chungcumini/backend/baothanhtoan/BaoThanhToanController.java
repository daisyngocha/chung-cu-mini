package vn.chungcumini.backend.baothanhtoan;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.chungcumini.backend.phieuthu.PhieuThuResponse;
import vn.chungcumini.backend.repository.BaoThanhToanRepository;

/** Chủ nhà xác nhận/từ chối báo chuyển khoản. Khách gửi báo — xem KhachThanhToanController. */
@RestController
@RequestMapping("/api/bao-thanh-toan")
@PreAuthorize("hasRole('CHU_NHA')")
public class BaoThanhToanController {

    private final BaoThanhToanService baoThanhToanService;
    private final BaoThanhToanRepository baoThanhToanRepository;

    public BaoThanhToanController(BaoThanhToanService baoThanhToanService, BaoThanhToanRepository baoThanhToanRepository) {
        this.baoThanhToanService = baoThanhToanService;
        this.baoThanhToanRepository = baoThanhToanRepository;
    }

    @GetMapping("/cho-xac-nhan")
    public List<BaoThanhToanResponse> choXacNhan() {
        return baoThanhToanRepository.timHangChoXacNhan().stream().map(BaoThanhToanResponse::tu).toList();
    }

    @PatchMapping("/{maBaoTT}/xac-nhan")
    public PhieuThuResponse xacNhan(
            @PathVariable String maBaoTT, @Valid @RequestBody XacNhanRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return PhieuThuResponse.tu(baoThanhToanService.xacNhan(maBaoTT, req.soTienThucNhan(), maTaiKhoan));
    }

    @PatchMapping("/{maBaoTT}/tu-choi")
    public void tuChoi(
            @PathVariable String maBaoTT, @Valid @RequestBody TuChoiRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        baoThanhToanService.tuChoi(maBaoTT, req.lyDo(), maTaiKhoan);
    }
}
