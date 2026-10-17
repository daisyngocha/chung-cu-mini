package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"NhatKyThaoTac\"")
@Getter
@Setter
public class NhatKyThaoTac {

    @Id
    @Column(name = "\"maNhatKy\"", length = 20)
    private String maNhatKy;

    /** NULL = Bộ hẹn giờ hệ thống thực hiện. */
    @Column(name = "\"maTaiKhoan\"", length = 20)
    private String maTaiKhoan;

    @Column(name = "\"thoiDiem\"", nullable = false)
    private LocalDateTime thoiDiem;

    @Column(name = "\"doiTuong\"", length = 30, nullable = false)
    private String doiTuong;

    @Column(name = "\"maDoiTuong\"", length = 20, nullable = false)
    private String maDoiTuong;

    @Column(name = "\"hanhDong\"", length = 30, nullable = false)
    private String hanhDong;

    @Column(name = "\"noiDung\"", columnDefinition = "text")
    private String noiDung;

    @PrePersist
    void khiTao() {
        if (thoiDiem == null) thoiDiem = LocalDateTime.now();
    }
}
