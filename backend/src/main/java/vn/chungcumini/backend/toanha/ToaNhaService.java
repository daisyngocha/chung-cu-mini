package vn.chungcumini.backend.toanha;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.ToaNha;
import vn.chungcumini.backend.repository.ToaNhaRepository;

@Service
public class ToaNhaService {

    private final ToaNhaRepository toaNhaRepository;

    public ToaNhaService(ToaNhaRepository toaNhaRepository) {
        this.toaNhaRepository = toaNhaRepository;
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
        ToaNha toaNha = toaNhaRepository.findById("TOA01").orElseThrow();

        String addInfo = URLEncoder.encode(noiDung, StandardCharsets.UTF_8);
        String accountName = URLEncoder.encode(toaNha.getChuTaiKhoan(), StandardCharsets.UTF_8);
        return "https://img.vietqr.io/image/" + toaNha.getNganHang() + "-" + toaNha.getSoTaiKhoan() + "-compact2.png"
                + "?amount=" + soTien.toBigInteger() + "&addInfo=" + addInfo + "&accountName=" + accountName;
    }
}
