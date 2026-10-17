package vn.chungcumini.backend.bangiaotaisan;

import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.BanGiaoTaiSan;
import vn.chungcumini.backend.domain.LoaiBanGiao;

public record BanGiaoTaiSanResponse(
        String maBanGiao,
        String maHopDong,
        String maTaiSan,
        String tenTaiSan,
        LoaiBanGiao loaiBanGiao,
        LocalDate ngayBanGiao,
        String tinhTrang,
        String anh,
        BigDecimal chiPhiHuHong) {

    public static BanGiaoTaiSanResponse tu(BanGiaoTaiSan b) {
        return new BanGiaoTaiSanResponse(
                b.getMaBanGiao(),
                b.getHopDong().getMaHopDong(),
                b.getTaiSan().getMaTaiSan(),
                b.getTaiSan().getTenTaiSan(),
                b.getLoaiBanGiao(),
                b.getNgayBanGiao(),
                b.getTinhTrang(),
                b.getAnh(),
                b.getChiPhiHuHong());
    }
}
