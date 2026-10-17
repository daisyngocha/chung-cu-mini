package vn.chungcumini.backend.baocao;

import java.math.BigDecimal;

/** DichVuBaoCao.congNoQuaHan: phòng, số còn nợ, số ngày quá hạn. */
public record CongNoQuaHanItem(String maPhong, String maThongBaoTien, BigDecimal soConNo, long soNgayQuaHan) {
}
