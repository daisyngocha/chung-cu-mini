package vn.chungcumini.backend.thongbao;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Hộp thông báo (chuông) — chung cho cả Chủ nhà và Khách thuê, mỗi người chỉ thấy thông báo gửi cho
 * mình (TB.XEM). Gửi thông báo chung cho cả tòa (TB.GUI_CHUNG) chỉ Chủ nhà.
 */
@RestController
@RequestMapping("/api/thong-bao")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;

    public ThongBaoController(ThongBaoService thongBaoService) {
        this.thongBaoService = thongBaoService;
    }

    @GetMapping
    public List<ThongBaoNguoiNhanResponse> danhSach(@AuthenticationPrincipal String maTaiKhoan) {
        return thongBaoService.danhSachCuaToi(maTaiKhoan).stream().map(ThongBaoNguoiNhanResponse::tu).toList();
    }

    @GetMapping("/chua-doc")
    public DemChuaDocResponse demChuaDoc(@AuthenticationPrincipal String maTaiKhoan) {
        return new DemChuaDocResponse(thongBaoService.demChuaDoc(maTaiKhoan));
    }

    @PatchMapping("/{maThongBao}/da-doc")
    public void danhDauDaDoc(@PathVariable String maThongBao, @AuthenticationPrincipal String maTaiKhoan) {
        thongBaoService.danhDauDaDoc(maThongBao, maTaiKhoan);
    }

    @PostMapping("/gui-chung")
    @PreAuthorize("hasRole('CHU_NHA')")
    public void guiThongBaoChung(@Valid @RequestBody GuiThongBaoChungRequest req) {
        thongBaoService.guiToanBoKhach(req.tieuDe(), req.noiDung());
    }
}
