package vn.chungcumini.backend.khoitao;

/** DichVuKhoiTao.nhapDuLieuDangVanHanh: trả về List<(HopDong, TaiKhoan, matKhauTam)>. */
public record KhoiTaoKetQuaItem(String maHopDong, String maPhong, String maTaiKhoan, String tenDangNhap, String matKhauTam) {
}
