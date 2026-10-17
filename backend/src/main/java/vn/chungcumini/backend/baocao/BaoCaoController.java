package vn.chungcumini.backend.baocao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;
import vn.chungcumini.backend.hopdong.HopDongResponse;

/** DichVuBaoCao — báo cáo vận hành cho chủ nhà (CN-08.*, UC08.05–UC08.09). */
@RestController
@RequestMapping("/api/bao-cao")
@PreAuthorize("hasRole('CHU_NHA')")
public class BaoCaoController {

    private final DichVuBaoCaoService dichVuBaoCaoService;

    public BaoCaoController(DichVuBaoCaoService dichVuBaoCaoService) {
        this.dichVuBaoCaoService = dichVuBaoCaoService;
    }

    @GetMapping("/tinh-trang-phong")
    public TinhTrangPhongResponse tinhTrangPhong() {
        return dichVuBaoCaoService.tinhTrangPhong();
    }

    @GetMapping("/ty-le-lap-day")
    public BigDecimal tyLeLapDay(@RequestParam String kyThang) {
        return dichVuBaoCaoService.tyLeLapDay(kyThang);
    }

    @GetMapping("/doanh-thu")
    public Map<LoaiKhoanTBT, BigDecimal> doanhThu(@RequestParam String kyThang) {
        return dichVuBaoCaoService.doanhThu(kyThang);
    }

    @GetMapping("/ty-le-thu")
    public BigDecimal tyLeThu(@RequestParam LocalDate tuNgay, @RequestParam LocalDate denNgay) {
        return dichVuBaoCaoService.tyLeThu(tuNgay, denNgay);
    }

    @GetMapping("/cong-no-qua-han")
    public List<CongNoQuaHanItem> congNoQuaHan(@RequestParam(required = false) LocalDate ngay) {
        return dichVuBaoCaoService.congNoQuaHan(ngay != null ? ngay : LocalDate.now());
    }

    @GetMapping("/coc-dang-giu")
    public List<CocDangGiuItem> cocDangGiu() {
        return dichVuBaoCaoService.cocDangGiu();
    }

    @GetMapping("/hop-dong-sap-het-han")
    public List<HopDongResponse> hopDongSapHetHan(@RequestParam(required = false, defaultValue = "30") int soNgay) {
        return dichVuBaoCaoService.hopDongSapHetHan(soNgay);
    }
}
