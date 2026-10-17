package vn.chungcumini.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.TaiKhoan;
import vn.chungcumini.backend.domain.VaiTro;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

    /** Hệ thống chỉ có 1 tài khoản chủ nhà — dùng để xác định người nhận ThongBao gửi cho chủ nhà. */
    Optional<TaiKhoan> findFirstByVaiTro(VaiTro vaiTro);
}
