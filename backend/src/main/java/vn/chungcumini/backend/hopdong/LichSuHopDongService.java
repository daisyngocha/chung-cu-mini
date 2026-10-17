package vn.chungcumini.backend.hopdong;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LichSuHopDong;
import vn.chungcumini.backend.domain.LoaiThayDoi;
import vn.chungcumini.backend.repository.LichSuHopDongRepository;

/** Mục 11: lịch sử hợp đồng chỉ thêm, không sửa/xóa. */
@Service
public class LichSuHopDongService {

    private final LichSuHopDongRepository lichSuHopDongRepository;

    public LichSuHopDongService(LichSuHopDongRepository lichSuHopDongRepository) {
        this.lichSuHopDongRepository = lichSuHopDongRepository;
    }

    /** LichSuHopDong.ghiNhan. */
    public void ghiNhan(
            HopDong hopDong,
            LoaiThayDoi loaiThayDoi,
            String truong,
            String cu,
            String moi,
            LocalDate ngayApDung,
            String anhPhuLuc) {
        LichSuHopDong lsh = new LichSuHopDong();
        lsh.setMaLichSu("LSHD" + String.format("%06d", lichSuHopDongRepository.count() + 1));
        lsh.setHopDong(hopDong);
        lsh.setLoaiThayDoi(loaiThayDoi);
        lsh.setTruongThayDoi(truong);
        lsh.setGiaTriCu(cu);
        lsh.setGiaTriMoi(moi);
        lsh.setNgayApDung(ngayApDung);
        lsh.setAnhPhuLuc(anhPhuLuc);
        lichSuHopDongRepository.save(lsh);
    }
}
