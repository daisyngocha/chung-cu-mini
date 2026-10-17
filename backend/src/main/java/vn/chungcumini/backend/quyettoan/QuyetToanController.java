package vn.chungcumini.backend.quyettoan;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.chungcumini.backend.phieuchi.PhieuChiResponse;
import vn.chungcumini.backend.thongbaotien.DongThongBaoTienResponse;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienResponse;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;

/** Chủ nhà: lập/điều chỉnh/chốt quyết toán cọc khi trả phòng. Khách phản hồi — xem KhachQuyetToanController. */
@RestController
@PreAuthorize("hasRole('CHU_NHA')")
public class QuyetToanController {

    private final QuyetToanService quyetToanService;
    private final ThongBaoTienService thongBaoTienService;

    public QuyetToanController(QuyetToanService quyetToanService, ThongBaoTienService thongBaoTienService) {
        this.quyetToanService = quyetToanService;
        this.thongBaoTienService = thongBaoTienService;
    }

    @PostMapping("/api/hop-dong/{maHopDong}/quyet-toan")
    public QuyetToanResponse tinhQuyetToan(@PathVariable String maHopDong, @AuthenticationPrincipal String maTaiKhoan) {
        return chiTiet(quyetToanService.tinhQuyetToan(maHopDong, maTaiKhoan).getMaQuyetToan());
    }

    @GetMapping("/api/hop-dong/{maHopDong}/quyet-toan")
    public QuyetToanResponse chiTietTheoHopDong(@PathVariable String maHopDong) {
        return chiTiet(quyetToanService.timTheoHopDong(maHopDong).getMaQuyetToan());
    }

    @GetMapping("/api/quyet-toan/{maQuyetToan}")
    public QuyetToanResponse chiTiet(@PathVariable String maQuyetToan) {
        var qt = quyetToanService.timHoacNem(maQuyetToan);
        var dong = quyetToanService.layDongTheoQuyetToan(maQuyetToan).stream().map(DongQuyetToanResponse::tu).toList();
        return QuyetToanResponse.tu(qt, dong);
    }

    @PostMapping("/api/quyet-toan/{maQuyetToan}/khoan-tru")
    public DongQuyetToanResponse themKhoanTru(
            @PathVariable String maQuyetToan, @Valid @RequestBody ThemKhoanTruRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return DongQuyetToanResponse.tu(quyetToanService.themKhoanTru(
                maQuyetToan, req.loaiKhoan(), req.soTien(), req.ghiChu(), req.anhBangChung(), req.lyDoDieuChinh(), maTaiKhoan));
    }

    @PatchMapping("/api/quyet-toan/{maQuyetToan}/khoan-tru/{maDong}")
    public DongQuyetToanResponse suaKhoanTru(
            @PathVariable String maQuyetToan,
            @PathVariable String maDong,
            @Valid @RequestBody SuaKhoanTruRequest req,
            @AuthenticationPrincipal String maTaiKhoan) {
        return DongQuyetToanResponse.tu(quyetToanService.suaKhoanTru(
                maQuyetToan, maDong, req.soTien(), req.ghiChu(), req.anhBangChung(), req.lyDo(), maTaiKhoan));
    }

    @DeleteMapping("/api/quyet-toan/{maQuyetToan}/khoan-tru/{maDong}")
    public void xoaKhoanTru(
            @PathVariable String maQuyetToan,
            @PathVariable String maDong,
            @RequestBody XoaKhoanTruRequest req,
            @AuthenticationPrincipal String maTaiKhoan) {
        quyetToanService.xoaKhoanTru(maQuyetToan, maDong, req.lyDo(), maTaiKhoan);
    }

    @PatchMapping("/api/quyet-toan/{maQuyetToan}/gui-khach")
    public void guiKhach(@PathVariable String maQuyetToan, @AuthenticationPrincipal String maTaiKhoan) {
        quyetToanService.guiKhach(maQuyetToan, maTaiKhoan);
    }

    @PatchMapping("/api/quyet-toan/{maQuyetToan}/chot")
    public void chotQuyetToan(
            @PathVariable String maQuyetToan, @RequestBody ChotQuyetToanRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        quyetToanService.chotQuyetToan(maQuyetToan, req.lyDo(), maTaiKhoan);
    }

    @PostMapping("/api/quyet-toan/{maQuyetToan}/phieu-chi")
    public PhieuChiResponse lapPhieuChi(
            @PathVariable String maQuyetToan, @Valid @RequestBody LapPhieuChiRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        return PhieuChiResponse.tu(quyetToanService.lapPhieuChi(maQuyetToan, req.hinhThuc(), maTaiKhoan));
    }

    @PostMapping("/api/quyet-toan/{maQuyetToan}/thong-bao-thu-them")
    public ThongBaoTienResponse lapThongBaoThuThem(@PathVariable String maQuyetToan, @AuthenticationPrincipal String maTaiKhoan) {
        var tb = quyetToanService.lapThongBaoThuThem(maQuyetToan, maTaiKhoan);
        var dong = thongBaoTienService.layDongTheoThongBao(tb.getMaThongBaoTien()).stream().map(DongThongBaoTienResponse::tu).toList();
        return ThongBaoTienResponse.tu(tb, dong);
    }
}
