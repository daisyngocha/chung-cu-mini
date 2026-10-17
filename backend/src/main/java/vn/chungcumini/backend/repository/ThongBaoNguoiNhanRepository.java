package vn.chungcumini.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.ThongBaoNguoiNhan;
import vn.chungcumini.backend.domain.ThongBaoNguoiNhanId;

public interface ThongBaoNguoiNhanRepository extends JpaRepository<ThongBaoNguoiNhan, ThongBaoNguoiNhanId> {

    /** TB.XEM: mỗi người chỉ thấy thông báo gửi cho mình, mới nhất trước. */
    @Query(
            "SELECT n FROM ThongBaoNguoiNhan n JOIN FETCH n.thongBao t WHERE n.taiKhoan = :maTaiKhoan "
                    + "ORDER BY t.ngayTao DESC")
    List<ThongBaoNguoiNhan> timTheoTaiKhoan(@Param("maTaiKhoan") String maTaiKhoan);

    Optional<ThongBaoNguoiNhan> findByThongBao_MaThongBaoAndTaiKhoan(String maThongBao, String taiKhoan);

    /** ThongBaoNguoiNhan.demChuaDoc. */
    long countByTaiKhoanAndDaDocFalse(String taiKhoan);
}
