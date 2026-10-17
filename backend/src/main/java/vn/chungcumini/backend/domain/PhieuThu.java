package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Chứng từ thu nội bộ — không phải hóa đơn điện tử (BR-HD-06). Đánh số liên tục, không xóa (BR-TT-02). */
@Entity
@Table(name = "\"PhieuThu\"")
@Getter
@Setter
public class PhieuThu {

    @Id
    @Column(name = "\"soPhieuThu\"", length = 20)
    private String soPhieuThu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maThongBaoTien\"", nullable = false)
    private ThongBaoTien thongBaoTien;

    /** NULL khi thu tiền mặt hoặc ghi nhận thu trực tiếp. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maBaoTT\"")
    private BaoThanhToan baoThanhToan;

    @Column(name = "\"ngayThu\"", nullable = false)
    private LocalDate ngayThu;

    @Column(name = "\"nguoiNop\"", length = 100, nullable = false)
    private String nguoiNop;

    @Column(name = "\"soTien\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal soTien;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"hinhThuc\"", columnDefinition = "\"HinhThucTT\"", nullable = false)
    private HinhThucTT hinhThuc;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiPT\"", nullable = false)
    private TrangThaiPT trangThai = TrangThaiPT.HIEU_LUC;

    @Column(name = "\"lyDoHuy\"", length = 500)
    private String lyDoHuy;

    @PrePersist
    void khiTao() {
        if (ngayThu == null) ngayThu = LocalDate.now();
    }
}
