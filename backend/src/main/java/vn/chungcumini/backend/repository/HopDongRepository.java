package vn.chungcumini.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.TrangThaiHD;

public interface HopDongRepository extends JpaRepository<HopDong, String> {

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HopDong h WHERE h.phong.maPhong = :maPhong")
    boolean existsByMaPhong(@Param("maPhong") String maPhong);

    @Query("SELECT h FROM HopDong h WHERE h.phong.maPhong = :maPhong AND h.trangThai IN :trangThai")
    List<HopDong> findByMaPhongAndTrangThaiIn(@Param("maPhong") String maPhong, @Param("trangThai") List<TrangThaiHD> trangThai);

    @Query("SELECT h FROM HopDong h JOIN FETCH h.phong WHERE h.khachThue.maKhach = :maKhach ORDER BY h.ngayBatDau DESC")
    List<HopDong> findByKhachThue_MaKhachOrderByNgayBatDauDesc(@Param("maKhach") String maKhach);

    List<HopDong> findByNgayNhanPhongBetween(java.time.LocalDate tuNgay, java.time.LocalDate denNgay);

    /** Nạp sẵn phong + khachThue — tránh LazyInitializationException khi map sang DTO ngoài transaction. */
    @Query("SELECT h FROM HopDong h JOIN FETCH h.phong JOIN FETCH h.khachThue WHERE h.maHopDong = :maHopDong")
    Optional<HopDong> timChiTiet(@Param("maHopDong") String maHopDong);

    @Query("SELECT h FROM HopDong h JOIN FETCH h.phong JOIN FETCH h.khachThue")
    List<HopDong> findAllChiTiet();

    @Query("SELECT h FROM HopDong h JOIN FETCH h.phong WHERE h.trangThai = :trangThai")
    List<HopDong> findByTrangThaiWithPhong(@Param("trangThai") TrangThaiHD trangThai);

    /** Mục 11: mọi truy vấn của khách lọc theo phòng đang thuê — HĐ hiệu lực gắn với tài khoản đang đăng nhập. */
    @Query("SELECT h FROM HopDong h JOIN FETCH h.phong JOIN FETCH h.khachThue WHERE h.maTaiKhoan = :maTaiKhoan AND h.trangThai = 'HIEU_LUC'")
    Optional<HopDong> findHieuLucByMaTaiKhoan(@Param("maTaiKhoan") String maTaiKhoan);

    default Optional<HopDong> timHopDongHieuLucCua(String maPhong) {
        List<HopDong> ds = findByMaPhongAndTrangThaiIn(maPhong, List.of(TrangThaiHD.HIEU_LUC, TrangThaiHD.CHO_NHAN_PHONG));
        return ds.isEmpty() ? Optional.empty() : Optional.of(ds.get(0));
    }
}
