package vn.chungcumini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.TaiSan;

public interface TaiSanRepository extends JpaRepository<TaiSan, String> {
    List<TaiSan> findByPhong_MaPhong(String maPhong);

    void deleteByPhong_MaPhong(String maPhong);
}
