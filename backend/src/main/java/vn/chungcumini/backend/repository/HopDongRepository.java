package vn.chungcumini.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.TrangThaiHD;

public interface HopDongRepository extends JpaRepository<HopDong, String> {
    boolean existsByMaPhong(String maPhong);

    List<HopDong> findByMaPhongAndTrangThaiIn(String maPhong, List<TrangThaiHD> trangThai);

    default Optional<HopDong> timHopDongHieuLucCua(String maPhong) {
        List<HopDong> ds = findByMaPhongAndTrangThaiIn(maPhong, List.of(TrangThaiHD.HIEU_LUC, TrangThaiHD.CHO_NHAN_PHONG));
        return ds.isEmpty() ? Optional.empty() : Optional.of(ds.get(0));
    }
}
