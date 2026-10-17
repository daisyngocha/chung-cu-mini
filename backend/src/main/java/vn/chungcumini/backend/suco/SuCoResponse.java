package vn.chungcumini.backend.suco;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import vn.chungcumini.backend.domain.LoaiSuCo;
import vn.chungcumini.backend.domain.SuCo;
import vn.chungcumini.backend.domain.TrangThaiSuCo;

public record SuCoResponse(
        String maSuCo,
        String maPhong,
        LoaiSuCo loaiSuCo,
        String moTa,
        String anh,
        LocalDateTime ngayBao,
        LocalDateTime lichHen,
        String ketQua,
        BigDecimal chiPhi,
        boolean doKhachGay,
        TrangThaiSuCo trangThai,
        String lyDoTuChoi) {

    public static SuCoResponse tu(SuCo s) {
        return new SuCoResponse(
                s.getMaSuCo(),
                s.getPhong().getMaPhong(),
                s.getLoaiSuCo(),
                s.getMoTa(),
                s.getAnh(),
                s.getNgayBao(),
                s.getLichHen(),
                s.getKetQua(),
                s.getChiPhi(),
                s.isDoKhachGay(),
                s.getTrangThai(),
                s.getLyDoTuChoi());
    }
}
