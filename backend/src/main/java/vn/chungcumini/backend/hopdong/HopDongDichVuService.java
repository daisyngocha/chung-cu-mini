package vn.chungcumini.backend.hopdong;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.HopDongDichVu;
import vn.chungcumini.backend.domain.HopDongDichVuId;
import vn.chungcumini.backend.repository.HopDongDichVuRepository;

@Service
public class HopDongDichVuService {

    private final HopDongDichVuRepository hopDongDichVuRepository;

    public HopDongDichVuService(HopDongDichVuRepository hopDongDichVuRepository) {
        this.hopDongDichVuRepository = hopDongDichVuRepository;
    }

    public java.util.List<HopDongDichVu> layTheoHopDong(String maHopDong) {
        return hopDongDichVuRepository.findByHopDong_MaHopDong(maHopDong);
    }

    /** HopDongDichVu.tinhPhi: mucPhi × soLuong × tyLeNgayO cho 1 tháng. */
    public BigDecimal tinhPhi(String maHopDong, String maKhoan, BigDecimal tyLeNgayO) {
        if (tyLeNgayO.signum() <= 0 || tyLeNgayO.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Tỷ lệ ngày ở phải trong (0, 1].");
        }
        HopDongDichVu hddv = hopDongDichVuRepository.findById(new HopDongDichVuId(maHopDong, maKhoan))
                .orElseThrow(() -> new IllegalArgumentException("Hợp đồng không đăng ký khoản phí \"" + maKhoan + "\"."));
        return hddv.getMucPhi().multiply(BigDecimal.valueOf(hddv.getSoLuong())).multiply(tyLeNgayO);
    }
}
