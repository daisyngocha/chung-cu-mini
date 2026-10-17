package vn.chungcumini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.NguoiO;

public interface NguoiORepository extends JpaRepository<NguoiO, String> {
    List<NguoiO> findByHopDong_MaHopDong(String maHopDong);

    int countByHopDong_MaHopDongAndNgayRaIsNull(String maHopDong);

    List<NguoiO> findByHopDong_MaHopDongAndNgayRaIsNull(String maHopDong);

    List<NguoiO> findByNgayVaoBetween(java.time.LocalDate tuNgay, java.time.LocalDate denNgay);
}
