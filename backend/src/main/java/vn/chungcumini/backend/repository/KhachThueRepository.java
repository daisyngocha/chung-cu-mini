package vn.chungcumini.backend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.KhachThue;

public interface KhachThueRepository extends JpaRepository<KhachThue, String> {
    Optional<KhachThue> findBySoDienThoai(String soDienThoai);
}
