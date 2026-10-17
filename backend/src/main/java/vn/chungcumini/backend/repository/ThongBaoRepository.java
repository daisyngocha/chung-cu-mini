package vn.chungcumini.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.chungcumini.backend.domain.ThongBao;

public interface ThongBaoRepository extends JpaRepository<ThongBao, String> {
}
