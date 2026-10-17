package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"NguoiO\"")
@Getter
@Setter
public class NguoiO {

    @Id
    @Column(name = "\"maNguoiO\"", length = 20)
    private String maNguoiO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maHopDong\"", nullable = false)
    private HopDong hopDong;

    @Column(name = "\"hoTen\"", length = 100, nullable = false)
    private String hoTen;

    /** Để trống được với trẻ em chưa có CCCD. */
    @Column(name = "\"soCCCD\"", length = 12)
    private String soCCCD;

    /** Bắt buộc khi người ở cùng được chọn cấp tài khoản (HopDong.maNguoiOCapTK). */
    @Column(name = "\"soDienThoai\"", length = 10)
    private String soDienThoai;

    @Column(name = "\"ngayVao\"", nullable = false)
    private LocalDate ngayVao;

    /** NULL = đang ở. */
    @Column(name = "\"ngayRa\"")
    private LocalDate ngayRa;
}
