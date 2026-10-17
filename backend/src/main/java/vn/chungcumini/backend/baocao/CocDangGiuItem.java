package vn.chungcumini.backend.baocao;

import java.math.BigDecimal;

/** DichVuBaoCao.cocDangGiu: tổng cọc chưa hoàn theo phòng. */
public record CocDangGiuItem(String maPhong, String maHopDong, BigDecimal tienCoc) {
}
