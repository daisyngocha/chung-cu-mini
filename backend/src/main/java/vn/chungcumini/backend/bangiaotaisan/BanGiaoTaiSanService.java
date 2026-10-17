package vn.chungcumini.backend.bangiaotaisan;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.BanGiaoTaiSan;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiBanGiao;
import vn.chungcumini.backend.domain.TaiSan;
import vn.chungcumini.backend.repository.BanGiaoTaiSanRepository;
import vn.chungcumini.backend.repository.TaiSanRepository;

@Service
public class BanGiaoTaiSanService {

    private final BanGiaoTaiSanRepository banGiaoTaiSanRepository;
    private final TaiSanRepository taiSanRepository;

    public BanGiaoTaiSanService(BanGiaoTaiSanRepository banGiaoTaiSanRepository, TaiSanRepository taiSanRepository) {
        this.banGiaoTaiSanRepository = banGiaoTaiSanRepository;
        this.taiSanRepository = taiSanRepository;
    }

    public List<BanGiaoTaiSan> layTheoHopDongVaLoai(String maHopDong, LoaiBanGiao loaiBanGiao) {
        return banGiaoTaiSanRepository.findByHopDong_MaHopDongAndLoaiBanGiao(maHopDong, loaiBanGiao);
    }

    /** BanGiaoTaiSan.ghiNhanTinhTrang: lưu bản ghi bàn giao; cập nhật TaiSan.tinhTrang (BR-TL-02). */
    @Transactional
    public BanGiaoTaiSan ghiNhanTinhTrang(
            HopDong hopDong,
            TaiSan taiSan,
            LoaiBanGiao loaiBanGiao,
            String tinhTrang,
            String anh,
            BigDecimal chiPhiHuHong) {
        if (tinhTrang == null || tinhTrang.isBlank()) {
            throw new IllegalArgumentException("Tình trạng không được rỗng.");
        }
        BanGiaoTaiSan banGiao = new BanGiaoTaiSan();
        banGiao.setMaBanGiao("BGTS" + String.format("%06d", banGiaoTaiSanRepository.count() + 1));
        banGiao.setHopDong(hopDong);
        banGiao.setTaiSan(taiSan);
        banGiao.setLoaiBanGiao(loaiBanGiao);
        banGiao.setTinhTrang(tinhTrang);
        banGiao.setAnh(anh);
        if (loaiBanGiao == LoaiBanGiao.TRA_PHONG && chiPhiHuHong != null) {
            banGiao.setChiPhiHuHong(chiPhiHuHong);
        }
        banGiao = banGiaoTaiSanRepository.save(banGiao);

        taiSan.setTinhTrang(tinhTrang);
        taiSanRepository.save(taiSan);

        return banGiao;
    }

    /** BanGiaoTaiSan.soSanhLucNhanPhong: chuỗi mô tả khác biệt so với bản ghi NHAN_PHONG cùng HĐ, cùng tài sản. */
    public String soSanhLucNhanPhong(String maBanGiao) {
        BanGiaoTaiSan traPhong = timHoacNem(maBanGiao);
        if (traPhong.getLoaiBanGiao() != LoaiBanGiao.TRA_PHONG) {
            throw new IllegalArgumentException("Chỉ áp dụng cho bản ghi trả phòng.");
        }
        Optional<BanGiaoTaiSan> nhanPhong = banGiaoTaiSanRepository.findByHopDong_MaHopDongAndTaiSan_MaTaiSanAndLoaiBanGiao(
                traPhong.getHopDong().getMaHopDong(), traPhong.getTaiSan().getMaTaiSan(), LoaiBanGiao.NHAN_PHONG);
        if (nhanPhong.isEmpty()) {
            return "Không có bản ghi nhận phòng để so sánh.";
        }
        String luc = nhanPhong.get().getTinhTrang();
        String tra = traPhong.getTinhTrang();
        if (luc.equals(tra)) {
            return "Không thay đổi so với lúc nhận phòng (\"" + luc + "\").";
        }
        return "Lúc nhận phòng: \"" + luc + "\" → Lúc trả phòng: \"" + tra + "\".";
    }

    /** BanGiaoTaiSan.tinhChiPhiHuHong: chi phí hư hỏng của bản ghi trả phòng. */
    public BigDecimal tinhChiPhiHuHong(String maBanGiao) {
        BanGiaoTaiSan banGiao = timHoacNem(maBanGiao);
        if (banGiao.getLoaiBanGiao() != LoaiBanGiao.TRA_PHONG) {
            throw new IllegalArgumentException("Chỉ áp dụng cho bản ghi trả phòng.");
        }
        return banGiao.getChiPhiHuHong();
    }

    private BanGiaoTaiSan timHoacNem(String maBanGiao) {
        return banGiaoTaiSanRepository.findById(maBanGiao)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bản ghi bàn giao \"" + maBanGiao + "\"."));
    }
}
