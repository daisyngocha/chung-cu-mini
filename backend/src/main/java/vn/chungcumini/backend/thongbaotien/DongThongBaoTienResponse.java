package vn.chungcumini.backend.thongbaotien;

import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.DongThongBaoTien;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;

public record DongThongBaoTienResponse(
        String maDong,
        LoaiKhoanTBT loaiKhoan,
        String dienGiai,
        LocalDate tuNgay,
        LocalDate denNgay,
        BigDecimal soLuong,
        BigDecimal donGia,
        BigDecimal tyLeNgayO,
        BigDecimal thanhTien) {

    public static DongThongBaoTienResponse tu(DongThongBaoTien d) {
        return new DongThongBaoTienResponse(
                d.getMaDong(), d.getLoaiKhoan(), d.getDienGiai(), d.getTuNgay(), d.getDenNgay(),
                d.getSoLuong(), d.getDonGia(), d.getTyLeNgayO(), d.getThanhTien());
    }
}
