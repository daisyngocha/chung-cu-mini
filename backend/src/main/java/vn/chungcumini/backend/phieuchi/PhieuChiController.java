package vn.chungcumini.backend.phieuchi;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** PhieuChi.xuatPDF: chỉ Chủ nhà xem/in (QT.PHIEU_CHI) — không có phần khách thuê trong đặc tả. */
@RestController
@RequestMapping("/api/phieu-chi")
@PreAuthorize("hasRole('CHU_NHA')")
public class PhieuChiController {

    private final PhieuChiService phieuChiService;

    public PhieuChiController(PhieuChiService phieuChiService) {
        this.phieuChiService = phieuChiService;
    }

    @GetMapping("/{soPhieuChi}")
    public PhieuChiResponse chiTiet(@PathVariable String soPhieuChi) {
        return PhieuChiResponse.tu(phieuChiService.timHoacNem(soPhieuChi));
    }

    @GetMapping(value = "/{soPhieuChi}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> xuatPDF(@PathVariable String soPhieuChi) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + soPhieuChi + ".pdf\"")
                .body(phieuChiService.xuatPDF(soPhieuChi));
    }
}
