package vn.chungcumini.backend.phieuthu;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/** PhieuThu.docSoTienBangChu: đọc số tiền bằng chữ tiếng Việt, vd 4.548.000 → "Bốn triệu năm trăm bốn mươi tám nghìn đồng". */
public final class SoTienBangChu {

    private static final String[] CHU_SO = {
        "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"
    };
    private static final String[] TEN_NHOM = {"", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ"};

    private SoTienBangChu() {
    }

    public static String doc(BigInteger soTien) {
        if (soTien.signum() == 0) {
            return "Không đồng";
        }
        List<Integer> nhom = new ArrayList<>();
        BigInteger con = soTien;
        BigInteger nghin = BigInteger.valueOf(1000);
        while (con.signum() > 0) {
            nhom.add(con.mod(nghin).intValue());
            con = con.divide(nghin);
        }

        int nhomDauTien = -1;
        for (int i = nhom.size() - 1; i >= 0; i--) {
            if (nhom.get(i) != 0) {
                nhomDauTien = i;
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = nhom.size() - 1; i >= 0; i--) {
            int gia = nhom.get(i);
            if (gia == 0) continue;
            if (sb.length() > 0) sb.append(" ");
            sb.append(docNhomBa(gia, i == nhomDauTien));
            if (!TEN_NHOM[i].isEmpty()) {
                sb.append(" ").append(TEN_NHOM[i]);
            }
        }
        sb.append(" đồng");

        String ketQua = sb.toString().replaceAll("\\s+", " ").trim();
        return Character.toUpperCase(ketQua.charAt(0)) + ketQua.substring(1);
    }

    private static String docNhomBa(int gia, boolean nhomDauTien) {
        int h = gia / 100;
        int t = (gia / 10) % 10;
        int u = gia % 10;
        StringBuilder sb = new StringBuilder();

        if (h != 0) {
            sb.append(CHU_SO[h]).append(" trăm");
        } else if (!nhomDauTien && (t != 0 || u != 0)) {
            sb.append("không trăm");
        }

        if (t == 0 && u != 0) {
            if (sb.length() > 0) sb.append(" linh ").append(CHU_SO[u]);
            else sb.append(CHU_SO[u]);
        } else if (t == 1) {
            if (sb.length() > 0) sb.append(" ");
            sb.append("mười");
            if (u != 0) sb.append(" ").append(u == 5 ? "lăm" : CHU_SO[u]);
        } else if (t >= 2) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(CHU_SO[t]).append(" mươi");
            if (u != 0) sb.append(" ").append(u == 1 ? "mốt" : u == 5 ? "lăm" : CHU_SO[u]);
        }

        return sb.toString();
    }
}
