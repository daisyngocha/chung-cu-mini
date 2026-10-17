package vn.chungcumini.backend.nguoio;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.LoaiThayDoi;
import vn.chungcumini.backend.domain.NguoiO;
import vn.chungcumini.backend.hopdong.LichSuHopDongService;
import vn.chungcumini.backend.repository.NguoiORepository;

@Service
public class NguoiOService {

    private final NguoiORepository nguoiORepository;
    private final LichSuHopDongService lichSuHopDongService;

    public NguoiOService(NguoiORepository nguoiORepository, LichSuHopDongService lichSuHopDongService) {
        this.nguoiORepository = nguoiORepository;
        this.lichSuHopDongService = lichSuHopDongService;
    }

    public java.util.List<NguoiO> layTheoHopDong(String maHopDong) {
        return nguoiORepository.findByHopDong_MaHopDong(maHopDong);
    }

    /** NguoiO.capNhatThongTin. */
    @Transactional
    public NguoiO capNhatThongTin(String maNguoiO, String hoTen, String soCCCD, String soDienThoai) {
        NguoiO nguoiO = timHoacNem(maNguoiO);
        nguoiO.setHoTen(hoTen);
        nguoiO.setSoCCCD(soCCCD);
        nguoiO.setSoDienThoai(soDienThoai);
        return nguoiORepository.save(nguoiO);
    }

    /** NguoiO.roiDi: ghi nhận người ở cùng rời đi; ghi LichSuHopDong BOT_NGUOI_O. */
    @Transactional
    public NguoiO roiDi(String maNguoiO, LocalDate ngayRa) {
        NguoiO nguoiO = timHoacNem(maNguoiO);
        if (ngayRa.isBefore(nguoiO.getNgayVao())) {
            throw new IllegalArgumentException("Ngày rời đi phải sau ngày vào ở.");
        }
        nguoiO.setNgayRa(ngayRa);
        nguoiO = nguoiORepository.save(nguoiO);

        lichSuHopDongService.ghiNhan(
                nguoiO.getHopDong(), LoaiThayDoi.BOT_NGUOI_O, "nguoiO", nguoiO.getHoTen(), null, ngayRa, null);
        return nguoiO;
    }

    private NguoiO timHoacNem(String maNguoiO) {
        return nguoiORepository.findById(maNguoiO)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người ở \"" + maNguoiO + "\"."));
    }
}
