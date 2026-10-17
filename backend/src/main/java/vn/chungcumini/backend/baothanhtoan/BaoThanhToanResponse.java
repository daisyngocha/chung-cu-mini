package vn.chungcumini.backend.baothanhtoan;

import java.time.LocalDateTime;
import vn.chungcumini.backend.domain.BaoThanhToan;
import vn.chungcumini.backend.domain.TrangThaiBaoTT;

public record BaoThanhToanResponse(
        String maBaoTT,
        String maThongBaoTien,
        String maPhong,
        LocalDateTime thoiDiemBao,
        String anhBienLai,
        TrangThaiBaoTT trangThai,
        String lyDoTuChoi) {

    public static BaoThanhToanResponse tu(BaoThanhToan b) {
        return new BaoThanhToanResponse(
                b.getMaBaoTT(),
                b.getThongBaoTien().getMaThongBaoTien(),
                b.getThongBaoTien().getHopDong().getPhong().getMaPhong(),
                b.getThoiDiemBao(),
                b.getAnhBienLai(),
                b.getTrangThai(),
                b.getLyDoTuChoi());
    }
}
