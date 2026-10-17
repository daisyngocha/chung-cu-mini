package vn.chungcumini.backend.suco;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.LichSuSuCo;
import vn.chungcumini.backend.domain.SuCo;
import vn.chungcumini.backend.domain.TrangThaiSuCo;
import vn.chungcumini.backend.domain.VaiTro;
import vn.chungcumini.backend.repository.LichSuSuCoRepository;

/** LichSuSuCo.ghiNhan: ghi lịch sử mỗi lần đổi trạng thái sự cố — chỉ thêm, cả 2 bên cùng xem. */
@Service
public class LichSuSuCoService {

    private final LichSuSuCoRepository lichSuSuCoRepository;

    public LichSuSuCoService(LichSuSuCoRepository lichSuSuCoRepository) {
        this.lichSuSuCoRepository = lichSuSuCoRepository;
    }

    public List<LichSuSuCo> layTheoSuCo(String maSuCo) {
        return lichSuSuCoRepository.findBySuCo_MaSuCoOrderByThoiDiem(maSuCo);
    }

    @Transactional
    public void ghiNhan(SuCo suCo, TrangThaiSuCo trangThaiCu, TrangThaiSuCo trangThaiMoi, VaiTro vaiTroThucHien, String ghiChu) {
        LichSuSuCo lichSu = new LichSuSuCo();
        lichSu.setMaLichSu("LSSC" + String.format("%06d", lichSuSuCoRepository.count() + 1));
        lichSu.setSuCo(suCo);
        lichSu.setTrangThaiCu(trangThaiCu);
        lichSu.setTrangThaiMoi(trangThaiMoi);
        lichSu.setVaiTroThucHien(vaiTroThucHien);
        lichSu.setGhiChu(ghiChu);
        lichSuSuCoRepository.save(lichSu);
    }
}
