package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Lịch sử đổi trạng thái sự cố — chỉ thêm, không sửa/xóa. Cả chủ nhà và khách cùng xem. */
@Entity
@Table(name = "\"LichSuSuCo\"")
@Getter
@Setter
public class LichSuSuCo {

    @Id
    @Column(name = "\"maLichSu\"", length = 20)
    private String maLichSu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maSuCo\"", nullable = false)
    private SuCo suCo;

    @Column(name = "\"thoiDiem\"", nullable = false)
    private LocalDateTime thoiDiem;

    /** NULL ở lần tạo sự cố. */
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThaiCu\"", columnDefinition = "\"TrangThaiSuCo\"")
    private TrangThaiSuCo trangThaiCu;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThaiMoi\"", columnDefinition = "\"TrangThaiSuCo\"", nullable = false)
    private TrangThaiSuCo trangThaiMoi;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"vaiTroThucHien\"", columnDefinition = "\"VaiTro\"", nullable = false)
    private VaiTro vaiTroThucHien;

    /** Lịch hẹn, kết quả, lý do… hiển thị cho cả 2 bên. */
    @Column(name = "\"ghiChu\"", length = 1000)
    private String ghiChu;

    @PrePersist
    void khiTao() {
        if (thoiDiem == null) thoiDiem = LocalDateTime.now();
    }
}
