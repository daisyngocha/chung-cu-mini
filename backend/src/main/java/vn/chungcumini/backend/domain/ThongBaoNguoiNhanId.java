package vn.chungcumini.backend.domain;

import java.io.Serializable;
import java.util.Objects;

public class ThongBaoNguoiNhanId implements Serializable {

    private String thongBao;
    private String taiKhoan;

    public ThongBaoNguoiNhanId() {
    }

    public ThongBaoNguoiNhanId(String thongBao, String taiKhoan) {
        this.thongBao = thongBao;
        this.taiKhoan = taiKhoan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThongBaoNguoiNhanId that)) return false;
        return Objects.equals(thongBao, that.thongBao) && Objects.equals(taiKhoan, that.taiKhoan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(thongBao, taiKhoan);
    }
}
