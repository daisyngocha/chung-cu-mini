package vn.chungcumini.backend.taikhoan;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.chungcumini.backend.domain.NguoiO;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.repository.NguoiORepository;

/** CN-09.03: quản lý tài khoản khách phía chủ nhà. */
@RestController
@PreAuthorize("hasRole('CHU_NHA')")
public class TaiKhoanController {

    private final TaiKhoanService taiKhoanService;
    private final HopDongService hopDongService;
    private final NguoiORepository nguoiORepository;

    public TaiKhoanController(TaiKhoanService taiKhoanService, HopDongService hopDongService, NguoiORepository nguoiORepository) {
        this.taiKhoanService = taiKhoanService;
        this.hopDongService = hopDongService;
        this.nguoiORepository = nguoiORepository;
    }

    @GetMapping("/api/tai-khoan-khach")
    public List<TaiKhoanKhachResponse> danhSachKhach() {
        return taiKhoanService.layDanhSachKhach();
    }

    @PostMapping("/api/hop-dong/{maHopDong}/tai-khoan-khach")
    public TaoTaiKhoanKhachResult taoTaiKhoanKhach(
            @PathVariable String maHopDong,
            @RequestBody(required = false) TaoTaiKhoanKhachRequest req,
            @AuthenticationPrincipal String maTaiKhoan) {
        var hopDong = hopDongService.timHoacNem(maHopDong);
        NguoiO nguoiO = null;
        if (req != null && req.maNguoiO() != null) {
            nguoiO = nguoiORepository.findById(req.maNguoiO())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người ở \"" + req.maNguoiO() + "\"."));
        }
        return taiKhoanService.taoTaiKhoanKhach(hopDong, nguoiO, maTaiKhoan);
    }

    @PatchMapping("/api/tai-khoan-khach/{maTaiKhoan}/khoa")
    public void khoa(@PathVariable String maTaiKhoan) {
        taiKhoanService.khoa(maTaiKhoan);
    }

    @PatchMapping("/api/tai-khoan-khach/{maTaiKhoan}/mo-khoa")
    public void moKhoa(@PathVariable String maTaiKhoan) {
        taiKhoanService.moKhoa(maTaiKhoan);
    }

    @PatchMapping("/api/tai-khoan-khach/{maTaiKhoan}/dat-lai-mat-khau")
    public MatKhauTamResponse datLaiMatKhau(@PathVariable String maTaiKhoan) {
        return new MatKhauTamResponse(taiKhoanService.datLaiMatKhau(maTaiKhoan));
    }
}
