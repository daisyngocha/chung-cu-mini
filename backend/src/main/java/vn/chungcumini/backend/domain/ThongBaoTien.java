package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"ThongBaoTien\"")
@Getter
@Setter
public class ThongBaoTien {

    @Id
    @Column(name = "\"maThongBaoTien\"", length = 20)
    private String maThongBaoTien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maHopDong\"", nullable = false)
    private HopDong hopDong;

    /** Chỉ có khi loaiThongBao = THU_THEM_QUYET_TOAN — QuyetToan chưa cài (GĐ5), giữ plain String. */
    @Column(name = "\"maQuyetToan\"", length = 20)
    private String maQuyetToan;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiThongBao\"", columnDefinition = "\"LoaiThongBao\"", nullable = false)
    private LoaiThongBao loaiThongBao = LoaiThongBao.HANG_THANG;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "\"kyThang\"", length = 7, nullable = false)
    private String kyThang;

    @Column(name = "\"ngayPhatHanh\"")
    private LocalDate ngayPhatHanh;

    @Column(name = "\"hanThanhToan\"", nullable = false)
    private LocalDate hanThanhToan;

    @Column(name = "\"noCu\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal noCu = BigDecimal.ZERO;

    @Column(name = "\"tongTien\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal tongTien;

    /** = Σ PhieuThu.soTien có trangThai HIEU_LUC — cập nhật bởi ThongBaoTienService.capNhatTrangThaiThu. */
    @Column(name = "\"soDaThu\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal soDaThu = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiTBT\"", nullable = false)
    private TrangThaiTBT trangThai = TrangThaiTBT.NHAP;

    @Column(name = "\"lyDoHuy\"", length = 500)
    private String lyDoHuy;
}
