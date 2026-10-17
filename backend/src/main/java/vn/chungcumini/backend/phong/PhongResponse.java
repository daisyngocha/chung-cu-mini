package vn.chungcumini.backend.phong;

import java.math.BigDecimal;
import vn.chungcumini.backend.domain.Phong;
import vn.chungcumini.backend.domain.TrangThaiPhong;

public record PhongResponse(
        String maPhong,
        int tang,
        BigDecimal dienTich,
        int soNguoiToiDa,
        BigDecimal giaNiemYet,
        boolean coCongToNuoc,
        TrangThaiPhong trangThai) {

    public static PhongResponse tu(Phong p) {
        return new PhongResponse(
                p.getMaPhong(),
                p.getTang(),
                p.getDienTich(),
                p.getSoNguoiToiDa(),
                p.getGiaNiemYet(),
                p.isCoCongToNuoc(),
                p.getTrangThai());
    }
}
