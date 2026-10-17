package vn.chungcumini.backend.repository;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.LoaiSuKien;
import vn.chungcumini.backend.domain.ThongBao;

public interface ThongBaoRepository extends JpaRepository<ThongBao, String> {

    /** BoHenGio.chayHangNgay: idempotent — không tạo trùng thông báo cho cùng sự kiện, cùng đối tượng, cùng ngày. */
    boolean existsByLoaiSuKienAndDoiTuongLienQuanAndNgayTaoBetween(
            LoaiSuKien loaiSuKien, String doiTuongLienQuan, LocalDateTime tuThoiDiem, LocalDateTime denThoiDiem);
}
