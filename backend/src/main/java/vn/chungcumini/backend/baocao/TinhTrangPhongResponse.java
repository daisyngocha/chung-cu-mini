package vn.chungcumini.backend.baocao;

import java.util.List;
import java.util.Map;
import vn.chungcumini.backend.domain.TrangThaiPhong;
import vn.chungcumini.backend.phong.PhongResponse;

/** DichVuBaoCao.tinhTrangPhong: số phòng theo trạng thái + sơ đồ tòa (thời gian thực). */
public record TinhTrangPhongResponse(Map<TrangThaiPhong, Long> soLuongTheoTrangThai, List<PhongResponse> soDoToaNha) {
}
