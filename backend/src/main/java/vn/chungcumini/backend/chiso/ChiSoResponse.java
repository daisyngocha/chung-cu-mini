package vn.chungcumini.backend.chiso;

import java.time.LocalDate;
import vn.chungcumini.backend.domain.ChiSo;
import vn.chungcumini.backend.domain.LoaiCongTo;
import vn.chungcumini.backend.domain.LoaiGhi;

public record ChiSoResponse(
        String maChiSo,
        String maHopDong,
        LoaiCongTo loaiCongTo,
        String kyThang,
        LoaiGhi loaiGhi,
        LocalDate ngayGhi,
        int chiSoCu,
        int chiSoMoi,
        int tieuThu,
        String anhCongTo) {

    public static ChiSoResponse tu(ChiSo c) {
        return new ChiSoResponse(
                c.getMaChiSo(),
                c.getHopDong().getMaHopDong(),
                c.getLoaiCongTo(),
                c.getKyThang(),
                c.getLoaiGhi(),
                c.getNgayGhi(),
                c.getChiSoCu(),
                c.getChiSoMoi(),
                c.getChiSoMoi() - c.getChiSoCu(),
                c.getAnhCongTo());
    }
}
