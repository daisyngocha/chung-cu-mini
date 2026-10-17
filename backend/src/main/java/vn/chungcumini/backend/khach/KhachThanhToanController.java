package vn.chungcumini.backend.khach;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.chungcumini.backend.baothanhtoan.BaoThanhToanResponse;
import vn.chungcumini.backend.baothanhtoan.BaoThanhToanService;
import vn.chungcumini.backend.baothanhtoan.GuiBaoRequest;
import vn.chungcumini.backend.domain.PhieuThu;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.phieuthu.PhieuThuResponse;
import vn.chungcumini.backend.phieuthu.PhieuThuService;
import vn.chungcumini.backend.thongbaotien.DongThongBaoTienResponse;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienResponse;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;
import vn.chungcumini.backend.thongbaotien.VietQRResponse;

/**
 * Mục 11: mọi truy vấn của khách lọc theo phòng đang thuê ở tầng dữ liệu. Các danh sách đã lọc sẵn
 * theo maTaiKhoan trong repository; các truy vấn theo-mã (chi tiết, PDF, QR) kiểm tra quyền sở hữu
 * ở đây trước khi trả dữ liệu.
 */
@RestController
@RequestMapping("/api/khach")
@PreAuthorize("hasRole('KHACH_THUE')")
public class KhachThanhToanController {

    private final ThongBaoTienService thongBaoTienService;
    private final PhieuThuService phieuThuService;
    private final BaoThanhToanService baoThanhToanService;

    public KhachThanhToanController(
            ThongBaoTienService thongBaoTienService, PhieuThuService phieuThuService, BaoThanhToanService baoThanhToanService) {
        this.thongBaoTienService = thongBaoTienService;
        this.phieuThuService = phieuThuService;
        this.baoThanhToanService = baoThanhToanService;
    }

    @GetMapping("/thong-bao-tien")
    public List<ThongBaoTienResponse> danhSachThongBaoTien(@AuthenticationPrincipal String maTaiKhoan) {
        return thongBaoTienService.layTheoTaiKhoanKhach(maTaiKhoan).stream()
                .map(t -> ThongBaoTienResponse.tu(t, List.of()))
                .toList();
    }

    @GetMapping("/thong-bao-tien/{maThongBaoTien}")
    public ThongBaoTienResponse chiTietThongBaoTien(@PathVariable String maThongBaoTien, @AuthenticationPrincipal String maTaiKhoan) {
        ThongBaoTien tb = kiemTraChuPhong(thongBaoTienService.timHoacNem(maThongBaoTien), maTaiKhoan);
        var dong = thongBaoTienService.layDongTheoThongBao(maThongBaoTien).stream().map(DongThongBaoTienResponse::tu).toList();
        return ThongBaoTienResponse.tu(tb, dong);
    }

    @GetMapping("/thong-bao-tien/{maThongBaoTien}/viet-qr")
    public VietQRResponse vietQR(@PathVariable String maThongBaoTien, @AuthenticationPrincipal String maTaiKhoan) {
        kiemTraChuPhong(thongBaoTienService.timHoacNem(maThongBaoTien), maTaiKhoan);
        return new VietQRResponse(thongBaoTienService.taoMaVietQR(maThongBaoTien));
    }

    @GetMapping("/phieu-thu")
    public List<PhieuThuResponse> danhSachPhieuThu(@AuthenticationPrincipal String maTaiKhoan) {
        return phieuThuService.layTheoTaiKhoanKhach(maTaiKhoan).stream().map(PhieuThuResponse::tu).toList();
    }

    @GetMapping("/phieu-thu/{soPhieuThu}")
    public PhieuThuResponse chiTietPhieuThu(@PathVariable String soPhieuThu, @AuthenticationPrincipal String maTaiKhoan) {
        return PhieuThuResponse.tu(kiemTraChuPhong(phieuThuService.timHoacNem(soPhieuThu), maTaiKhoan));
    }

    @GetMapping(value = "/phieu-thu/{soPhieuThu}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> xuatPdfPhieuThu(@PathVariable String soPhieuThu, @AuthenticationPrincipal String maTaiKhoan) {
        kiemTraChuPhong(phieuThuService.timHoacNem(soPhieuThu), maTaiKhoan);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + soPhieuThu + ".pdf\"")
                .body(phieuThuService.xuatPDF(soPhieuThu));
    }

    @GetMapping("/bao-thanh-toan")
    public List<BaoThanhToanResponse> danhSachBaoThanhToan(@AuthenticationPrincipal String maTaiKhoan) {
        return baoThanhToanService.layTheoTaiKhoanKhach(maTaiKhoan).stream().map(BaoThanhToanResponse::tu).toList();
    }

    @PostMapping("/bao-thanh-toan")
    public BaoThanhToanResponse guiBao(@Valid @RequestBody GuiBaoRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return BaoThanhToanResponse.tu(baoThanhToanService.guiBao(req.maThongBaoTien(), maTaiKhoan, req.anhBienLai()));
    }

    private static ThongBaoTien kiemTraChuPhong(ThongBaoTien tb, String maTaiKhoan) {
        if (!maTaiKhoan.equals(tb.getHopDong().getMaTaiKhoan())) {
            throw new IllegalArgumentException("Thông báo tiền không thuộc phòng của bạn.");
        }
        return tb;
    }

    private static PhieuThu kiemTraChuPhong(PhieuThu phieuThu, String maTaiKhoan) {
        if (!maTaiKhoan.equals(phieuThu.getThongBaoTien().getHopDong().getMaTaiKhoan())) {
            throw new IllegalArgumentException("Phiếu thu không thuộc phòng của bạn.");
        }
        return phieuThu;
    }
}
