package vn.chungcumini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.NhatKyThaoTac;

public interface NhatKyThaoTacRepository extends JpaRepository<NhatKyThaoTac, String> {

    /** CN-09.05: tra cứu nhật ký, mới nhất trước. */
    List<NhatKyThaoTac> findAllByOrderByThoiDiemDesc();

    List<NhatKyThaoTac> findByDoiTuongOrderByThoiDiemDesc(String doiTuong);
}
