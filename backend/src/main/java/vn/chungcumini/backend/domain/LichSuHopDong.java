package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Chỉ thêm, không sửa/xóa — lịch sử thay đổi của một hợp đồng (gia hạn, đổi giá/phí, thêm/bớt người ở). */
@Entity
@Table(name = "\"LichSuHopDong\"")
@Getter
@Setter
public class LichSuHopDong {

    @Id
    @Column(name = "\"maLichSu\"", length = 20)
    private String maLichSu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maHopDong\"", nullable = false)
    private HopDong hopDong;

    @Column(name = "\"ngayThayDoi\"", nullable = false)
    private LocalDate ngayThayDoi;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"loaiThayDoi\"", columnDefinition = "\"LoaiThayDoi\"", nullable = false)
    private LoaiThayDoi loaiThayDoi;

    @Column(name = "\"truongThayDoi\"", length = 50, nullable = false)
    private String truongThayDoi;

    /** NULL khi thêm mới (vd thêm người ở). */
    @Column(name = "\"giaTriCu\"", length = 500)
    private String giaTriCu;

    @Column(name = "\"giaTriMoi\"", length = 500)
    private String giaTriMoi;

    /** Điều chỉnh giá/phí: ngày 1 của tháng; gia hạn: ngayHetHan cũ + 1. tinhTienPhong() dùng mốc này. */
    @Column(name = "\"ngayApDung\"", nullable = false)
    private LocalDate ngayApDung;

    /** Bắt buộc với GIA_HAN, DIEU_CHINH_GIA, DIEU_CHINH_PHI. */
    @Column(name = "\"anhPhuLuc\"", length = 1000)
    private String anhPhuLuc;

    @PrePersist
    void khiTao() {
        if (ngayThayDoi == null) ngayThayDoi = LocalDate.now();
    }
}
