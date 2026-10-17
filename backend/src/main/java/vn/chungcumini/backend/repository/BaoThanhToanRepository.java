package vn.chungcumini.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.BaoThanhToan;
import vn.chungcumini.backend.domain.TrangThaiBaoTT;

public interface BaoThanhToanRepository extends JpaRepository<BaoThanhToan, String> {

    @Query(
            "SELECT b FROM BaoThanhToan b JOIN FETCH b.thongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong "
                    + "JOIN FETCH h.khachThue WHERE b.maBaoTT = :maBaoTT")
    Optional<BaoThanhToan> timChiTiet(@Param("maBaoTT") String maBaoTT);

    boolean existsByThongBaoTien_MaThongBaoTienAndTrangThai(String maThongBaoTien, TrangThaiBaoTT trangThai);

    @Query(
            "SELECT b FROM BaoThanhToan b JOIN FETCH b.thongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong "
                    + "WHERE h.maTaiKhoan = :maTaiKhoan ORDER BY b.thoiDiemBao DESC")
    List<BaoThanhToan> findByThongBaoTien_HopDong_MaTaiKhoanOrderByThoiDiemBaoDesc(@Param("maTaiKhoan") String maTaiKhoan);

    @Query(
            "SELECT b FROM BaoThanhToan b JOIN FETCH b.thongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong "
                    + "WHERE b.trangThai = 'CHO_XAC_NHAN' ORDER BY b.thoiDiemBao")
    List<BaoThanhToan> timHangChoXacNhan();
}
