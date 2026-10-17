package vn.chungcumini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.LichSuSuCo;

public interface LichSuSuCoRepository extends JpaRepository<LichSuSuCo, String> {

    List<LichSuSuCo> findBySuCo_MaSuCoOrderByThoiDiem(String maSuCo);
}
