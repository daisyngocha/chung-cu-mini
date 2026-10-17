package vn.chungcumini.backend.baocao;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** DichVuViecCanLam.layDanhSach: màn hình chính của chủ nhà (CN-01.01). */
@RestController
@RequestMapping("/api/viec-can-lam")
@PreAuthorize("hasRole('CHU_NHA')")
public class ViecCanLamController {

    private final DichVuViecCanLamService dichVuViecCanLamService;

    public ViecCanLamController(DichVuViecCanLamService dichVuViecCanLamService) {
        this.dichVuViecCanLamService = dichVuViecCanLamService;
    }

    @GetMapping
    public List<ViecCanLam> danhSach() {
        return dichVuViecCanLamService.layDanhSach();
    }
}
