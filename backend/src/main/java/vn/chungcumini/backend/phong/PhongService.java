package vn.chungcumini.backend.phong;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.Phong;
import vn.chungcumini.backend.domain.TrangThaiPhong;
import vn.chungcumini.backend.repository.PhongRepository;
import vn.chungcumini.backend.repository.ToaNhaRepository;

@Service
public class PhongService {

    private final PhongRepository phongRepository;
    private final ToaNhaRepository toaNhaRepository;

    public PhongService(PhongRepository phongRepository, ToaNhaRepository toaNhaRepository) {
        this.phongRepository = phongRepository;
        this.toaNhaRepository = toaNhaRepository;
    }

    public List<PhongResponse> layDanhSach() {
        return phongRepository.findAll().stream()
                .sorted((a, b) -> {
                    int sTang = Integer.compare(a.getTang(), b.getTang());
                    return sTang != 0 ? sTang : a.getMaPhong().compareTo(b.getMaPhong());
                })
                .map(PhongResponse::tu)
                .toList();
    }

    @Transactional
    public PhongResponse taoPhong(TaoPhongRequest req) {
        if (phongRepository.existsById(req.maPhong())) {
            throw new IllegalArgumentException("Mã phòng \"" + req.maPhong() + "\" đã tồn tại.");
        }

        Phong phong = new Phong();
        phong.setMaPhong(req.maPhong());
        phong.setToaNha(toaNhaRepository.findById("TOA01").orElseThrow());
        phong.setTang(req.tang());
        phong.setDienTich(req.dienTich());
        phong.setSoNguoiToiDa(req.soNguoiToiDa());
        phong.setGiaNiemYet(req.giaNiemYet());
        phong.setCoCongToNuoc(req.coCongToNuoc());

        try {
            return PhongResponse.tu(phongRepository.save(phong));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Mã phòng \"" + req.maPhong() + "\" đã tồn tại.");
        }
    }

    /** BR-DM-04: chủ nhà chỉ chuyển Trống <-> Ngừng khai thác. */
    @Transactional
    public PhongResponse chuyenTrangThai(String maPhong) {
        Phong phong = phongRepository.findById(maPhong)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng \"" + maPhong + "\"."));

        if (phong.getTrangThai() == TrangThaiPhong.TRONG) {
            phong.setTrangThai(TrangThaiPhong.NGUNG_KHAI_THAC);
        } else if (phong.getTrangThai() == TrangThaiPhong.NGUNG_KHAI_THAC) {
            phong.setTrangThai(TrangThaiPhong.TRONG);
        }

        return PhongResponse.tu(phongRepository.save(phong));
    }
}
