package vn.chungcumini.backend.auth;

/** Lỗi nghiệp vụ khi đăng nhập (sai mật khẩu, tài khoản khóa...) — luôn trả 401 kèm message cho người dùng. */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
