package vn.chungcumini.backend.khach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiKetThuc;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.nguoio.NguoiOResponse;

/** Hồ sơ & hợp đồng của khách (KT-01.01, KT-03.01) — không lộ ảnh CCCD. */
public record KhachHopDongResponse(
        String maHopDong,
        String maPhong,
        int tang,
        BigDecimal dienTich,
        boolean coCongToNuoc,
        String tenKhach,
        String soCCCDKhach,
        LocalDate ngayBatDau,
        LocalDate ngayHetHan,
        LocalDate ngayNhanPhong,
        BigDecimal giaThue,
        BigDecimal tienCoc,
        String anhHopDong,
        TrangThaiHD trangThai,
        LocalDate ngayBaoTra,
        LocalDate ngayTraDuKien,
        LoaiKetThuc loaiKetThuc,
        boolean daThuHoiChiaKhoa,
        List<NguoiOResponse> dsNguoiO) {

    public static KhachHopDongResponse tu(HopDong h, List<NguoiOResponse> dsNguoiO) {
        return new KhachHopDongResponse(
                h.getMaHopDong(),
                h.getPhong().getMaPhong(),
                h.getPhong().getTang(),
                h.getPhong().getDienTich(),
                h.getPhong().isCoCongToNuoc(),
                h.getKhachThue().getHoTen(),
                h.getKhachThue().getSoCCCD(),
                h.getNgayBatDau(),
                h.getNgayHetHan(),
                h.getNgayNhanPhong(),
                h.getGiaThue(),
                h.getTienCoc(),
                h.getAnhHopDong(),
                h.getTrangThai(),
                h.getNgayBaoTra(),
                h.getNgayTraDuKien(),
                h.getLoaiKetThuc(),
                h.isDaThuHoiChiaKhoa(),
                dsNguoiO);
    }
}
