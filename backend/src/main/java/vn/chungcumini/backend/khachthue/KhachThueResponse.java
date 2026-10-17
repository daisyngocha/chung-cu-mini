package vn.chungcumini.backend.khachthue;

import java.time.LocalDate;
import vn.chungcumini.backend.domain.KhachThue;

public record KhachThueResponse(
        String maKhach,
        String soDienThoai,
        String hoTen,
        LocalDate ngaySinh,
        String soCCCD,
        String anhCCCD,
        String queQuan,
        String noiLamViec) {

    public static KhachThueResponse tu(KhachThue k) {
        return new KhachThueResponse(
                k.getMaKhach(), k.getSoDienThoai(), k.getHoTen(), k.getNgaySinh(), k.getSoCCCD(), k.getAnhCCCD(), k.getQueQuan(), k.getNoiLamViec());
    }
}
