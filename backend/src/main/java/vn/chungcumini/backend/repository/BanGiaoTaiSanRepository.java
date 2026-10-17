package vn.chungcumini.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.chungcumini.backend.domain.BanGiaoTaiSan;
import vn.chungcumini.backend.domain.LoaiBanGiao;

public interface BanGiaoTaiSanRepository extends JpaRepository<BanGiaoTaiSan, String> {

    /** JOIN FETCH taiSan — BanGiaoTaiSanResponse đọc taiSan.tenTaiSan (không phải ID), cần nạp sẵn. */
    @Query(
            "SELECT b FROM BanGiaoTaiSan b JOIN FETCH b.taiSan WHERE b.hopDong.maHopDong = :maHopDong AND b.loaiBanGiao = :loaiBanGiao")
    List<BanGiaoTaiSan> findByHopDong_MaHopDongAndLoaiBanGiao(
            @Param("maHopDong") String maHopDong, @Param("loaiBanGiao") LoaiBanGiao loaiBanGiao);

    Optional<BanGiaoTaiSan> findByHopDong_MaHopDongAndTaiSan_MaTaiSanAndLoaiBanGiao(
            String maHopDong, String maTaiSan, LoaiBanGiao loaiBanGiao);
}
