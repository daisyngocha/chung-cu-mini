package vn.chungcumini.backend.thongbao;

import java.time.LocalDateTime;
import vn.chungcumini.backend.domain.LoaiSuKien;
import vn.chungcumini.backend.domain.NguonTB;
import vn.chungcumini.backend.domain.PhamViTB;
import vn.chungcumini.backend.domain.ThongBaoNguoiNhan;

public record ThongBaoNguoiNhanResponse(
        String maThongBao,
        LoaiSuKien loaiSuKien,
        String tieuDe,
        String noiDung,
        NguonTB nguon,
        PhamViTB phamVi,
        String doiTuongLienQuan,
        LocalDateTime ngayTao,
        boolean daDoc,
        LocalDateTime thoiDiemDoc) {

    public static ThongBaoNguoiNhanResponse tu(ThongBaoNguoiNhan n) {
        var tb = n.getThongBao();
        return new ThongBaoNguoiNhanResponse(
                tb.getMaThongBao(), tb.getLoaiSuKien(), tb.getTieuDe(), tb.getNoiDung(), tb.getNguon(), tb.getPhamVi(),
                tb.getDoiTuongLienQuan(), tb.getNgayTao(), n.isDaDoc(), n.getThoiDiemDoc());
    }
}
