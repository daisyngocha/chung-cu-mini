package vn.chungcumini.backend.nhatky;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** CN-09.05 (UC01.09): tra cứu nhật ký thao tác. */
@RestController
@RequestMapping("/api/nhat-ky")
@PreAuthorize("hasRole('CHU_NHA')")
public class NhatKyThaoTacController {

    private final NhatKyThaoTacService nhatKyThaoTacService;

    public NhatKyThaoTacController(NhatKyThaoTacService nhatKyThaoTacService) {
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    @GetMapping
    public List<NhatKyThaoTacResponse> danhSach(@RequestParam(required = false) String doiTuong) {
        return nhatKyThaoTacService.layDanhSach(doiTuong).stream().map(NhatKyThaoTacResponse::tu).toList();
    }
}
