package vn.chungcumini.backend.thongbaotien;

import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thong-bao-tien")
@PreAuthorize("hasRole('CHU_NHA')")
public class ThongBaoTienController {

    private final ThongBaoTienService thongBaoTienService;
    private final DichVuThongBaoTienService dichVuThongBaoTienService;

    public ThongBaoTienController(ThongBaoTienService thongBaoTienService, DichVuThongBaoTienService dichVuThongBaoTienService) {
        this.thongBaoTienService = thongBaoTienService;
        this.dichVuThongBaoTienService = dichVuThongBaoTienService;
    }

    @GetMapping
    public java.util.List<TongHopKyResponse> bangTongHop(@RequestParam String kyThang) {
        return dichVuThongBaoTienService.bangTongHop(kyThang);
    }

    @GetMapping("/{maThongBaoTien}")
    public ThongBaoTienResponse chiTiet(@PathVariable String maThongBaoTien) {
        var tb = thongBaoTienService.timHoacNem(maThongBaoTien);
        var dong = thongBaoTienService.layDongTheoThongBao(maThongBaoTien).stream().map(DongThongBaoTienResponse::tu).toList();
        return ThongBaoTienResponse.tu(tb, dong);
    }

    @PostMapping("/{maThongBaoTien}/dong")
    public DongThongBaoTienResponse themDong(@PathVariable String maThongBaoTien, @Valid @RequestBody ThemDongRequest req) {
        return DongThongBaoTienResponse.tu(thongBaoTienService.themDong(
                maThongBaoTien, req.loaiKhoan(), req.dienGiai(), req.tuNgay(), req.denNgay(),
                req.soLuong(), req.donGia(), req.tyLeNgayO(), req.maChiSo(), req.maThongBaoTienGoc()));
    }

    @PatchMapping("/{maThongBaoTien}/phat-hanh")
    public ResponseEntity<ThongBaoTienResponse> phatHanh(@PathVariable String maThongBaoTien, @AuthenticationPrincipal String maTaiKhoan) {
        boolean canhBaoHetHan = thongBaoTienService.phatHanh(maThongBaoTien, maTaiKhoan);
        return ResponseEntity.ok()
                .header("X-Canh-Bao-Het-Han", String.valueOf(canhBaoHetHan))
                .body(chiTiet(maThongBaoTien));
    }

    @PatchMapping("/{maThongBaoTien}/huy")
    public void huy(@PathVariable String maThongBaoTien, @RequestBody HuyThongBaoRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        thongBaoTienService.huy(maThongBaoTien, req.lyDo(), maTaiKhoan);
    }

    @GetMapping("/{maThongBaoTien}/viet-qr")
    public VietQRResponse vietQR(@PathVariable String maThongBaoTien) {
        return new VietQRResponse(thongBaoTienService.taoMaVietQR(maThongBaoTien));
    }

    @PatchMapping("/{maThongBaoTien}/qua-han")
    public void danhDauQuaHan(@PathVariable String maThongBaoTien, @RequestParam(required = false) LocalDate ngay) {
        thongBaoTienService.danhDauQuaHan(maThongBaoTien, ngay != null ? ngay : LocalDate.now());
    }

    @PostMapping("/lap-hang-loat")
    public LapThongBaoHangLoatResult lapHangLoat(@RequestParam String kyThang, @AuthenticationPrincipal String maTaiKhoan) {
        return dichVuThongBaoTienService.lapThongBaoHangLoat(kyThang, maTaiKhoan);
    }

    @PostMapping("/phat-hanh-hang-loat")
    public PhatHanhHangLoatResult phatHanhHangLoat(
            @RequestBody java.util.List<String> dsMaThongBaoTien, @AuthenticationPrincipal String maTaiKhoan) {
        return dichVuThongBaoTienService.phatHanhHangLoat(dsMaThongBaoTien, maTaiKhoan);
    }
}
