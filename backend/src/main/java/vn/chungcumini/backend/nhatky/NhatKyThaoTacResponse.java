package vn.chungcumini.backend.nhatky;

import java.time.LocalDateTime;
import vn.chungcumini.backend.domain.NhatKyThaoTac;

public record NhatKyThaoTacResponse(
        String maNhatKy,
        String maTaiKhoan,
        LocalDateTime thoiDiem,
        String doiTuong,
        String maDoiTuong,
        String hanhDong,
        String noiDung) {

    public static NhatKyThaoTacResponse tu(NhatKyThaoTac n) {
        return new NhatKyThaoTacResponse(
                n.getMaNhatKy(), n.getMaTaiKhoan(), n.getThoiDiem(), n.getDoiTuong(), n.getMaDoiTuong(), n.getHanhDong(), n.getNoiDung());
    }
}
