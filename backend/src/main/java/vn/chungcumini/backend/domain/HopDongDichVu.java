package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/** Phí dịch vụ cố định HĐ đăng ký (internet, vệ sinh, gửi xe...). Chỉ dịch vụ cố định; điện/nước không đăng ký ở đây. */
@Entity
@Table(name = "\"HopDongDichVu\"")
@IdClass(HopDongDichVuId.class)
@Getter
@Setter
public class HopDongDichVu {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maHopDong\"")
    private HopDong hopDong;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maKhoan\"")
    private BangGia bangGia;

    @Column(name = "\"soLuong\"", nullable = false)
    private Integer soLuong = 1;

    /** Chụp giá tại thời điểm ký; đổi mức phí qua LichSuHopDong. */
    @Column(name = "\"mucPhi\"", precision = 15, scale = 0, nullable = false)
    private BigDecimal mucPhi;

    @Column(name = "\"ngayApDung\"", nullable = false)
    private LocalDate ngayApDung;
}
