package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Chỉ số gắn HĐ (không gắn phòng) để tách người thuê cũ/mới. */
@Entity
@Table(name = "\"ChiSo\"")
@Getter
@Setter
public class ChiSo {

    @Id
    @Column(name = "\"maChiSo\"", length = 20)
    private String maChiSo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maHopDong\"", nullable = false)
    private HopDong hopDong;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiCongTo\"", columnDefinition = "\"LoaiCongTo\"", nullable = false)
    private LoaiCongTo loaiCongTo;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "\"kyThang\"", length = 7, nullable = false)
    private String kyThang;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiGhi\"", columnDefinition = "\"LoaiGhi\"", nullable = false)
    private LoaiGhi loaiGhi = LoaiGhi.HANG_THANG;

    @Column(name = "\"ngayGhi\"", nullable = false)
    private LocalDate ngayGhi;

    @Column(name = "\"chiSoCu\"", nullable = false)
    private Integer chiSoCu;

    @Column(name = "\"chiSoMoi\"", nullable = false)
    private Integer chiSoMoi;

    @Column(name = "\"anhCongTo\"", length = 500)
    private String anhCongTo;

    @PrePersist
    void khiTao() {
        if (ngayGhi == null) ngayGhi = LocalDate.now();
    }
}
