package vn.chungcumini.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.ChiSo;
import vn.chungcumini.backend.domain.LoaiCongTo;
import vn.chungcumini.backend.domain.LoaiGhi;

public interface ChiSoRepository extends JpaRepository<ChiSo, String> {

    List<ChiSo> findByHopDong_MaHopDongOrderByNgayGhiDesc(String maHopDong);

    /** UK đề xuất ở sheet 2_ThuocTinh: (maHopDong, loaiCongTo, kyThang, loaiGhi) — DAU_KY và HANG_THANG
     * có thể cùng kyThang (khách vào ở và được chốt chỉ số ngay trong tháng đầu), nên loaiGhi bắt buộc
     * phải nằm trong điều kiện lọc, không được bỏ qua. */
    Optional<ChiSo> findByHopDong_MaHopDongAndLoaiCongToAndKyThangAndLoaiGhi(
            String maHopDong, LoaiCongTo loaiCongTo, String kyThang, LoaiGhi loaiGhi);

    /** Lần ghi gần nhất (mọi loại ghi) của một công tơ — để tự lấy chiSoCu khi ghi chỉ số mới. */
    Optional<ChiSo> findFirstByHopDong_MaHopDongAndLoaiCongToOrderByNgayGhiDescMaChiSoDesc(String maHopDong, LoaiCongTo loaiCongTo);
}
