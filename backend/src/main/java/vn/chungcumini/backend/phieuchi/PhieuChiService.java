package vn.chungcumini.backend.phieuchi;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.PhieuChi;
import vn.chungcumini.backend.repository.PhieuChiRepository;

@Service
public class PhieuChiService {

    private final PhieuChiRepository phieuChiRepository;

    public PhieuChiService(PhieuChiRepository phieuChiRepository) {
        this.phieuChiRepository = phieuChiRepository;
    }

    public PhieuChi timHoacNem(String soPhieuChi) {
        return phieuChiRepository.timChiTiet(soPhieuChi)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phiếu chi \"" + soPhieuChi + "\"."));
    }

    public Optional<PhieuChi> timTheoQuyetToan(String maQuyetToan) {
        return phieuChiRepository.findByQuyetToan_MaQuyetToan(maQuyetToan);
    }

    /** PhieuChi.docSoTienBangChu. */
    public String docSoTienBangChu(BigDecimal soTien) {
        return SoTienBangChu.doc(soTien.toBigInteger());
    }

    /** PhieuChi.xuatPDF. */
    public byte[] xuatPDF(String soPhieuChi) {
        PhieuChi phieuChi = timHoacNem(soPhieuChi);
        return PhieuChiPdfExporter.xuat(phieuChi, docSoTienBangChu(phieuChi.getSoTien()));
    }
}
