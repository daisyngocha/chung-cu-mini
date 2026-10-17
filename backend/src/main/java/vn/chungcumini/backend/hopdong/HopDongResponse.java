package vn.chungcumini.backend.hopdong;

import java.math.BigDecimal;
import java.time.LocalDate;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiKetThuc;
import vn.chungcumini.backend.domain.TrangThaiHD;

public record HopDongResponse(
        String maHopDong,
        String maPhong,
        String maKhach,
        String tenKhach,
        String maTaiKhoan,
        LocalDate ngayBatDau,
        LocalDate ngayHetHan,
        BigDecimal giaThue,
        BigDecimal tienCoc,
        BigDecimal mucPhatTraTruoc,
        String anhHopDong,
        LocalDate ngayNhanPhong,
        LocalDate ngayBaoTra,
        LocalDate ngayTraDuKien,
        LocalDate ngayTraThucTe,
        TrangThaiHD trangThai,
        String lyDoNhanPhongChuaThuDu,
        LoaiKetThuc loaiKetThuc,
        boolean daThuHoiChiaKhoa) {

    public static HopDongResponse tu(HopDong h) {
        return new HopDongResponse(
                h.getMaHopDong(),
                h.getPhong().getMaPhong(),
                h.getKhachThue().getMaKhach(),
                h.getKhachThue().getHoTen(),
                h.getMaTaiKhoan(),
                h.getNgayBatDau(),
                h.getNgayHetHan(),
                h.getGiaThue(),
                h.getTienCoc(),
                h.getMucPhatTraTruoc(),
                h.getAnhHopDong(),
                h.getNgayNhanPhong(),
                h.getNgayBaoTra(),
                h.getNgayTraDuKien(),
                h.getNgayTraThucTe(),
                h.getTrangThai(),
                h.getLyDoNhanPhongChuaThuDu(),
                h.getLoaiKetThuc(),
                h.isDaThuHoiChiaKhoa());
    }
}
