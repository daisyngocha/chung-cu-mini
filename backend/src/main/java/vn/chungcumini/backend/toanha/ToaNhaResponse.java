package vn.chungcumini.backend.toanha;

import vn.chungcumini.backend.domain.ToaNha;

public record ToaNhaResponse(
        String maToa,
        String tenToa,
        String diaChi,
        String noiQuy,
        int ngayChotChiSo,
        int hanThanhToan,
        String nganHang,
        String soTaiKhoan,
        String chuTaiKhoan,
        String tenChuNha,
        String sdtChuNha,
        String diaChiChuNha) {

    public static ToaNhaResponse tu(ToaNha t) {
        return new ToaNhaResponse(
                t.getMaToa(),
                t.getTenToa(),
                t.getDiaChi(),
                t.getNoiQuy(),
                t.getNgayChotChiSo(),
                t.getHanThanhToan(),
                t.getNganHang(),
                t.getSoTaiKhoan(),
                t.getChuTaiKhoan(),
                t.getTenChuNha(),
                t.getSdtChuNha(),
                t.getDiaChiChuNha());
    }
}
