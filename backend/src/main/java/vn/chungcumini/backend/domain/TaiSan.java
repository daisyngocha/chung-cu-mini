package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"TaiSan\"")
@Getter
@Setter
public class TaiSan {

    @Id
    @Column(name = "\"maTaiSan\"", length = 20)
    private String maTaiSan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"maPhong\"", nullable = false)
    private Phong phong;

    @Column(name = "\"tenTaiSan\"", length = 100, nullable = false)
    private String tenTaiSan;

    @Column(name = "\"soLuong\"", nullable = false)
    private Integer soLuong = 1;

    @Column(name = "\"tinhTrang\"", length = 255, nullable = false)
    private String tinhTrang = "Tốt";

    @Column(name = "\"ghiChu\"", length = 500)
    private String ghiChu;
}
