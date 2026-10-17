package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Thông báo trong hệ thống (chuông) — tự động theo sự kiện hoặc chủ nhà gửi. Không gửi SMS/Zalo. */
@Entity
@Table(name = "\"ThongBao\"")
@Getter
@Setter
public class ThongBao {

    @Id
    @Column(name = "\"maThongBao\"", length = 20)
    private String maThongBao;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiSuKien\"", columnDefinition = "\"LoaiSuKien\"", nullable = false)
    private LoaiSuKien loaiSuKien;

    @Column(name = "\"tieuDe\"", length = 150, nullable = false)
    private String tieuDe;

    @Column(name = "\"noiDung\"", columnDefinition = "text", nullable = false)
    private String noiDung;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"nguon\"", columnDefinition = "\"NguonTB\"", nullable = false)
    private NguonTB nguon = NguonTB.TU_DONG;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"phamVi\"", columnDefinition = "\"PhamViTB\"", nullable = false)
    private PhamViTB phamVi = PhamViTB.CA_NHAN;

    /** "TenLop:ma", vd "ThongBaoTien:TB2610P101" — để mở thẳng màn hình chi tiết khi bấm thông báo. */
    @Column(name = "\"doiTuongLienQuan\"", length = 50)
    private String doiTuongLienQuan;

    @Column(name = "\"ngayTao\"", nullable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    void khiTao() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
