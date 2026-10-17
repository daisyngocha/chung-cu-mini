package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Quyết toán cọc khi trả phòng (BR-TL-01, BR-TL-03, BR-TT-04). Mỗi HĐ tối đa 1 quyết toán. */
@Entity
@Table(name = "\"QuyetToan\"")
@Getter
@Setter
public class QuyetToan {

    @Id
    @Column(name = "\"maQuyetToan\"", length = 20)
    private String maQuyetToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maHopDong\"", nullable = false)
    private HopDong hopDong;

    /** Ngày trả không tính tiền. */
    @Column(name = "\"ngayTra\"", nullable = false)
    private LocalDate ngayTra;

    @Column(name = "\"tienCoc\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal tienCoc;

    /** Tiền phòng đã thu cho các ngày từ ngayTra trở đi. */
    @Column(name = "\"tienPhongHoanLai\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal tienPhongHoanLai = BigDecimal.ZERO;

    @Column(name = "\"tongKhauTru\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal tongKhauTru = BigDecimal.ZERO;

    /** = tienCoc + tienPhongHoanLai − tongKhauTru. >0 → phiếu chi; <0 → thông báo thu thêm. */
    @Column(name = "\"soTienQuyetToan\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal soTienQuyetToan;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiQT\"", nullable = false)
    private TrangThaiQT trangThai = TrangThaiQT.NHAP;

    @Column(name = "\"lyDoDieuChinh\"", length = 500)
    private String lyDoDieuChinh;

    /** NULL = chưa phản hồi. */
    @Column(name = "\"khachDongY\"")
    private Boolean khachDongY;

    @Column(name = "\"yKienKhach\"", length = 500)
    private String yKienKhach;

    @Column(name = "\"thoiDiemPhanHoi\"")
    private LocalDateTime thoiDiemPhanHoi;
}
