package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** Liên kết thông báo – tài khoản, trạng thái đã đọc. Mỗi người chỉ thấy thông báo gửi cho mình. */
@Entity
@Table(name = "\"ThongBaoNguoiNhan\"")
@IdClass(ThongBaoNguoiNhanId.class)
@Getter
@Setter
public class ThongBaoNguoiNhan {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maThongBao\"")
    private ThongBao thongBao;

    @Id
    @Column(name = "\"maTaiKhoan\"", length = 20)
    private String taiKhoan;

    @Column(name = "\"daDoc\"", nullable = false)
    private boolean daDoc = false;

    @Column(name = "\"thoiDiemDoc\"")
    private LocalDateTime thoiDiemDoc;
}
