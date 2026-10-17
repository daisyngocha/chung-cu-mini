package vn.chungcumini.backend.nguoio;

import java.time.LocalDate;
import vn.chungcumini.backend.domain.NguoiO;

public record NguoiOResponse(
        String maNguoiO, String maHopDong, String hoTen, String soCCCD, String soDienThoai, LocalDate ngayVao, LocalDate ngayRa) {

    public static NguoiOResponse tu(NguoiO n) {
        return new NguoiOResponse(
                n.getMaNguoiO(), n.getHopDong().getMaHopDong(), n.getHoTen(), n.getSoCCCD(), n.getSoDienThoai(), n.getNgayVao(), n.getNgayRa());
    }
}
