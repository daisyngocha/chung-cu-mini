package vn.chungcumini.backend.hopdong;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.chungcumini.backend.nguoio.NguoiOResponse;
import vn.chungcumini.backend.nguoio.NguoiOService;

@RestController
@RequestMapping("/api/hop-dong")
@PreAuthorize("hasRole('CHU_NHA')")
public class HopDongController {

    private final HopDongService hopDongService;
    private final NguoiOService nguoiOService;

    public HopDongController(HopDongService hopDongService, NguoiOService nguoiOService) {
        this.hopDongService = hopDongService;
        this.nguoiOService = nguoiOService;
    }

    @GetMapping
    public List<HopDongSummaryResponse> danhSach() {
        return hopDongService.layDanhSach().stream().map(HopDongSummaryResponse::tu).toList();
    }

    @GetMapping("/{maHopDong}")
    public HopDongResponse chiTiet(@PathVariable String maHopDong) {
        return HopDongResponse.tu(hopDongService.timHoacNem(maHopDong));
    }

    @PostMapping
    public HopDongResponse taoMoi(@Valid @RequestBody TaoHopDongRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return HopDongResponse.tu(hopDongService.taoMoi(
                req.maPhong(),
                req.maKhach(),
                req.ngayBatDau(),
                req.ngayHetHan(),
                req.giaThue(),
                req.tienCoc(),
                req.dsDichVu(),
                req.mucPhatTraTruoc(),
                req.anhHopDong(),
                maTaiKhoan));
    }

    @PatchMapping("/{maHopDong}/nhan-phong")
    public HopDongResponse xacNhanNhanPhong(
            @PathVariable String maHopDong, @Valid @RequestBody XacNhanNhanPhongRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return HopDongResponse.tu(hopDongService.xacNhanNhanPhong(maHopDong, req.lyDo(), req.maNguoiOCapTK(), maTaiKhoan));
    }

    @GetMapping("/{maHopDong}/nguoi-o")
    public List<NguoiOResponse> danhSachNguoiO(@PathVariable String maHopDong) {
        return nguoiOService.layTheoHopDong(maHopDong).stream().map(NguoiOResponse::tu).toList();
    }

    @PostMapping("/{maHopDong}/nguoi-o")
    public NguoiOResponse themNguoiO(@PathVariable String maHopDong, @Valid @RequestBody ThemNguoiORequest req) {
        return NguoiOResponse.tu(
                hopDongService.themNguoiO(maHopDong, req.hoTen(), req.soCCCD(), req.soDienThoai(), req.ngayVao()));
    }

    @PatchMapping("/{maHopDong}/gia-han")
    public HopDongResponse giaHan(@PathVariable String maHopDong, @Valid @RequestBody GiaHanRequest req) {
        return HopDongResponse.tu(hopDongService.giaHan(maHopDong, req.ngayHetHanMoi(), req.giaMoi(), req.anhPhuLuc()));
    }

    @PatchMapping("/{maHopDong}/dieu-chinh-gia")
    public HopDongResponse dieuChinhGia(@PathVariable String maHopDong, @Valid @RequestBody DieuChinhGiaRequest req) {
        return HopDongResponse.tu(hopDongService.dieuChinhGia(maHopDong, req.giaMoi(), req.ngayApDung(), req.anhPhuLuc()));
    }

    @PatchMapping("/{maHopDong}/bao-tra")
    public ResponseEntity<HopDongResponse> baoTraPhong(@PathVariable String maHopDong, @Valid @RequestBody BaoTraPhongRequest req) {
        boolean traTruocHan = hopDongService.baoTraPhong(maHopDong, req.ngayTraDuKien(), req.loaiKetThuc());
        return ResponseEntity.ok()
                .header("X-Tra-Truoc-Han", String.valueOf(traTruocHan))
                .body(HopDongResponse.tu(hopDongService.timHoacNem(maHopDong)));
    }

    @GetMapping("/{maHopDong}/tinh-tien-phong")
    public java.math.BigDecimal tinhTienPhong(
            @PathVariable String maHopDong, @RequestParam LocalDate tuNgay, @RequestParam LocalDate denNgay) {
        return hopDongService.tinhTienPhong(maHopDong, tuNgay, denNgay);
    }

    @PatchMapping("/{maHopDong}/thu-hoi-chia-khoa")
    public HopDongResponse xacNhanThuHoiChiaKhoa(@PathVariable String maHopDong, @AuthenticationPrincipal String maTaiKhoan) {
        return HopDongResponse.tu(hopDongService.xacNhanThuHoiChiaKhoa(maHopDong, maTaiKhoan));
    }

    @PatchMapping("/{maHopDong}/thanh-ly")
    public HopDongResponse thanhLy(@PathVariable String maHopDong, @RequestBody ThanhLyRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return HopDongResponse.tu(hopDongService.thanhLy(maHopDong, req.boQuaCanhBaoChuaThuHoiChiaKhoa(), maTaiKhoan));
    }

    @GetMapping("/tam-tru")
    public ResponseEntity<byte[]> xuatDanhSachTamTru(
            @RequestParam LocalDate tuNgay, @RequestParam LocalDate denNgay, @AuthenticationPrincipal String maTaiKhoan) {
        byte[] file = hopDongService.xuatDanhSachTamTru(tuNgay, denNgay, maTaiKhoan);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"danh-sach-tam-tru.xlsx\"")
                .body(file);
    }
}
