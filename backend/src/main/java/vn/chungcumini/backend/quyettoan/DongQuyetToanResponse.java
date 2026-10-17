package vn.chungcumini.backend.quyettoan;

import java.math.BigDecimal;
import vn.chungcumini.backend.domain.DongQuyetToan;
import vn.chungcumini.backend.domain.LoaiKhoanQT;

public record DongQuyetToanResponse(String maDong, LoaiKhoanQT loaiKhoan, BigDecimal soTien, String ghiChu, String anhBangChung) {

    public static DongQuyetToanResponse tu(DongQuyetToan d) {
        return new DongQuyetToanResponse(d.getMaDong(), d.getLoaiKhoan(), d.getSoTien(), d.getGhiChu(), d.getAnhBangChung());
    }
}
