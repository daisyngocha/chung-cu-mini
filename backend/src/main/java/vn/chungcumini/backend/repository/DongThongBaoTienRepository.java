package vn.chungcumini.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.DongThongBaoTien;

public interface DongThongBaoTienRepository extends JpaRepository<DongThongBaoTien, String> {

    @Query("SELECT d FROM DongThongBaoTien d JOIN FETCH d.thongBaoTien WHERE d.thongBaoTien.maThongBaoTien = :maThongBaoTien")
    List<DongThongBaoTien> findByThongBaoTien_MaThongBaoTien(@Param("maThongBaoTien") String maThongBaoTien);

    boolean existsByThongBaoTien_MaThongBaoTienAndLoaiKhoan(String maThongBaoTien, vn.chungcumini.backend.domain.LoaiKhoanTBT loaiKhoan);
}
