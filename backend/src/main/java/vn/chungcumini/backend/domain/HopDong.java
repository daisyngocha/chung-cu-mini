package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"HopDong\"")
@Getter
@Setter
public class HopDong {

    @Id
    @Column(name = "\"maHopDong\"", length = 20)
    private String maHopDong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maPhong\"", nullable = false)
    private Phong phong;

    /** Người đứng tên hợp đồng. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maKhach\"", nullable = false)
    private KhachThue khachThue;

    /** NULL tới khi cấp tài khoản (P01 bước 5). */
    @Column(name = "\"maTaiKhoan\"", length = 20)
    private String maTaiKhoan;

    /** NULL = tài khoản cấp cho người đứng tên; có giá trị = cấp cho một NguoiO. */
    @Column(name = "\"maNguoiOCapTK\"", length = 20)
    private String maNguoiOCapTK;

    @Column(name = "\"ngayBatDau\"", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "\"ngayHetHan\"", nullable = false)
    private LocalDate ngayHetHan;

    @Column(name = "\"giaThue\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal giaThue;

    @Column(name = "\"tienCoc\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal tienCoc;

    @Column(name = "\"mucPhatTraTruoc\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal mucPhatTraTruoc = BigDecimal.ZERO;

    @Column(name = "\"anhHopDong\"", length = 1000)
    private String anhHopDong;

    @Column(name = "\"ngayNhanPhong\"")
    private LocalDate ngayNhanPhong;

    @Column(name = "\"ngayBaoTra\"")
    private LocalDate ngayBaoTra;

    @Column(name = "\"ngayTraDuKien\"")
    private LocalDate ngayTraDuKien;

    @Column(name = "\"ngayTraThucTe\"")
    private LocalDate ngayTraThucTe;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiHD\"", nullable = false)
    private TrangThaiHD trangThai = TrangThaiHD.CHO_NHAN_PHONG;

    @Column(name = "\"lyDoNhanPhongChuaThuDu\"", length = 500)
    private String lyDoNhanPhongChuaThuDu;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiKetThuc\"", columnDefinition = "\"LoaiKetThuc\"")
    private LoaiKetThuc loaiKetThuc;

    @Column(name = "\"daThuHoiChiaKhoa\"", nullable = false)
    private boolean daThuHoiChiaKhoa = false;
}
