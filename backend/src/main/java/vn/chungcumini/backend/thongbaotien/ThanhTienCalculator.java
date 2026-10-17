package vn.chungcumini.backend.thongbaotien;

import java.math.BigDecimal;
import vn.chungcumini.backend.domain.LoaiKhoanTBT;

/** DongThongBaoTien.tinhThanhTien: soLuong × donGia × tyLeNgayO; GIAM_TRU mang dấu âm; KHÔNG làm tròn (BR-HD-05). */
final class ThanhTienCalculator {

    private ThanhTienCalculator() {
    }

    static BigDecimal tinh(LoaiKhoanTBT loaiKhoan, BigDecimal soLuong, BigDecimal donGia, BigDecimal tyLeNgayO) {
        BigDecimal thanhTien = soLuong.multiply(donGia).multiply(tyLeNgayO);
        return loaiKhoan == LoaiKhoanTBT.GIAM_TRU ? thanhTien.abs().negate() : thanhTien;
    }
}
