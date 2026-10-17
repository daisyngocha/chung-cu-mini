package vn.chungcumini.backend.khoitao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Một hợp đồng đang vận hành thực tế tại thời điểm bàn giao, cùng toàn bộ dữ liệu liên quan. Gộp
 * theo từng hợp đồng (thay vì các danh sách rời cần khóa tạm để nối) cho đơn giản và chắc chắn đúng
 * liên kết — tương đương nội dung dsKhach/dsNguoiO/dsHopDong/dsDichVu/dsChiSo/dsNoTon trong đặc tả.
 */
public record KhoiTaoHopDongItem(
        @NotNull @Valid KhoiTaoKhachInfo khach,
        @NotBlank String maPhong,
        @NotNull LocalDate ngayBatDau,
        @NotNull LocalDate ngayHetHan,
        @NotNull BigDecimal giaThue,
        @NotNull BigDecimal tienCoc,
        List<KhoiTaoDichVuItem> dsDichVu,
        List<KhoiTaoNguoiOItem> dsNguoiO,
        @NotNull @Valid KhoiTaoChiSoItem chiSoDien,
        @Valid KhoiTaoChiSoItem chiSoNuoc,
        BigDecimal noTon,
        Integer viTriNguoiOCapTaiKhoan) {
}
