package vn.chungcumini.backend.thongbaotien;

import java.math.BigDecimal;
import vn.chungcumini.backend.domain.TrangThaiTBT;

public record TongHopKyResponse(
        String maThongBaoTien,
        String maPhong,
        BigDecimal dien,
        BigDecimal nuoc,
        BigDecimal dichVu,
        BigDecimal tienPhong,
        BigDecimal phatSinh,
        BigDecimal noCu,
        BigDecimal tongTien,
        TrangThaiTBT trangThai) {
}
