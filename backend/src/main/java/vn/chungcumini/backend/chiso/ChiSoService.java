package vn.chungcumini.backend.chiso;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.ChiSo;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiCongTo;
import vn.chungcumini.backend.domain.LoaiGhi;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.repository.ChiSoRepository;
import vn.chungcumini.backend.repository.HopDongRepository;

@Service
public class ChiSoService {

    private final ChiSoRepository chiSoRepository;
    private final HopDongRepository hopDongRepository;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public ChiSoService(
            ChiSoRepository chiSoRepository, HopDongRepository hopDongRepository, NhatKyThaoTacService nhatKyThaoTacService) {
        this.chiSoRepository = chiSoRepository;
        this.hopDongRepository = hopDongRepository;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    public List<ChiSo> layTheoHopDong(String maHopDong) {
        return chiSoRepository.findByHopDong_MaHopDongOrderByNgayGhiDesc(maHopDong);
    }

    /** ChiSo.kiemTraHopLe: chỉ số mới không được nhỏ hơn chỉ số cũ (UC05.03). */
    public boolean kiemTraHopLe(int chiSoCu, int chiSoMoi) {
        return chiSoMoi >= chiSoCu;
    }

    /** ChiSo.tinhTieuThu. */
    public int tinhTieuThu(ChiSo chiSo) {
        return chiSo.getChiSoMoi() - chiSo.getChiSoCu();
    }

    /**
     * ChiSo.ghiChiSo: tự lấy chiSoCu từ lần ghi trước cùng HĐ, cùng loại công tơ (DAU_KY: chiSoCu = chiSoMoi).
     * loaiGhi không có trong tham số của sheet 3 nhưng là thuộc tính NOT NULL của ChiSo — do ngữ cảnh gọi
     * (nhận phòng / hàng tháng / trả phòng) quyết định, nên bổ sung làm tham số thực tế ở đây.
     */
    @Transactional
    public ChiSo ghiChiSo(
            String maHopDong,
            LoaiCongTo loaiCongTo,
            LoaiGhi loaiGhi,
            String kyThang,
            int chiSoMoi,
            String anhCongTo,
            String maTaiKhoanThucHien) {
        HopDong hopDong = hopDongRepository.findById(maHopDong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hợp đồng \"" + maHopDong + "\"."));
        if (hopDong.getTrangThai() != TrangThaiHD.CHO_NHAN_PHONG && hopDong.getTrangThai() != TrangThaiHD.HIEU_LUC) {
            throw new IllegalArgumentException("Chỉ ghi chỉ số cho hợp đồng chờ nhận phòng hoặc đang hiệu lực.");
        }
        if (loaiCongTo == LoaiCongTo.NUOC && !hopDong.getPhong().isCoCongToNuoc()) {
            throw new IllegalArgumentException("Phòng không có công tơ nước riêng.");
        }

        int chiSoCu = chiSoRepository
                .findFirstByHopDong_MaHopDongAndLoaiCongToOrderByNgayGhiDescMaChiSoDesc(maHopDong, loaiCongTo)
                .map(ChiSo::getChiSoMoi)
                .orElse(chiSoMoi);

        if (!kiemTraHopLe(chiSoCu, chiSoMoi)) {
            throw new IllegalArgumentException("Chỉ số mới (" + chiSoMoi + ") không được nhỏ hơn chỉ số cũ (" + chiSoCu + ").");
        }

        ChiSo chiSo = new ChiSo();
        chiSo.setMaChiSo("CS" + String.format("%06d", chiSoRepository.count() + 1));
        chiSo.setHopDong(hopDong);
        chiSo.setLoaiCongTo(loaiCongTo);
        chiSo.setKyThang(kyThang);
        chiSo.setLoaiGhi(loaiGhi);
        chiSo.setNgayGhi(LocalDate.now());
        chiSo.setChiSoCu(chiSoCu);
        chiSo.setChiSoMoi(chiSoMoi);
        chiSo.setAnhCongTo(anhCongTo);
        chiSo = chiSoRepository.save(chiSo);

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien,
                "ChiSo",
                chiSo.getMaChiSo(),
                "TAO",
                "Ghi chỉ số " + loaiCongTo + " kỳ " + kyThang + ": " + chiSoCu + " → " + chiSoMoi + ".");
        return chiSo;
    }
}
