package vn.chungcumini.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.Phong;

public interface PhongRepository extends JpaRepository<Phong, String> {
}
