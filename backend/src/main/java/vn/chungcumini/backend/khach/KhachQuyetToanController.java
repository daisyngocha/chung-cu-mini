package vn.chungcumini.backend.khach;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.chungcumini.backend.domain.QuyetToan;
import vn.chungcumini.backend.domain.TrangThaiQT;
import vn.chungcumini.backend.quyettoan.DongQuyetToanResponse;
import vn.chungcumini.backend.quyettoan.KhachPhanHoiRequest;
import vn.chungcumini.backend.quyettoan.QuyetToanResponse;
import vn.chungcumini.backend.quyettoan.QuyetToanService;

/** KT-03.02: khách xem bảng quyết toán của phòng mình (chỉ sau khi chủ nhà gửi) và phản hồi đồng ý/không đồng ý. */
@RestController
@RequestMapping("/api/khach/quyet-toan")
@PreAuthorize("hasRole('KHACH_THUE')")
public class KhachQuyetToanController {

    private final QuyetToanService quyetToanService;

    public KhachQuyetToanController(QuyetToanService quyetToanService) {
        this.quyetToanService = quyetToanService;
    }

    @GetMapping
    public QuyetToanResponse xemCuaMinh(@AuthenticationPrincipal String maTaiKhoan) {
        QuyetToan qt = quyetToanService.timTheoTaiKhoanKhach(maTaiKhoan);
        if (qt.getTrangThai() == TrangThaiQT.NHAP) {
            throw new IllegalArgumentException("Chủ nhà chưa gửi quyết toán.");
        }
        var dong = quyetToanService.layDongTheoQuyetToan(qt.getMaQuyetToan()).stream().map(DongQuyetToanResponse::tu).toList();
        return QuyetToanResponse.tu(qt, dong);
    }

    @PatchMapping("/{maQuyetToan}/phan-hoi")
    public void phanHoi(
            @PathVariable String maQuyetToan, @Valid @RequestBody KhachPhanHoiRequest req, @AuthenticationPrincipal String maTaiKhoan) {
        quyetToanService.khachPhanHoi(maQuyetToan, req.dongY(), req.yKien(), maTaiKhoan);
    }
}
