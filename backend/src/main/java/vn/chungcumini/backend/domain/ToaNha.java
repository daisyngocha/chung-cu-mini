package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"ToaNha\"")
@Getter
@Setter
public class ToaNha {

    @Id
    @Column(name = "\"maToa\"", length = 10)
    private String maToa;

    @Column(name = "\"tenToa\"", length = 100, nullable = false)
    private String tenToa;

    @Column(name = "\"diaChi\"", length = 255, nullable = false)
    private String diaChi;

    @Column(name = "\"noiQuy\"", columnDefinition = "text")
    private String noiQuy;

    @Column(name = "\"ngayChotChiSo\"", nullable = false)
    private Integer ngayChotChiSo = 31;

    @Column(name = "\"hanThanhToan\"", nullable = false)
    private Integer hanThanhToan = 5;

    @Column(name = "\"nganHang\"", length = 10, nullable = false)
    private String nganHang;

    @Column(name = "\"soTaiKhoan\"", length = 20, nullable = false)
    private String soTaiKhoan;

    @Column(name = "\"chuTaiKhoan\"", length = 100, nullable = false)
    private String chuTaiKhoan;

    @Column(name = "\"tenChuNha\"", length = 100, nullable = false)
    private String tenChuNha;

    @Column(name = "\"sdtChuNha\"", length = 10, nullable = false)
    private String sdtChuNha;

    @Column(name = "\"diaChiChuNha\"", length = 255)
    private String diaChiChuNha;
}
