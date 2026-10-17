package vn.chungcumini.backend.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.PhieuThu;
import vn.chungcumini.backend.domain.TrangThaiPT;

public interface PhieuThuRepository extends JpaRepository<PhieuThu, String> {

    @Query(
            "SELECT p FROM PhieuThu p JOIN FETCH p.thongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong ph "
                    + "JOIN FETCH ph.toaNha JOIN FETCH h.khachThue WHERE p.soPhieuThu = :soPhieuThu")
    Optional<PhieuThu> timChiTiet(@Param("soPhieuThu") String soPhieuThu);

    @Query(
            "SELECT p FROM PhieuThu p JOIN FETCH p.thongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong "
                    + "WHERE t.maThongBaoTien = :maThongBaoTien ORDER BY p.ngayThu DESC")
    List<PhieuThu> findByThongBaoTien_MaThongBaoTien(@Param("maThongBaoTien") String maThongBaoTien);

    @Query("SELECT COALESCE(SUM(p.soTien), 0) FROM PhieuThu p WHERE p.thongBaoTien.maThongBaoTien = :maThongBaoTien AND p.trangThai = 'HIEU_LUC'")
    BigDecimal tongDaThuHieuLuc(@Param("maThongBaoTien") String maThongBaoTien);

    long countBySoPhieuThuStartingWith(String tienTo);

    @Query(
            "SELECT p FROM PhieuThu p JOIN FETCH p.thongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong "
                    + "WHERE h.maTaiKhoan = :maTaiKhoan ORDER BY p.ngayThu DESC")
    List<PhieuThu> findByThongBaoTien_HopDong_MaTaiKhoanOrderByNgayThuDesc(@Param("maTaiKhoan") String maTaiKhoan);
}
