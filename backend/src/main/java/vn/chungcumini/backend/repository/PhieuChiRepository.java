package vn.chungcumini.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.PhieuChi;

public interface PhieuChiRepository extends JpaRepository<PhieuChi, String> {

    @Query(
            "SELECT p FROM PhieuChi p JOIN FETCH p.quyetToan q JOIN FETCH q.hopDong h JOIN FETCH h.phong ph "
                    + "JOIN FETCH ph.toaNha JOIN FETCH h.khachThue WHERE p.soPhieuChi = :soPhieuChi")
    Optional<PhieuChi> timChiTiet(@Param("soPhieuChi") String soPhieuChi);

    long countBySoPhieuChiStartingWith(String tienTo);
}
