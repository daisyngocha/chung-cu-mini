package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"Phong\"")
@Getter
@Setter
public class Phong {

    @Id
    @Column(name = "\"maPhong\"", length = 10)
    private String maPhong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maToa\"", nullable = false)
    private ToaNha toaNha;

    @Column(nullable = false)
    private Integer tang;

    @Column(name = "\"dienTich\"", precision = 6, scale = 2, nullable = false)
    private BigDecimal dienTich;

    @Column(name = "\"soNguoiToiDa\"", nullable = false)
    private Integer soNguoiToiDa;

    @Column(name = "\"giaNiemYet\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal giaNiemYet;

    @Column(name = "\"coCongToNuoc\"", nullable = false)
    private boolean coCongToNuoc = true;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiPhong\"", nullable = false)
    private TrangThaiPhong trangThai = TrangThaiPhong.TRONG;
}
