package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"BangGia\"")
@Getter
@Setter
public class BangGia {

    @Id
    @Column(name = "\"maKhoan\"", length = 20)
    private String maKhoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maToa\"", nullable = false)
    private ToaNha toaNha;

    @Column(name = "\"tenKhoan\"", length = 100, nullable = false)
    private String tenKhoan;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiKhoan\"", columnDefinition = "\"LoaiKhoan\"", nullable = false)
    private LoaiKhoan loaiKhoan;

    @Column(name = "\"donGia\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal donGia;

    @Column(name = "\"donViTinh\"", length = 20, nullable = false)
    private String donViTinh;

    @Column(name = "\"ngayApDung\"", nullable = false)
    private LocalDate ngayApDung;

    @Column(name = "\"ngayHetHieuLuc\"")
    private LocalDate ngayHetHieuLuc;
}
