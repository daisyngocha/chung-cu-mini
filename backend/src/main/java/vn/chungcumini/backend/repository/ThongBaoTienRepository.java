package vn.chungcumini.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.LoaiThongBao;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiTBT;

public interface ThongBaoTienRepository extends JpaRepository<ThongBaoTien, String> {

    @Query("SELECT t FROM ThongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong JOIN FETCH h.khachThue WHERE t.maThongBaoTien = :maThongBaoTien")
    Optional<ThongBaoTien> timChiTiet(@Param("maThongBaoTien") String maThongBaoTien);

    @Query("SELECT t FROM ThongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong WHERE t.kyThang = :kyThang ORDER BY h.phong.maPhong")
    List<ThongBaoTien> timTheoKyThang(@Param("kyThang") String kyThang);

    @Query(
            "SELECT t FROM ThongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong WHERE h.maTaiKhoan = :maTaiKhoan "
                    + "ORDER BY t.kyThang DESC")
    List<ThongBaoTien> timTheoTaiKhoanKhach(@Param("maTaiKhoan") String maTaiKhoan);

    List<ThongBaoTien> findByHopDong_MaHopDongAndTrangThaiNot(String maHopDong, TrangThaiTBT trangThai);

    /** Thông báo chưa hủy gần nhất trước một kỳ, dùng để tính noCu (BR-TT-03). */
    @Query(
            "SELECT t FROM ThongBaoTien t WHERE t.hopDong.maHopDong = :maHopDong AND t.trangThai <> 'DA_HUY' "
                    + "AND t.kyThang < :kyThang ORDER BY t.kyThang DESC")
    List<ThongBaoTien> timTruocKy(@Param("maHopDong") String maHopDong, @Param("kyThang") String kyThang);

    long countByMaThongBaoTienStartingWith(String tienTo);

    /** HopDong.xacNhanNhanPhong (BR-CT-05): mỗi HĐ có đúng 1 thông báo KY_HOP_DONG (lapThongBaoKyHopDong). */
    Optional<ThongBaoTien> findByHopDong_MaHopDongAndLoaiThongBao(String maHopDong, LoaiThongBao loaiThongBao);
}
