package vn.chungcumini.backend.khach;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.chungcumini.backend.domain.SuCo;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.suco.BaoSuCoRequest;
import vn.chungcumini.backend.suco.LichSuSuCoResponse;
import vn.chungcumini.backend.suco.LichSuSuCoService;
import vn.chungcumini.backend.suco.MoLaiSuCoRequest;
import vn.chungcumini.backend.suco.SuCoResponse;
import vn.chungcumini.backend.suco.SuCoService;

/** KT: khách báo sự cố phòng mình, xem danh sách/chi tiết, mở lại khi chưa khắc phục (BR-SC-01). */
@RestController
@RequestMapping("/api/khach/su-co")
@PreAuthorize("hasRole('KHACH_THUE')")
public class KhachSuCoController {

    private final SuCoService suCoService;
    private final LichSuSuCoService lichSuSuCoService;
    private final HopDongService hopDongService;

    public KhachSuCoController(SuCoService suCoService, LichSuSuCoService lichSuSuCoService, HopDongService hopDongService) {
        this.suCoService = suCoService;
        this.lichSuSuCoService = lichSuSuCoService;
        this.hopDongService = hopDongService;
    }

    @GetMapping
    public List<SuCoResponse> danhSach(@AuthenticationPrincipal String maTaiKhoan) {
        return suCoService.layTheoTaiKhoanKhach(maTaiKhoan).stream().map(SuCoResponse::tu).toList();
    }

    @PostMapping
    public SuCoResponse baoSuCo(@Valid @RequestBody BaoSuCoRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return SuCoResponse.tu(suCoService.baoSuCo(maTaiKhoan, req.loaiSuCo(), req.moTa(), req.anh()));
    }

    @GetMapping("/{maSuCo}")
    public SuCoResponse chiTiet(@PathVariable String maSuCo, @AuthenticationPrincipal String maTaiKhoan) {
        return SuCoResponse.tu(kiemTraChuPhong(suCoService.timHoacNem(maSuCo), maTaiKhoan));
    }

    @GetMapping("/{maSuCo}/lich-su")
    public List<LichSuSuCoResponse> lichSu(@PathVariable String maSuCo, @AuthenticationPrincipal String maTaiKhoan) {
        kiemTraChuPhong(suCoService.timHoacNem(maSuCo), maTaiKhoan);
        return lichSuSuCoService.layTheoSuCo(maSuCo).stream().map(LichSuSuCoResponse::tu).toList();
    }

    @PatchMapping("/{maSuCo}/mo-lai")
    public SuCoResponse moLai(
            @PathVariable String maSuCo, @RequestBody MoLaiSuCoRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return SuCoResponse.tu(suCoService.moLai(maSuCo, req.ghiChu(), maTaiKhoan));
    }

    private SuCo kiemTraChuPhong(SuCo suCo, String maTaiKhoan) {
        var hopDong = hopDongService.layHopDongHieuLucCuaTaiKhoan(maTaiKhoan);
        if (!hopDong.getPhong().getMaPhong().equals(suCo.getPhong().getMaPhong())) {
            throw new IllegalArgumentException("Sự cố không thuộc phòng của bạn.");
        }
        return suCo;
    }
}
