package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Phiếu chi hoàn cọc — một quyết toán tối đa một phiếu chi. */
@Entity
@Table(name = "\"PhieuChi\"")
@Getter
@Setter
public class PhieuChi {

    @Id
    @Column(name = "\"soPhieuChi\"", length = 20)
    private String soPhieuChi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maQuyetToan\"", nullable = false)
    private QuyetToan quyetToan;

    @Column(name = "\"ngayChi\"", nullable = false)
    private LocalDate ngayChi;

    @Column(name = "\"nguoiNhan\"", length = 100, nullable = false)
    private String nguoiNhan;

    @Column(name = "\"soTien\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"hinhThuc\"", columnDefinition = "\"HinhThucTT\"", nullable = false)
    private HinhThucTT hinhThuc;

    @PrePersist
    void khiTao() {
        if (ngayChi == null) ngayChi = LocalDate.now();
    }
}
