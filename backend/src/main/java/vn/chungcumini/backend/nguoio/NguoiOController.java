package vn.chungcumini.backend.nguoio;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** NGUOI_O.QUAN_LY: thao tác trên một người ở cùng đã tồn tại. Tạo mới nằm ở HopDong.themNguoiO. */
@RestController
@RequestMapping("/api/nguoi-o")
@PreAuthorize("hasRole('CHU_NHA')")
public class NguoiOController {

    private final NguoiOService nguoiOService;

    public NguoiOController(NguoiOService nguoiOService) {
        this.nguoiOService = nguoiOService;
    }

    @PatchMapping("/{maNguoiO}")
    public NguoiOResponse capNhatThongTin(@PathVariable String maNguoiO, @Valid @RequestBody CapNhatNguoiORequest request) {
        return NguoiOResponse.tu(nguoiOService.capNhatThongTin(maNguoiO, request.hoTen(), request.soCCCD(), request.soDienThoai()));
    }

    @PatchMapping("/{maNguoiO}/roi-di")
    public NguoiOResponse roiDi(@PathVariable String maNguoiO, @Valid @RequestBody RoiDiRequest request) {
        return NguoiOResponse.tu(nguoiOService.roiDi(maNguoiO, request.ngayRa()));
    }
}
