package vn.chungcumini.backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Ánh xạ rút gọn: chỉ các cột mà Phong cần đọc (BR-CT-03, Phong.coTheXoa/layHopDongHieuLuc/laSapTra).
 * Chưa dùng để tạo/sửa hợp đồng — các thuộc tính và phương thức còn lại của lớp HopDong
 * (maKhach, giaThue, taoMoi, xacNhanNhanPhong...) thuộc giai đoạn kế tiếp (module Hợp đồng).
 */
@Entity
@Table(name = "\"HopDong\"")
@Getter
@Setter
public class HopDong {

    @Id
    @Column(name = "\"maHopDong\"", length = 20)
    private String maHopDong;

    @Column(name = "\"maPhong\"", length = 10, nullable = false)
    private String maPhong;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "\"trangThai\"", columnDefinition = "\"TrangThaiHD\"", nullable = false)
    private TrangThaiHD trangThai;

    @Column(name = "\"ngayBaoTra\"")
    private LocalDate ngayBaoTra;

    @Column(name = "\"ngayTraDuKien\"")
    private LocalDate ngayTraDuKien;

    @Column(name = "\"ngayTraThucTe\"")
    private LocalDate ngayTraThucTe;
}
