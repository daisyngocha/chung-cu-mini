package vn.chungcumini.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.TaiKhoan;

/** Tương đương lib/session.ts (jose) ở phía Next.js trước đây — nay JWT do backend này tự ký. */
@Service
public class JwtService {

    private static final Duration THOI_HAN = Duration.ofHours(8);

    private final SecretKey khoa;

    public JwtService(@Value("${app.jwt-secret}") String jwtSecret) {
        this.khoa = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String taoToken(TaiKhoan taiKhoan) {
        Date bayGio = new Date();
        return Jwts.builder()
                .subject(taiKhoan.getMaTaiKhoan())
                // Next.js (lib/session.ts) đọc "maTaiKhoan" trực tiếp từ payload, không dùng "sub",
                // nên phải khai báo thêm claim này để hai bên đọc cùng một shape JWT.
                .claim("maTaiKhoan", taiKhoan.getMaTaiKhoan())
                .claim("tenDangNhap", taiKhoan.getTenDangNhap())
                .claim("vaiTro", taiKhoan.getVaiTro().name())
                .claim("phaiDoiMatKhau", taiKhoan.isPhaiDoiMatKhau())
                .issuedAt(bayGio)
                .expiration(new Date(bayGio.getTime() + THOI_HAN.toMillis()))
                .signWith(khoa)
                .compact();
    }

    public Claims xacThucToken(String token) {
        return Jwts.parser().verifyWith(khoa).build().parseSignedClaims(token).getPayload();
    }
}
