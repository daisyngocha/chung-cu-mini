package vn.chungcumini.backend.phieuthu;

import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.HinhThucTT;
import vn.chungcumini.backend.domain.PhieuThu;
import vn.chungcumini.backend.domain.TrangThaiPT;

public record PhieuThuResponse(
        String soPhieuThu,
        String maThongBaoTien,
        String maPhong,
        String maBaoTT,
        LocalDate ngayThu,
        String nguoiNop,
        BigDecimal soTien,
        HinhThucTT hinhThuc,
        TrangThaiPT trangThai,
        String lyDoHuy) {

    public static PhieuThuResponse tu(PhieuThu p) {
        return new PhieuThuResponse(
                p.getSoPhieuThu(),
                p.getThongBaoTien().getMaThongBaoTien(),
                p.getThongBaoTien().getHopDong().getPhong().getMaPhong(),
                p.getBaoThanhToan() != null ? p.getBaoThanhToan().getMaBaoTT() : null,
                p.getNgayThu(),
                p.getNguoiNop(),
                p.getSoTien(),
                p.getHinhThuc(),
                p.getTrangThai(),
                p.getLyDoHuy());
    }
}
