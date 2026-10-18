package vn.chungcumini.backend.khach;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiKetThuc;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.nguoio.NguoiOResponse;
import vn.chungcumini.backend.nguoio.NguoiOService;

/** KT-01.01 (Trang chủ), KT-03.01 (Hồ sơ & hợp đồng), KT-03.02 (Báo trả phòng). */
@RestController
@RequestMapping("/api/khach/hop-dong")
@PreAuthorize("hasRole('KHACH_THUE')")
public class KhachHopDongController {

    private final HopDongService hopDongService;
    private final NguoiOService nguoiOService;

    public KhachHopDongController(HopDongService hopDongService, NguoiOService nguoiOService) {
        this.hopDongService = hopDongService;
        this.nguoiOService = nguoiOService;
    }

    @GetMapping
    public KhachHopDongResponse cuaToi(@AuthenticationPrincipal String maTaiKhoan) {
        HopDong hopDong = hopDongService.layHopDongHieuLucCuaTaiKhoan(maTaiKhoan);
        var dsNguoiO = nguoiOService.layTheoHopDong(hopDong.getMaHopDong()).stream().map(NguoiOResponse::tu).toList();
        return KhachHopDongResponse.tu(hopDong, dsNguoiO);
    }

    /** KT-03.02: khách tự báo trả phòng — luôn ghi loại kết thúc KHACH_BAO_TRA. */
    @PatchMapping("/bao-tra")
    public void baoTraPhong(@Valid @RequestBody BaoTraPhongKhachRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        HopDong hopDong = hopDongService.layHopDongHieuLucCuaTaiKhoan(maTaiKhoan);
        hopDongService.baoTraPhong(hopDong.getMaHopDong(), req.ngayTraDuKien(), LoaiKetThuc.KHACH_BAO_TRA);
    }
}
