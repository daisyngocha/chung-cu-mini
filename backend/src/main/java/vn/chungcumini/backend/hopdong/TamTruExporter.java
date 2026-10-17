package vn.chungcumini.backend.hopdong;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.NguoiO;

/** HopDong.xuatDanhSachTamTru: người đứng tên + người ở cùng mới trong khoảng ngày (UC03.08). */
final class TamTruExporter {

    private TamTruExporter() {
    }

    static byte[] xuat(List<HopDong> nguoiDungTen, List<NguoiO> nguoiOCung) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Danh sach tam tru");
            String[] cot = {"Họ tên", "Ngày sinh", "Số CCCD", "Quê quán", "Ngày vào", "Vai trò", "Phòng"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < cot.length; i++) {
                header.createCell(i).setCellValue(cot[i]);
            }

            int r = 1;
            for (HopDong hd : nguoiDungTen) {
                Row row = sheet.createRow(r++);
                var khach = hd.getKhachThue();
                row.createCell(0).setCellValue(khach.getHoTen());
                row.createCell(1).setCellValue(toText(khach.getNgaySinh()));
                row.createCell(2).setCellValue(khach.getSoCCCD());
                row.createCell(3).setCellValue(khach.getQueQuan() != null ? khach.getQueQuan() : "");
                row.createCell(4).setCellValue(toText(hd.getNgayNhanPhong()));
                row.createCell(5).setCellValue("Người đứng tên");
                row.createCell(6).setCellValue(hd.getPhong().getMaPhong());
            }
            for (NguoiO no : nguoiOCung) {
                Row row = sheet.createRow(r++);
                row.createCell(0).setCellValue(no.getHoTen());
                row.createCell(1).setCellValue("");
                row.createCell(2).setCellValue(no.getSoCCCD() != null ? no.getSoCCCD() : "");
                row.createCell(3).setCellValue("");
                row.createCell(4).setCellValue(toText(no.getNgayVao()));
                row.createCell(5).setCellValue("Người ở cùng");
                row.createCell(6).setCellValue(no.getHopDong().getPhong().getMaPhong());
            }
            for (int i = 0; i < cot.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String toText(LocalDate d) {
        return d == null ? "" : d.toString();
    }
}
