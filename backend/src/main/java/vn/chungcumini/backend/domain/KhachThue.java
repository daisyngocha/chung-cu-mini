package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"KhachThue\"")
@Getter
@Setter
public class KhachThue {

    @Id
    @Column(name = "\"maKhach\"", length = 20)
    private String maKhach;

    @Column(name = "\"soDienThoai\"", length = 10, nullable = false, unique = true)
    private String soDienThoai;

    @Column(name = "\"hoTen\"", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "\"ngaySinh\"", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "\"soCCCD\"", length = 12, nullable = false)
    private String soCCCD;

    /** Lưu thư mục KHÔNG công khai, chỉ tài khoản chủ nhà mở được (BR-CT-06, mục 11). */
    @Column(name = "\"anhCCCD\"", length = 500)
    private String anhCCCD;

    @Column(name = "\"queQuan\"", length = 255)
    private String queQuan;

    @Column(name = "\"noiLamViec\"", length = 255)
    private String noiLamViec;
}
