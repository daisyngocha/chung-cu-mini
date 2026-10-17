package vn.chungcumini.backend.phieuchi;

import java.math.BigInteger;

/**
 * PhieuChi.docSoTienBangChu — đọc số tiền bằng chữ tiếng Việt. Trùng với
 * vn.chungcumini.backend.phieuthu.SoTienBangChu (GĐ4, nhánh riêng chưa gộp vào lúc viết GĐ5) —
 * tách riêng để nhánh này tự đủ, có thể gộp lại thành 1 lớp dùng chung ở lần dọn dẹp sau.
 */
public final class SoTienBangChu {

    private static final String[] CHU_SO = {
        "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
    };
    private static final String[] DON_VI_NHOM = {"", "nghìn", "triệu", "tỷ"};

    private SoTienBangChu() {
    }

    public static String doc(BigInteger soTien) {
        if (soTien.signum() == 0) {
            return "Không đồng";
        }
        boolean am = soTien.signum() < 0;
        BigInteger trieuTuyetDoi = soTien.abs();

        java.util.List<Integer> nhom = new java.util.ArrayList<>();
        BigInteger con = trieuTuyetDoi;
        BigInteger nghin = BigInteger.valueOf(1000);
        while (con.signum() > 0) {
            nhom.add(con.mod(nghin).intValue());
            con = con.divide(nghin);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = nhom.size() - 1; i >= 0; i--) {
            int giaTri = nhom.get(i);
            if (giaTri == 0 && !(i == nhom.size() - 1)) {
                if (coNhomKhongRong(nhom, i)) {
                    sb.append("không nghìn ");
                }
                continue;
            }
            if (giaTri == 0) {
                continue;
            }
            sb.append(docNhom3ChuSo(giaTri, i != nhom.size() - 1));
            String donVi = donViNhom(i);
            if (!donVi.isEmpty()) {
                sb.append(donVi).append(' ');
            }
        }

        String ketQua = sb.toString().trim();
        ketQua = ketQua.substring(0, 1).toUpperCase() + ketQua.substring(1);
        return (am ? "Âm " + ketQua.substring(0, 1).toLowerCase() + ketQua.substring(1) : ketQua) + " đồng";
    }

    private static boolean coNhomKhongRong(java.util.List<Integer> nhom, int i) {
        for (int j = i - 1; j >= 0; j--) {
            if (nhom.get(j) != 0) {
                return true;
            }
        }
        return false;
    }

    private static String donViNhom(int bac) {
        if (bac < DON_VI_NHOM.length) {
            return DON_VI_NHOM[bac];
        }
        StringBuilder sb = new StringBuilder();
        int bacConLai = bac;
        while (bacConLai >= 3) {
            sb.append("tỷ ");
            bacConLai -= 3;
        }
        return (DON_VI_NHOM[bacConLai] + " " + sb).trim();
    }

    /** Đọc 1 nhóm 3 chữ số (0–999). coNhomTruoc = true khi còn nhóm cao hơn khác 0 phía trước (cần "không trăm"). */
    private static String docNhom3ChuSo(int giaTri, boolean coNhomTruoc) {
        int tram = giaTri / 100;
        int chuc = (giaTri % 100) / 10;
        int donVi = giaTri % 10;
        StringBuilder sb = new StringBuilder();

        if (tram > 0) {
            sb.append(CHU_SO[tram]).append(" trăm ");
        } else if (coNhomTruoc) {
            sb.append("không trăm ");
        }

        if (chuc == 0) {
            if (donVi > 0) {
                if (tram > 0 || coNhomTruoc) {
                    sb.append("linh ");
                }
                sb.append(CHU_SO[donVi]).append(' ');
            }
        } else if (chuc == 1) {
            sb.append("mười ");
            if (donVi == 5) {
                sb.append("lăm ");
            } else if (donVi > 0) {
                sb.append(CHU_SO[donVi]).append(' ');
            }
        } else {
            sb.append(CHU_SO[chuc]).append(" mươi ");
            if (donVi == 1) {
                sb.append("mốt ");
            } else if (donVi == 5) {
                sb.append("lăm ");
            } else if (donVi > 0) {
                sb.append(CHU_SO[donVi]).append(' ');
            }
        }
        return sb.toString();
    }
}
