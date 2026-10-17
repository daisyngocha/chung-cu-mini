package vn.chungcumini.backend.bangiaotaisan;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.chungcumini.backend.domain.LoaiBanGiao;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.repository.TaiSanRepository;

@RestController
@RequestMapping("/api/hop-dong/{maHopDong}/ban-giao")
@PreAuthorize("hasRole('CHU_NHA')")
public class BanGiaoTaiSanController {

    private final BanGiaoTaiSanService banGiaoTaiSanService;
    private final HopDongService hopDongService;
    private final TaiSanRepository taiSanRepository;

    public BanGiaoTaiSanController(
            BanGiaoTaiSanService banGiaoTaiSanService, HopDongService hopDongService, TaiSanRepository taiSanRepository) {
        this.banGiaoTaiSanService = banGiaoTaiSanService;
        this.hopDongService = hopDongService;
        this.taiSanRepository = taiSanRepository;
    }

    @GetMapping
    public List<BanGiaoTaiSanResponse> danhSach(@PathVariable String maHopDong, @RequestParam LoaiBanGiao loaiBanGiao) {
        return banGiaoTaiSanService.layTheoHopDongVaLoai(maHopDong, loaiBanGiao).stream().map(BanGiaoTaiSanResponse::tu).toList();
    }

    @PostMapping
    public BanGiaoTaiSanResponse ghiNhanTinhTrang(@PathVariable String maHopDong, @Valid @RequestBody GhiNhanTinhTrangRequest req) {
        var hopDong = hopDongService.timHoacNem(maHopDong);
        var taiSan = taiSanRepository.findById(req.maTaiSan())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản \"" + req.maTaiSan() + "\"."));
        return BanGiaoTaiSanResponse.tu(banGiaoTaiSanService.ghiNhanTinhTrang(
                hopDong, taiSan, req.loaiBanGiao(), req.tinhTrang(), req.anh(), req.chiPhiHuHong()));
    }

    @GetMapping("/{maBanGiao}/so-sanh")
    public String soSanh(@PathVariable String maHopDong, @PathVariable String maBanGiao) {
        return banGiaoTaiSanService.soSanhLucNhanPhong(maBanGiao);
    }

    @GetMapping("/{maBanGiao}/chi-phi-hu-hong")
    public BigDecimal chiPhiHuHong(@PathVariable String maHopDong, @PathVariable String maBanGiao) {
        return banGiaoTaiSanService.tinhChiPhiHuHong(maBanGiao);
    }
}
