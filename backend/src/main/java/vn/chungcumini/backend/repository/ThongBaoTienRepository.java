package vn.chungcumini.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiTBT;

public interface ThongBaoTienRepository extends JpaRepository<ThongBaoTien, String> {

    @Query("SELECT t FROM ThongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong WHERE t.maThongBaoTien = :maThongBaoTien")
    Optional<ThongBaoTien> timChiTiet(@Param("maThongBaoTien") String maThongBaoTien);

    @Query("SELECT t FROM ThongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong WHERE t.kyThang = :kyThang ORDER BY h.phong.maPhong")
    List<ThongBaoTien> timTheoKyThang(@Param("kyThang") String kyThang);

    List<ThongBaoTien> findByHopDong_MaHopDongAndTrangThaiNot(String maHopDong, TrangThaiTBT trangThai);

    /** Thông báo chưa hủy gần nhất trước một kỳ, dùng để tính noCu (BR-TT-03). */
    @Query(
            "SELECT t FROM ThongBaoTien t WHERE t.hopDong.maHopDong = :maHopDong AND t.trangThai <> 'DA_HUY' "
                    + "AND t.kyThang < :kyThang ORDER BY t.kyThang DESC")
    List<ThongBaoTien> timTruocKy(@Param("maHopDong") String maHopDong, @Param("kyThang") String kyThang);

    long countByMaThongBaoTienStartingWith(String tienTo);

    /** BoHenGio.chayHangNgay / DichVuViecCanLam.layDanhSach: thông báo đang ở các trạng thái cho trước. */
    @Query("SELECT t FROM ThongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong WHERE t.trangThai IN :trangThai")
    List<ThongBaoTien> findByTrangThaiIn(@Param("trangThai") List<TrangThaiTBT> trangThai);

    /** DichVuBaoCao.doanhThu/tyLeThu: thông báo đã phát hành (không NHAP, không DA_HUY) trong khoảng ngày phát hành. */
    @Query(
            "SELECT t FROM ThongBaoTien t JOIN FETCH t.hopDong h JOIN FETCH h.phong WHERE t.trangThai NOT IN ('NHAP', 'DA_HUY') "
                    + "AND t.ngayPhatHanh BETWEEN :tuNgay AND :denNgay")
    List<ThongBaoTien> timDaPhatHanhTrongKhoang(@Param("tuNgay") java.time.LocalDate tuNgay, @Param("denNgay") java.time.LocalDate denNgay);
}
