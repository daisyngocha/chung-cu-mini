package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "\"BaoThanhToan\"")
@Getter
@Setter
public class BaoThanhToan {

    @Id
    @Column(name = "\"maBaoTT\"", length = 20)
    private String maBaoTT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maThongBaoTien\"", nullable = false)
    private ThongBaoTien thongBaoTien;

    @Column(name = "\"thoiDiemBao\"", nullable = false)
    private LocalDateTime thoiDiemBao;

    @Column(name = "\"anhBienLai\"", length = 500)
    private String anhBienLai;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiBaoTT\"", nullable = false)
    private TrangThaiBaoTT trangThai = TrangThaiBaoTT.CHO_XAC_NHAN;

    @Column(name = "\"lyDoTuChoi\"", length = 500)
    private String lyDoTuChoi;

    @PrePersist
    void khiTao() {
        if (thoiDiemBao == null) thoiDiemBao = LocalDateTime.now();
    }
}
