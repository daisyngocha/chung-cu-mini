package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"BanGiaoTaiSan\"")
@Getter
@Setter
public class BanGiaoTaiSan {

    @Id
    @Column(name = "\"maBanGiao\"", length = 20)
    private String maBanGiao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maHopDong\"", nullable = false)
    private HopDong hopDong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maTaiSan\"", nullable = false)
    private TaiSan taiSan;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiBanGiao\"", columnDefinition = "\"LoaiBanGiao\"", nullable = false)
    private LoaiBanGiao loaiBanGiao;

    @Column(name = "\"ngayBanGiao\"", nullable = false)
    private LocalDate ngayBanGiao;

    @Column(name = "\"tinhTrang\"", length = 255, nullable = false)
    private String tinhTrang;

    /** Bắt buộc khi TRA_PHONG có hư hỏng (BR-TL-02). */
    @Column(name = "\"anh\"", length = 1000)
    private String anh;

    /** Chỉ dùng khi loaiBanGiao = TRA_PHONG. */
    @Column(name = "\"chiPhiHuHong\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal chiPhiHuHong = BigDecimal.ZERO;

    @PrePersist
    void khiTao() {
        if (ngayBanGiao == null) ngayBanGiao = LocalDate.now();
    }
}
