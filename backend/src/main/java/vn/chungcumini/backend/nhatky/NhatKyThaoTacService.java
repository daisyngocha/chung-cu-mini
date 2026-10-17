package vn.chungcumini.backend.nhatky;

import java.util.List;
import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.NhatKyThaoTac;
import vn.chungcumini.backend.repository.NhatKyThaoTacRepository;

/** Mục 11: nhật ký thao tác chỉ thêm, không sửa/xóa. */
@Service
public class NhatKyThaoTacService {

    private final NhatKyThaoTacRepository nhatKyThaoTacRepository;

    public NhatKyThaoTacService(NhatKyThaoTacRepository nhatKyThaoTacRepository) {
        this.nhatKyThaoTacRepository = nhatKyThaoTacRepository;
    }

    /** CN-09.05 (UC01.09): tra cứu nhật ký, lọc theo đối tượng nếu có. */
    public List<NhatKyThaoTac> layDanhSach(String doiTuong) {
        return doiTuong != null && !doiTuong.isBlank()
                ? nhatKyThaoTacRepository.findByDoiTuongOrderByThoiDiemDesc(doiTuong)
                : nhatKyThaoTacRepository.findAllByOrderByThoiDiemDesc();
    }

    public void ghiNhan(String maTaiKhoan, String doiTuong, String maDoiTuong, String hanhDong, String noiDung) {
        NhatKyThaoTac nk = new NhatKyThaoTac();
        nk.setMaNhatKy("NK" + String.format("%06d", nhatKyThaoTacRepository.count() + 1));
        nk.setMaTaiKhoan(maTaiKhoan);
        nk.setDoiTuong(doiTuong);
        nk.setMaDoiTuong(maDoiTuong);
        nk.setHanhDong(hanhDong);
        nk.setNoiDung(noiDung);
        nhatKyThaoTacRepository.save(nk);
    }
}
