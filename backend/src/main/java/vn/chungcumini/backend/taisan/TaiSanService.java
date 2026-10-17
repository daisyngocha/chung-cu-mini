package vn.chungcumini.backend.taisan;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.Phong;
import vn.chungcumini.backend.domain.TaiSan;
import vn.chungcumini.backend.repository.PhongRepository;
import vn.chungcumini.backend.repository.TaiSanRepository;

@Service
public class TaiSanService {

    private final TaiSanRepository taiSanRepository;
    private final PhongRepository phongRepository;

    public TaiSanService(TaiSanRepository taiSanRepository, PhongRepository phongRepository) {
        this.taiSanRepository = taiSanRepository;
        this.phongRepository = phongRepository;
    }

    public List<TaiSanResponse> layDanhSach(String maPhong) {
        return taiSanRepository.findByPhong_MaPhong(maPhong).stream().map(TaiSanResponse::tu).toList();
    }

    @Transactional
    public TaiSanResponse them(String maPhong, ThemTaiSanRequest req) {
        Phong phong = phongRepository.findById(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng \"" + maPhong + "\"."));

        TaiSan taiSan = new TaiSan();
        taiSan.setMaTaiSan("TS" + String.format("%04d", taiSanRepository.count() + 1));
        taiSan.setPhong(phong);
        taiSan.setTenTaiSan(req.tenTaiSan());
        taiSan.setSoLuong(req.soLuong());
        if (req.tinhTrang() != null && !req.tinhTrang().isBlank()) {
            taiSan.setTinhTrang(req.tinhTrang());
        }
        taiSan.setGhiChu(req.ghiChu());

        return TaiSanResponse.tu(taiSanRepository.save(taiSan));
    }

    /** TaiSan.capNhatTinhTrang: cập nhật tình trạng hiện tại của tài sản (thường gọi sau bàn giao nhận/trả phòng). */
    @Transactional
    public TaiSanResponse capNhatTinhTrang(String maTaiSan, String tinhTrangMoi) {
        TaiSan taiSan = taiSanRepository.findById(maTaiSan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài sản \"" + maTaiSan + "\"."));
        taiSan.setTinhTrang(tinhTrangMoi);
        return TaiSanResponse.tu(taiSanRepository.save(taiSan));
    }
}
