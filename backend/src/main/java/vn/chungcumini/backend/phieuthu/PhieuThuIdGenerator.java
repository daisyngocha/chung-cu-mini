package vn.chungcumini.backend.phieuthu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import vn.chungcumini.backend.repository.PhieuThuRepository;

/**
 * PhieuThu.sinhSoPhieu: PT-yyyy-NNNNN tăng dần trong năm, không tái sử dụng số đã hủy (BR-TT-02).
 * Dùng chung cho ThongBaoTien.ghiNhanThu và BaoThanhToan.xacNhan — cả hai tự có PhieuThuRepository
 * riêng (không gọi qua PhieuThuService) để tránh phụ thuộc vòng giữa các service.
 */
public final class PhieuThuIdGenerator {

    private static final DateTimeFormatter NAM = DateTimeFormatter.ofPattern("yyyy");

    private PhieuThuIdGenerator() {
    }

    public static String sinh(PhieuThuRepository phieuThuRepository, LocalDate ngay) {
        String tienTo = "PT-" + ngay.format(NAM) + "-";
        long soLuong = phieuThuRepository.countBySoPhieuThuStartingWith(tienTo);
        return tienTo + String.format("%05d", soLuong + 1);
    }
}
