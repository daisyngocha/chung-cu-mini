package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"DongThongBaoTien\"")
@Getter
@Setter
public class DongThongBaoTien {

    @Id
    @Column(name = "\"maDong\"", length = 20)
    private String maDong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maThongBaoTien\"", nullable = false)
    private ThongBaoTien thongBaoTien;

    /** Bắt buộc với dòng DIEN và NUOC theo công tơ. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maChiSo\"")
    private ChiSo chiSo;

    /** Có khi phí phát sinh do khách gây sự cố (BR-SC-03) — SuCo chưa cài (GĐ6), giữ plain String. */
    @Column(name = "\"maSuCo\"", length = 20)
    private String maSuCo;

    /** Bắt buộc với dòng DIEU_CHINH (BR-HD-04). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maThongBaoTienGoc\"")
    private ThongBaoTien thongBaoTienGoc;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiKhoan\"", columnDefinition = "\"LoaiKhoanTBT\"", nullable = false)
    private LoaiKhoanTBT loaiKhoan;

    @Column(name = "\"dienGiai\"", length = 255, nullable = false)
    private String dienGiai;

    @Column(name = "\"tuNgay\"")
    private LocalDate tuNgay;

    @Column(name = "\"denNgay\"")
    private LocalDate denNgay;

    @Column(name = "\"soLuong\"", precision = 10, scale = 2, nullable = false)
    private BigDecimal soLuong = BigDecimal.ONE;

    @Column(name = "\"donGia\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal donGia;

    @Column(name = "\"tyLeNgayO\"", precision = 7, scale = 6, nullable = false)
    private BigDecimal tyLeNgayO = BigDecimal.ONE;

    /** = soLuong × donGia × tyLeNgayO; GIAM_TRU mang dấu âm; KHÔNG làm tròn (BR-HD-05). */
    @Column(name = "\"thanhTien\"", precision = 15, scale = 2, nullable = false)
    private BigDecimal thanhTien;
}
