package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Sự cố khách báo và quá trình xử lý (BR-SC-01, BR-SC-02, BR-SC-03). */
@Entity
@Table(name = "\"SuCo\"")
@Getter
@Setter
public class SuCo {

    @Id
    @Column(name = "\"maSuCo\"", length = 20)
    private String maSuCo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maPhong\"", nullable = false)
    private Phong phong;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiSuCo\"", columnDefinition = "\"LoaiSuCo\"", nullable = false)
    private LoaiSuCo loaiSuCo;

    @Column(name = "\"moTa\"", length = 1000, nullable = false)
    private String moTa;

    @Column(name = "\"anh\"", length = 1000)
    private String anh;

    /** Khách chỉ thấy sự cố có ngayBao ≥ ngày nhận phòng của HĐ mình. */
    @Column(name = "\"ngayBao\"", nullable = false)
    private LocalDateTime ngayBao;

    /** Bắt buộc khi chuyển DANG_XU_LY. */
    @Column(name = "\"lichHen\"")
    private LocalDateTime lichHen;

    /** Bắt buộc khi DA_XU_LY. */
    @Column(name = "\"ketQua\"", length = 1000)
    private String ketQua;

    @Column(name = "\"chiPhi\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal chiPhi = BigDecimal.ZERO;

    /** true → được cộng phí vào thông báo tiền (BR-SC-03). */
    @Column(name = "\"doKhachGay\"", nullable = false)
    private boolean doKhachGay = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiSuCo\"", nullable = false)
    private TrangThaiSuCo trangThai = TrangThaiSuCo.MOI;

    /** Bắt buộc khi TU_CHOI (BR-SC-02). */
    @Column(name = "\"lyDoTuChoi\"", length = 500)
    private String lyDoTuChoi;

    @PrePersist
    void khiTao() {
        if (ngayBao == null) ngayBao = LocalDateTime.now();
    }
}
