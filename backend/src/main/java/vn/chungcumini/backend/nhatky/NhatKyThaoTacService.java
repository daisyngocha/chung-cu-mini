package vn.chungcumini.backend.nhatky;

import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.NhatKyThaoTac;
import vn.chungcumini.backend.repository.NhatKyThaoTacRepository;

/** Mục 11: nhật ký thao tác chỉ thêm, không sửa/xóa. Tra cứu (UC01.09, CN-09.05) thuộc giai đoạn kế tiếp. */
@Service
public class NhatKyThaoTacService {

    private final NhatKyThaoTacRepository nhatKyThaoTacRepository;

    public NhatKyThaoTacService(NhatKyThaoTacRepository nhatKyThaoTacRepository) {
        this.nhatKyThaoTacRepository = nhatKyThaoTacRepository;
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
