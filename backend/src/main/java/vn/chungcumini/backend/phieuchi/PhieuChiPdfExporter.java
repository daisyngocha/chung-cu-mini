package vn.chungcumini.backend.phieuchi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import vn.chungcumini.backend.domain.PhieuChi;
import vn.chungcumini.backend.domain.ToaNha;

/** PhieuChi.xuatPDF: số phiếu, ngày, người nhận, số tiền bằng số và chữ, hình thức, thông tin chủ nhà. */
final class PhieuChiPdfExporter {

    private static final float LE = 50f;

    private PhieuChiPdfExporter() {
    }

    static byte[] xuat(PhieuChi phieuChi, String soTienBangChu) {
        ToaNha toaNha = phieuChi.getQuyetToan().getHopDong().getPhong().getToaNha();
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDFont font = taiFont(doc);
            float y = page.getMediaBox().getHeight() - LE;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                y = dongTieuDe(cs, font, "PHIẾU CHI", y);
                y = dong(cs, font, 11, "(Hoàn tiền cọc — không phải hóa đơn điện tử)", y - 6);
                y -= 20;
                y = dong(cs, font, 11, "Số phiếu: " + phieuChi.getSoPhieuChi(), y);
                y = dong(cs, font, 11, "Ngày chi: " + phieuChi.getNgayChi(), y);
                y = dong(cs, font, 11, "Người nhận: " + phieuChi.getNguoiNhan(), y);
                y = dong(cs, font, 11, "Hình thức: " + phieuChi.getHinhThuc(), y);
                y -= 10;
                y = dong(cs, font, 12, "Số tiền: " + dinhDangTien(phieuChi.getSoTien()) + " đ", y);
                y = dong(cs, font, 11, "Bằng chữ: " + soTienBangChu, y);
                y -= 20;
                y = dong(cs, font, 10, "Chủ nhà: " + toaNha.getTenChuNha(), y);
                y = dong(cs, font, 10, "SĐT: " + toaNha.getSdtChuNha(), y);
                if (toaNha.getDiaChiChuNha() != null) {
                    dong(cs, font, 10, "Địa chỉ: " + toaNha.getDiaChiChuNha(), y);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("Không xuất được PDF phiếu chi.", e);
        }
    }

    private static float dongTieuDe(PDPageContentStream cs, PDFont font, String tieuDe, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, 16);
        cs.newLineAtOffset(LE, y);
        cs.showText(tieuDe);
        cs.endText();
        return y - 24;
    }

    private static float dong(PDPageContentStream cs, PDFont font, int coChu, String noiDung, float y) throws IOException {
        cs.beginText();
        cs.setFont(font, coChu);
        cs.newLineAtOffset(LE, y);
        cs.showText(noiDung);
        cs.endText();
        return y - (coChu + 6);
    }

    private static PDFont taiFont(PDDocument doc) throws IOException {
        try (InputStream in = PhieuChiPdfExporter.class.getResourceAsStream("/fonts/DejaVuSans.ttf")) {
            return PDType0Font.load(doc, in);
        }
    }

    private static String dinhDangTien(java.math.BigDecimal soTien) {
        return String.format("%,d", soTien.toBigInteger()).replace(',', '.');
    }
}
