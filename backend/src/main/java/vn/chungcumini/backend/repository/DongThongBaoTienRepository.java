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

    /** QuyetToan.tinhQuyetToan: dòng TIEN_PHONG đã thu mà còn trùm tới/ qua ngày trả → cơ sở tính HOAN_TIEN_PHONG. */
    @Query(
            "SELECT d FROM DongThongBaoTien d JOIN FETCH d.thongBaoTien t WHERE t.hopDong.maHopDong = :maHopDong "
                    + "AND d.loaiKhoan = 'TIEN_PHONG' AND t.trangThai <> 'DA_HUY' AND d.denNgay >= :ngayTra")
    List<DongThongBaoTien> timDongTienPhongTuNgayTra(
            @Param("maHopDong") String maHopDong, @Param("ngayTra") java.time.LocalDate ngayTra);
}
