package vn.chungcumini.backend.web;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.chungcumini.backend.auth.AuthException;

/**
 * Chuẩn hóa lỗi trả về dạng {"message": "..."} để Next.js đọc res.json().message.
 *
 * Phải tự bắt MethodArgumentNotValidException ở đây — nếu để DefaultHandlerExceptionResolver
 * xử lý, nó gọi response.sendError(400) khiến container forward nội bộ sang "/error", request
 * forward đó lại đi qua SecurityFilterChain và bị anyRequest().authenticated() chặn thành 403
 * (che mất lỗi 400 thật). Tự viết @ExceptionHandler ở đây ghi response trực tiếp, không forward.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, String>> xuLyLoiDangNhap(AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> xuLyLoiDauVao(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> xuLyLoiValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ.");
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}
