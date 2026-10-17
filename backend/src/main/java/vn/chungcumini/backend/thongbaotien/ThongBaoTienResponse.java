package vn.chungcumini.backend.thongbaotien;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import vn.chungcumini.backend.domain.LoaiThongBao;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiTBT;

public record ThongBaoTienResponse(
        String maThongBaoTien,
        String maHopDong,
        String maPhong,
        LoaiThongBao loaiThongBao,
        String kyThang,
        LocalDate ngayPhatHanh,
        LocalDate hanThanhToan,
        BigDecimal noCu,
        BigDecimal tongTien,
        BigDecimal soDaThu,
        TrangThaiTBT trangThai,
        String lyDoHuy,
        List<DongThongBaoTienResponse> dong) {

    public static ThongBaoTienResponse tu(ThongBaoTien t, List<DongThongBaoTienResponse> dong) {
        return new ThongBaoTienResponse(
                t.getMaThongBaoTien(),
                t.getHopDong().getMaHopDong(),
                t.getHopDong().getPhong().getMaPhong(),
                t.getLoaiThongBao(),
                t.getKyThang(),
                t.getNgayPhatHanh(),
                t.getHanThanhToan(),
                t.getNoCu(),
                t.getTongTien(),
                t.getSoDaThu(),
                t.getTrangThai(),
                t.getLyDoHuy(),
                dong);
    }
}
