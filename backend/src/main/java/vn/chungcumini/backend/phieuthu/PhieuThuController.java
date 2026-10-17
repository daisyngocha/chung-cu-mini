package vn.chungcumini.backend.phieuthu;

import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Chủ nhà: xem/hủy/xuất PDF phiếu thu của mọi phòng. Khách chỉ xem phiếu của phòng mình — xem KhachThanhToanController. */
@RestController
@RequestMapping("/api/phieu-thu")
@PreAuthorize("hasRole('CHU_NHA')")
public class PhieuThuController {

    private final PhieuThuService phieuThuService;

    public PhieuThuController(PhieuThuService phieuThuService) {
        this.phieuThuService = phieuThuService;
    }

    @GetMapping
    public List<PhieuThuResponse> theoThongBao(@RequestParam String maThongBaoTien) {
        return phieuThuService.layTheoThongBao(maThongBaoTien).stream().map(PhieuThuResponse::tu).toList();
    }

    @GetMapping("/{soPhieuThu}")
    public PhieuThuResponse chiTiet(@PathVariable String soPhieuThu) {
        return PhieuThuResponse.tu(phieuThuService.timHoacNem(soPhieuThu));
    }

    @GetMapping(value = "/{soPhieuThu}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> xuatPDF(@PathVariable String soPhieuThu) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + soPhieuThu + ".pdf\"")
                .body(phieuThuService.xuatPDF(soPhieuThu));
    }

    @PatchMapping("/{soPhieuThu}/huy")
    public void huy(
            @PathVariable String soPhieuThu, @RequestBody HuyPhieuThuRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        phieuThuService.huy(soPhieuThu, req.lyDo(), maTaiKhoan);
    }
}
