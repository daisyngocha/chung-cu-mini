package vn.chungcumini.backend.taikhoan;

import java.security.SecureRandom;

/** Mục 11: mật khẩu tạm 8 ký tự, hiển thị 1 lần cho chủ nhà. */
final class MatKhauTamGenerator {

    private static final String BANG_KY_TU = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private MatKhauTamGenerator() {
    }

    static String sinh() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(BANG_KY_TU.charAt(RANDOM.nextInt(BANG_KY_TU.length())));
        }
        return sb.toString();
    }
}
