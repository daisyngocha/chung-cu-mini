package vn.chungcumini.backend.phieuchi;

import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.HinhThucTT;
import vn.chungcumini.backend.domain.PhieuChi;

public record PhieuChiResponse(
        String soPhieuChi, String maQuyetToan, String maPhong, LocalDate ngayChi, String nguoiNhan, BigDecimal soTien, HinhThucTT hinhThuc) {

    public static PhieuChiResponse tu(PhieuChi p) {
        return new PhieuChiResponse(
                p.getSoPhieuChi(),
                p.getQuyetToan().getMaQuyetToan(),
                p.getQuyetToan().getHopDong().getPhong().getMaPhong(),
                p.getNgayChi(),
                p.getNguoiNhan(),
                p.getSoTien(),
                p.getHinhThuc());
    }
}
