package vn.chungcumini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.HopDongDichVu;
import vn.chungcumini.backend.domain.HopDongDichVuId;

public interface HopDongDichVuRepository extends JpaRepository<HopDongDichVu, HopDongDichVuId> {
    List<HopDongDichVu> findByHopDong_MaHopDong(String maHopDong);
}
