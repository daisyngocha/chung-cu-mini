package vn.chungcumini.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.BangGia;
import vn.chungcumini.backend.domain.LoaiKhoan;

public interface BangGiaRepository extends JpaRepository<BangGia, String> {

    List<BangGia> findAllByOrderByTenKhoanAscNgayApDungDesc();

    boolean existsByTenKhoanAndNgayHetHieuLucIsNull(String tenKhoan);

    /** Bản ghi hiệu lực tại {@code ngay} — chỉ dùng cho DIEN/NUOC/NUOC_THEO_NGUOI (mỗi loại có đúng 1 bản ghi hiệu lực tại một thời điểm). */
    @Query(
            "SELECT b FROM BangGia b WHERE b.loaiKhoan = :loaiKhoan AND b.ngayApDung <= :ngay "
                    + "AND (b.ngayHetHieuLuc IS NULL OR b.ngayHetHieuLuc >= :ngay) ORDER BY b.ngayApDung DESC")
    List<BangGia> timHieuLucTai(@Param("loaiKhoan") LoaiKhoan loaiKhoan, @Param("ngay") LocalDate ngay);

    default Optional<BangGia> timMotHieuLucTai(LoaiKhoan loaiKhoan, LocalDate ngay) {
        List<BangGia> ds = timHieuLucTai(loaiKhoan, ngay);
        return ds.isEmpty() ? Optional.empty() : Optional.of(ds.get(0));
    }
}
