package vn.chungcumini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.LichSuHopDong;

public interface LichSuHopDongRepository extends JpaRepository<LichSuHopDong, String> {
    List<LichSuHopDong> findByHopDong_MaHopDong(String maHopDong);
}
