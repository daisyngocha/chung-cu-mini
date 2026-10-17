package vn.chungcumini.backend.domain;

import java.io.Serializable;
import java.util.Objects;

public class HopDongDichVuId implements Serializable {

    private String hopDong;
    private String bangGia;

    public HopDongDichVuId() {
    }

    public HopDongDichVuId(String hopDong, String bangGia) {
        this.hopDong = hopDong;
        this.bangGia = bangGia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HopDongDichVuId that)) return false;
        return Objects.equals(hopDong, that.hopDong) && Objects.equals(bangGia, that.bangGia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hopDong, bangGia);
    }
}
