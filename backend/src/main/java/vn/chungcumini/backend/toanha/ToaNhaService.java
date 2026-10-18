package vn.chungcumini.backend.toanha;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.ToaNha;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.repository.ToaNhaRepository;

@Service
public class ToaNhaService {

    private final ToaNhaRepository toaNhaRepository;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public ToaNhaService(ToaNhaRepository toaNhaRepository, NhatKyThaoTacService nhatKyThaoTacService) {
        this.toaNhaRepository = toaNhaRepository;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    public ToaNha layMacDinh() {
        return toaNhaRepository.findById("TOA01").orElseThrow();
    }

    /** CN-09.01: thông tin chung của tòa. */
    @Transactional
    public ToaNha capNhatThongTin(CapNhatThongTinRequest req, String maTaiKhoanThucHien) {
        ToaNha toaNha = layMacDinh();
        toaNha.setTenToa(req.tenToa());
        toaNha.setDiaChi(req.diaChi());
        toaNha.setNoiQuy(req.noiQuy());
        toaNha.setNgayChotChiSo(req.ngayChotChiSo());
        toaNha.setHanThanhToan(req.hanThanhToan());
        toaNha.setTenChuNha(req.tenChuNha());
        toaNha.setSdtChuNha(req.sdtChuNha());
        toaNha.setDiaChiChuNha(req.diaChiChuNha());
        toaNha = toaNhaRepository.save(toaNha);
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "ToaNha", toaNha.getMaToa(), "SUA", "Cập nhật thông tin tòa.");
        return toaNha;
    }

    /** CN-09.02: tài khoản nhận tiền dùng để sinh mã VietQR và in trên phiếu thu. */
    @Transactional
    public ToaNha capNhatTaiKhoanNhanTien(CapNhatTaiKhoanNhanTienRequest req, String maTaiKhoanThucHien) {
        ToaNha toaNha = layMacDinh();
        toaNha.setNganHang(req.nganHang());
        toaNha.setSoTaiKhoan(req.soTaiKhoan());
        toaNha.setChuTaiKhoan(req.chuTaiKhoan());
        toaNha = toaNhaRepository.save(toaNha);
        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien, "ToaNha", toaNha.getMaToa(), "SUA", "Cập nhật tài khoản nhận tiền.");
        return toaNha;
    }

    /**
     * ToaNha.taoMaVietQR: dùng dịch vụ ảnh VietQR công khai (img.vietqr.io) theo đúng gợi ý của đặc tả,
     * thay vì tự sinh payload EMVCo + CRC16. Trả về URL ảnh QR.
     */
    public String taoMaVietQR(BigDecimal soTien, String noiDung) {
        if (soTien == null || soTien.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0.");
        }
        if (noiDung == null || noiDung.length() > 25 || !noiDung.matches("^[A-Za-z0-9]+$")) {
            throw new IllegalArgumentException("Nội dung chuyển khoản chỉ gồm chữ và số, tối đa 25 ký tự.");
        }
        ToaNha toaNha = layMacDinh();

        String addInfo = URLEncoder.encode(noiDung, StandardCharsets.UTF_8);
        String accountName = URLEncoder.encode(toaNha.getChuTaiKhoan(), StandardCharsets.UTF_8);
        return "https://img.vietqr.io/image/" + toaNha.getNganHang() + "-" + toaNha.getSoTaiKhoan() + "-compact2.png"
                + "?amount=" + soTien.toBigInteger() + "&addInfo=" + addInfo + "&accountName=" + accountName;
    }
}
