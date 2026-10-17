package vn.chungcumini.backend.taisan;

import vn.chungcumini.backend.domain.TaiSan;

public record TaiSanResponse(String maTaiSan, String maPhong, String tenTaiSan, int soLuong, String tinhTrang, String ghiChu) {

    public static TaiSanResponse tu(TaiSan t) {
        return new TaiSanResponse(
                t.getMaTaiSan(), t.getPhong().getMaPhong(), t.getTenTaiSan(), t.getSoLuong(), t.getTinhTrang(), t.getGhiChu());
    }
}
