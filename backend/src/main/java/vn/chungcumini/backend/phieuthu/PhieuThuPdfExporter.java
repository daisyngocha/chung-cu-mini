package vn.chungcumini.backend.phieuthu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import vn.chungcumini.backend.domain.DongThongBaoTien;
import vn.chungcumini.backend.domain.PhieuThu;

/**
 * PhieuThu.xuatPDF (BR-HD-06: không phải hóa đơn điện tử). Dùng font DejaVu Sans nhúng sẵn trong
 * resources/fonts — font chuẩn của PDFBox (Helvetica...) không có dấu tiếng Việt.
 */
final class PhieuThuPdfExporter {

    private static final float LE = 50;

    private PhieuThuPdfExporter() {
    }

    static byte[] xuat(PhieuThu phieuThu, String soTienBangChu, List<DongThongBaoTien> dong) {
        try (PDDocument doc = new PDDocument()) {
            PDPage trang = new PDPage(PDRectangle.A4);
            doc.addPage(trang);
            PDFont font = taiFont(doc);

            var hopDong = phieuThu.getThongBaoTien().getHopDong();
            var toaNha = hopDong.getPhong().getToaNha();

            try (PDPageContentStream cs = new PDPageContentStream(doc, trang)) {
                float y = PDRectangle.A4.getHeight() - 60;
                y = dongTieuDe(cs, font, "PHIẾU THU", y);
                y = dong(cs, font, 10, "(Không phải hóa đơn điện tử)", y, 18);

                y -= 10;
                y = dong(cs, font, 11, "Số phiếu: " + phieuThu.getSoPhieuThu(), y, 16);
                y = dong(cs, font, 11, "Ngày thu: " + phieuThu.getNgayThu(), y, 16);
                y = dong(cs, font, 11, "Tòa nhà: " + toaNha.getTenToa() + " — Phòng: " + hopDong.getPhong().getMaPhong(), y, 16);
                y = dong(cs, font, 11, "Người nộp: " + phieuThu.getNguoiNop(), y, 16);
                y = dong(cs, font, 11, "Kỳ thanh toán: " + phieuThu.getThongBaoTien().getKyThang(), y, 16);
                y = dong(cs, font, 11, "Hình thức: " + phieuThu.getHinhThuc(), y, 16);

                y -= 10;
                y = dong(cs, font, 11, "Chi tiết các khoản:", y, 16);
                for (DongThongBaoTien d : dong) {
                    y = dong(cs, font, 10, "  - " + d.getDienGiai() + ": " + dinhDangTien(d.getThanhTien()) + " đ", y, 14);
                }

                y -= 10;
                y = dong(cs, font, 12, "Tổng tiền: " + dinhDangTien(phieuThu.getSoTien()) + " đ", y, 16);
                y = dong(cs, font, 11, "Bằng chữ: " + soTienBangChu, y, 16);

                y -= 20;
                y = dong(cs, font, 10, "Chủ nhà: " + toaNha.getTenChuNha(), y, 14);
                y = dong(cs, font, 10, "SĐT: " + toaNha.getSdtChuNha(), y, 14);
                dong(cs, font, 10, "Địa chỉ: " + (toaNha.getDiaChiChuNha() != null ? toaNha.getDiaChiChuNha() : toaNha.getDiaChi()), y, 14);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static PDFont taiFont(PDDocument doc) throws IOException {
        try (InputStream in = PhieuThuPdfExporter.class.getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            return PDType0Font.load(doc, in);
        }
    }

    private static float dongTieuDe(PDPageContentStream cs, PDFont font, String text, float y) throws IOException {
        return dong(cs, font, 16, text, y, 24);
    }

    private static float dong(PDPageContentStream cs, PDFont font, float coChu, String text, float y, float khoangCach)
            throws IOException {
        cs.beginText();
        cs.setFont(font, coChu);
        cs.newLineAtOffset(LE, y);
        cs.showText(text);
        cs.endText();
        return y - khoangCach;
    }

    private static String dinhDangTien(java.math.BigDecimal soTien) {
        return String.format("%,d", soTien.toBigInteger()).replace(',', '.');
    }
}
