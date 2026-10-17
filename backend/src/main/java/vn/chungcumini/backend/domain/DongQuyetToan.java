package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Từng khoản hoàn (+) / trừ (−) trong QuyetToan — dấu do loaiKhoan quyết định (BR-TL-02). */
@Entity
@Table(name = "\"DongQuyetToan\"")
@Getter
@Setter
public class DongQuyetToan {

    @Id
    @Column(name = "\"maDong\"", length = 20)
    private String maDong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maQuyetToan\"", nullable = false)
    private QuyetToan quyetToan;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiKhoan\"", columnDefinition = "\"LoaiKhoanQT\"", nullable = false)
    private LoaiKhoanQT loaiKhoan;

    @Column(name = "\"soTien\"", precision = 15, scale = 2, nullable = false)
    private BigDecimal soTien;

    /** Bắt buộc với mọi khoản trừ (BR-TL-02). */
    @Column(name = "\"ghiChu\"", length = 500)
    private String ghiChu;

    /** Bắt buộc với mọi khoản trừ (BR-TL-02); dòng điện/nước có thể tự lấy ảnh công tơ CUOI_KY. */
    @Column(name = "\"anhBangChung\"", length = 1000)
    private String anhBangChung;
}
