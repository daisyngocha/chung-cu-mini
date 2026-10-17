package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"TaiKhoan\"")
@Getter
@Setter
public class TaiKhoan {

    @Id
    @Column(name = "\"maTaiKhoan\"", length = 20)
    private String maTaiKhoan;

    @Column(name = "\"tenDangNhap\"", length = 10, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "\"matKhauHash\"", length = 255, nullable = false)
    private String matKhauHash;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"vaiTro\"", columnDefinition = "\"VaiTro\"", nullable = false)
    private VaiTro vaiTro;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiTK\"", nullable = false)
    private TrangThaiTK trangThai = TrangThaiTK.HOAT_DONG;

    @Column(name = "\"soLanSai\"", nullable = false)
    private int soLanSai = 0;

    @Column(name = "\"phaiDoiMatKhau\"", nullable = false)
    private boolean phaiDoiMatKhau = true;

    @Column(name = "\"ngayTao\"", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "\"khoaDenLuc\"")
    private LocalDateTime khoaDenLuc;

    @PrePersist
    void khiTao() {
        if (ngayTao == null) ngayTao = LocalDateTime.now();
    }
}
