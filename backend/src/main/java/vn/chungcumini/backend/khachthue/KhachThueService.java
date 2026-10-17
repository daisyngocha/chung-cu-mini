package vn.chungcumini.backend.khachthue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.KhachThue;
import vn.chungcumini.backend.hopdong.HopDongService;
import vn.chungcumini.backend.repository.KhachThueRepository;

@Service
public class KhachThueService {

    private final KhachThueRepository khachThueRepository;
    private final HopDongService hopDongService;

    public KhachThueService(KhachThueRepository khachThueRepository, HopDongService hopDongService) {
        this.khachThueRepository = khachThueRepository;
        this.hopDongService = hopDongService;
    }

    /** KhachThue.timTheoSDT. */
    public Optional<KhachThue> timTheoSDT(String sdt) {
        return khachThueRepository.findBySoDienThoai(sdt);
    }

    /**
     * KhachThue.capNhatHoSo (BR-CT-06): tạo hồ sơ mới nếu SĐT chưa có, hoặc cập nhật hồ sơ cũ
     * (khách thuê lại). SĐT là khóa gọi lại hồ sơ, không đổi được qua phương thức này.
     */
    @Transactional
    public KhachThue capNhatHoSo(
            String soDienThoai, String hoTen, LocalDate ngaySinh, String soCCCD, String anhCCCD, String queQuan, String noiLamViec) {
        if (soDienThoai == null || !soDienThoai.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ.");
        }
        if (soCCCD == null || !soCCCD.matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Số CCCD phải gồm 12 chữ số.");
        }
        if (ngaySinh == null || !ngaySinh.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
        }

        KhachThue khach = khachThueRepository.findBySoDienThoai(soDienThoai).orElseGet(() -> {
            KhachThue moi = new KhachThue();
            moi.setMaKhach("KH" + String.format("%04d", khachThueRepository.count() + 1));
            moi.setSoDienThoai(soDienThoai);
            return moi;
        });
        khach.setHoTen(hoTen);
        khach.setNgaySinh(ngaySinh);
        khach.setSoCCCD(soCCCD);
        khach.setAnhCCCD(anhCCCD);
        khach.setQueQuan(queQuan);
        khach.setNoiLamViec(noiLamViec);
        return khachThueRepository.save(khach);
    }

    /** KhachThue.layLichSuThue: sắp xếp ngayBatDau giảm dần. */
    public List<HopDong> layLichSuThue(String maKhach) {
        return hopDongService.layLichSuThueCuaKhach(maKhach);
    }
}
