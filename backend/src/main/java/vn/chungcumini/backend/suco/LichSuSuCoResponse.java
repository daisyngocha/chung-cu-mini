package vn.chungcumini.backend.suco;

import java.time.LocalDateTime;
import vn.chungcumini.backend.domain.LichSuSuCo;
import vn.chungcumini.backend.domain.TrangThaiSuCo;
import vn.chungcumini.backend.domain.VaiTro;

public record LichSuSuCoResponse(
        String maLichSu, LocalDateTime thoiDiem, TrangThaiSuCo trangThaiCu, TrangThaiSuCo trangThaiMoi, VaiTro vaiTroThucHien, String ghiChu) {

    public static LichSuSuCoResponse tu(LichSuSuCo l) {
        return new LichSuSuCoResponse(l.getMaLichSu(), l.getThoiDiem(), l.getTrangThaiCu(), l.getTrangThaiMoi(), l.getVaiTroThucHien(), l.getGhiChu());
    }
}
