package vn.chungcumini.backend.hopdong;

import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.TrangThaiHD;

public record HopDongSummaryResponse(
        String maHopDong, String maPhong, LocalDate ngayBatDau, LocalDate ngayHetHan, BigDecimal giaThue, TrangThaiHD trangThai) {

    public static HopDongSummaryResponse tu(HopDong h) {
        return new HopDongSummaryResponse(
                h.getMaHopDong(), h.getPhong().getMaPhong(), h.getNgayBatDau(), h.getNgayHetHan(), h.getGiaThue(), h.getTrangThai());
    }
}
