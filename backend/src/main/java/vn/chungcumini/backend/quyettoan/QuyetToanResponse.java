package vn.chungcumini.backend.quyettoan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import vn.chungcumini.backend.domain.QuyetToan;
import vn.chungcumini.backend.domain.TrangThaiQT;

public record QuyetToanResponse(
        String maQuyetToan,
        String maHopDong,
        String maPhong,
        LocalDate ngayTra,
        BigDecimal tienCoc,
        BigDecimal tienPhongHoanLai,
        BigDecimal tongKhauTru,
        BigDecimal soTienQuyetToan,
        TrangThaiQT trangThai,
        String lyDoDieuChinh,
        Boolean khachDongY,
        String yKienKhach,
        LocalDateTime thoiDiemPhanHoi,
        List<DongQuyetToanResponse> dong) {

    public static QuyetToanResponse tu(QuyetToan q, List<DongQuyetToanResponse> dong) {
        return new QuyetToanResponse(
                q.getMaQuyetToan(),
                q.getHopDong().getMaHopDong(),
                q.getHopDong().getPhong().getMaPhong(),
                q.getNgayTra(),
                q.getTienCoc(),
                q.getTienPhongHoanLai(),
                q.getTongKhauTru(),
                q.getSoTienQuyetToan(),
                q.getTrangThai(),
                q.getLyDoDieuChinh(),
                q.getKhachDongY(),
                q.getYKienKhach(),
                q.getThoiDiemPhanHoi(),
                dong);
    }
}
