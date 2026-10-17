package vn.chungcumini.backend.taikhoan;

import vn.chungcumini.backend.domain.TaiKhoan;
import vn.chungcumini.backend.domain.TrangThaiTK;

public record TaiKhoanKhachResponse(
        String maTaiKhoan, String tenDangNhap, TrangThaiTK trangThai, String maHopDong, String maPhong, String tenKhach) {

    public static TaiKhoanKhachResponse tu(TaiKhoan t, String maHopDong, String maPhong, String tenKhach) {
        return new TaiKhoanKhachResponse(t.getMaTaiKhoan(), t.getTenDangNhap(), t.getTrangThai(), maHopDong, maPhong, tenKhach);
    }
}
