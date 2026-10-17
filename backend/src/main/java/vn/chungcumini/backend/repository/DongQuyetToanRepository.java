package vn.chungcumini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.DongQuyetToan;

public interface DongQuyetToanRepository extends JpaRepository<DongQuyetToan, String> {

    List<DongQuyetToan> findByQuyetToan_MaQuyetToan(String maQuyetToan);

    void deleteByQuyetToan_MaQuyetToan(String maQuyetToan);
}
