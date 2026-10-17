package vn.chungcumini.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.QuyetToan;

public interface QuyetToanRepository extends JpaRepository<QuyetToan, String> {

    @Query(
            "SELECT q FROM QuyetToan q JOIN FETCH q.hopDong h JOIN FETCH h.phong JOIN FETCH h.khachThue "
                    + "WHERE q.maQuyetToan = :maQuyetToan")
    Optional<QuyetToan> timChiTiet(@Param("maQuyetToan") String maQuyetToan);

    @Query(
            "SELECT q FROM QuyetToan q JOIN FETCH q.hopDong h JOIN FETCH h.phong JOIN FETCH h.khachThue "
                    + "WHERE h.maHopDong = :maHopDong")
    Optional<QuyetToan> timTheoHopDong(@Param("maHopDong") String maHopDong);

    /** Khách xem quyết toán của phòng mình (Mục 11) — chưa thanh lý nên HopDong.maTaiKhoan vẫn còn gắn. */
    @Query(
            "SELECT q FROM QuyetToan q JOIN FETCH q.hopDong h JOIN FETCH h.phong JOIN FETCH h.khachThue "
                    + "WHERE h.maTaiKhoan = :maTaiKhoan")
    Optional<QuyetToan> timTheoTaiKhoanKhach(@Param("maTaiKhoan") String maTaiKhoan);
}
