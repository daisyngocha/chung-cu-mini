package vn.chungcumini.backend.phieuthu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.PhieuThu;
import vn.chungcumini.backend.domain.TrangThaiPT;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.repository.DongThongBaoTienRepository;
import vn.chungcumini.backend.repository.PhieuThuRepository;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;

@Service
public class PhieuThuService {

    private final PhieuThuRepository phieuThuRepository;
    private final DongThongBaoTienRepository dongThongBaoTienRepository;
    private final ThongBaoTienService thongBaoTienService;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public PhieuThuService(
            PhieuThuRepository phieuThuRepository,
            DongThongBaoTienRepository dongThongBaoTienRepository,
            ThongBaoTienService thongBaoTienService,
            NhatKyThaoTacService nhatKyThaoTacService) {
        this.phieuThuRepository = phieuThuRepository;
        this.dongThongBaoTienRepository = dongThongBaoTienRepository;
        this.thongBaoTienService = thongBaoTienService;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    public PhieuThu timHoacNem(String soPhieuThu) {
        return phieuThuRepository.timChiTiet(soPhieuThu)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu thu \"" + soPhieuThu + "\"."));
    }

    public List<PhieuThu> layTheoThongBao(String maThongBaoTien) {
        return phieuThuRepository.findByThongBaoTien_MaThongBaoTien(maThongBaoTien);
    }

    public List<PhieuThu> layTheoTaiKhoanKhach(String maTaiKhoan) {
        return phieuThuRepository.findByThongBaoTien_HopDong_MaTaiKhoanOrderByNgayThuDesc(maTaiKhoan);
    }

    /** PhieuThu.sinhSoPhieu. */
    public String sinhSoPhieu(LocalDate ngay) {
        return PhieuThuIdGenerator.sinh(phieuThuRepository, ngay);
    }

    /** PhieuThu.docSoTienBangChu. */
    public String docSoTienBangChu(BigDecimal soTien) {
        return SoTienBangChu.doc(soTien.toBigInteger());
    }

    /** PhieuThu.huy: không xóa (BR-TT-02). */
    @Transactional
    public void huy(String soPhieuThu, String lyDo, String maTaiKhoanThucHien) {
        PhieuThu phieuThu = timHoacNem(soPhieuThu);
        if (phieuThu.getTrangThai() != TrangThaiPT.HIEU_LUC) {
            throw new IllegalArgumentException("Phiếu thu đã hủy.");
        }
        if (lyDo == null || lyDo.isBlank()) {
            throw new IllegalArgumentException("Bắt buộc ghi lý do hủy.");
        }
        phieuThu.setTrangThai(TrangThaiPT.DA_HUY);
        phieuThu.setLyDoHuy(lyDo);
        phieuThuRepository.save(phieuThu);

        thongBaoTienService.capNhatTrangThaiThu(phieuThu.getThongBaoTien().getMaThongBaoTien());
        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "PhieuThu", soPhieuThu, "HUY", lyDo);
    }

    /**
     * PhieuThu.xuatPDF: số phiếu, ngày thu, phòng, người nộp, kỳ, chi tiết từng khoản, tổng bằng số
     * và chữ, hình thức, thông tin chủ nhà. Ghi rõ "không phải hóa đơn điện tử" (BR-HD-06).
     * Khách chỉ xem phiếu của phòng mình — kiểm tra ở service gọi (Controller truyền maTaiKhoanXem).
     */
    public byte[] xuatPDF(String soPhieuThu) {
        PhieuThu phieuThu = timHoacNem(soPhieuThu);
        var dong = dongThongBaoTienRepository.findByThongBaoTien_MaThongBaoTien(phieuThu.getThongBaoTien().getMaThongBaoTien());
        return PhieuThuPdfExporter.xuat(phieuThu, docSoTienBangChu(phieuThu.getSoTien()), dong);
    }
}
