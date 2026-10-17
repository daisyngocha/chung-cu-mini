package vn.chungcumini.backend.auth;

import vn.chungcumini.backend.domain.VaiTro;

public record DangNhapResponse(String token, VaiTro vaiTro, boolean phaiDoiMatKhau) {
}
