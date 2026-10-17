package vn.chungcumini.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.SuCo;
import vn.chungcumini.backend.domain.TrangThaiSuCo;

public interface SuCoRepository extends JpaRepository<SuCo, String> {

    @Query("SELECT s FROM SuCo s JOIN FETCH s.phong WHERE s.maSuCo = :maSuCo")
    Optional<SuCo> timChiTiet(@Param("maSuCo") String maSuCo);

    @Query("SELECT s FROM SuCo s JOIN FETCH s.phong ORDER BY s.ngayBao DESC")
    List<SuCo> findAllChiTiet();

    /** BR-SC-01: khách chỉ xem sự cố của phòng mình, từ ngày nhận phòng của HĐ hiện tại trở đi. */
    @Query(
            "SELECT s FROM SuCo s JOIN FETCH s.phong WHERE s.phong.maPhong = :maPhong AND s.ngayBao >= :tuNgay "
                    + "ORDER BY s.ngayBao DESC")
    List<SuCo> timTheoPhongTuNgay(@Param("maPhong") String maPhong, @Param("tuNgay") LocalDateTime tuNgay);

    /** DichVuBaoCao.suCoDangMo / DichVuViecCanLam.layDanhSach: sự cố MOI, DANG_XU_LY, MO_LAI. */
    @Query("SELECT s FROM SuCo s JOIN FETCH s.phong WHERE s.trangThai IN :trangThai ORDER BY s.ngayBao")
    List<SuCo> findByTrangThaiIn(@Param("trangThai") List<TrangThaiSuCo> trangThai);
}
