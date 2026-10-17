package vn.chungcumini.backend.banggia;

import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.BangGia;
import vn.chungcumini.backend.domain.LoaiKhoan;

public record BangGiaResponse(
        String maKhoan,
        String tenKhoan,
        LoaiKhoan loaiKhoan,
        BigDecimal donGia,
        String donViTinh,
        LocalDate ngayApDung,
        LocalDate ngayHetHieuLuc) {

    public static BangGiaResponse tu(BangGia b) {
        return new BangGiaResponse(
                b.getMaKhoan(), b.getTenKhoan(), b.getLoaiKhoan(), b.getDonGia(), b.getDonViTinh(), b.getNgayApDung(), b.getNgayHetHieuLuc());
    }
}
