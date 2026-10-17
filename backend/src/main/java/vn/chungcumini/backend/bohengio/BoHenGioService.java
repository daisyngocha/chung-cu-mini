package vn.chungcumini.backend.bohengio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiSuKien;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.domain.VaiTro;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.TaiKhoanRepository;
import vn.chungcumini.backend.repository.ThongBaoRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;
import vn.chungcumini.backend.thongbao.ThongBaoService;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;

/**
 * BoHenGio — job chạy 1 lần/ngày (07:00). (1) "Ghi chỉ số" vào việc cần làm: không cần xử lý riêng ở
 * đây — DichVuViecCanLam.layDanhSach() tự tính theo ngày chốt mỗi lần gọi (thời gian thực), không
 * phải trạng thái cần ghi lại.
 */
@Service
public class BoHenGioService {

    private final ThongBaoTienRepository thongBaoTienRepository;
    private final ThongBaoRepository thongBaoRepository;
    private final HopDongRepository hopDongRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final ThongBaoTienService thongBaoTienService;
    private final ThongBaoService thongBaoService;

    public BoHenGioService(
            ThongBaoTienRepository thongBaoTienRepository,
            ThongBaoRepository thongBaoRepository,
            HopDongRepository hopDongRepository,
            TaiKhoanRepository taiKhoanRepository,
            ThongBaoTienService thongBaoTienService,
            ThongBaoService thongBaoService) {
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.thongBaoRepository = thongBaoRepository;
        this.hopDongRepository = hopDongRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.thongBaoTienService = thongBaoTienService;
        this.thongBaoService = thongBaoService;
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void chayTuDong() {
        chayHangNgay(LocalDate.now());
    }

    /** BoHenGio.chayHangNgay: idempotent — không tạo trùng thông báo/việc cho cùng sự kiện, cùng ngày. */
    @Transactional
    public KetQuaChayHangNgay chayHangNgay(LocalDate ngay) {
        List<String> nhacHan = new ArrayList<>();
        List<String> quaHan = new ArrayList<>();
        List<String> hopDongSapHetHan = new ArrayList<>();

        for (ThongBaoTien tb : thongBaoTienRepository.findByTrangThaiIn(List.of(TrangThaiTBT.DA_PHAT_HANH, TrangThaiTBT.DA_THU_MOT_PHAN))) {
            if (tb.getHanThanhToan().isEqual(ngay)) {
                if (guiNhacHanNeuChuaGui(tb)) {
                    nhacHan.add(tb.getMaThongBaoTien());
                }
            } else if (ngay.isEqual(tb.getHanThanhToan().plusDays(3))) {
                try {
                    thongBaoTienService.danhDauQuaHan(tb.getMaThongBaoTien(), ngay);
                    quaHan.add(tb.getMaThongBaoTien());
                } catch (IllegalArgumentException khongDungTrangThai) {
                    // Đã quá hạn từ trước (job chạy lại cùng ngày) hoặc không còn ở trạng thái phù hợp — bỏ qua.
                }
            }
        }

        for (HopDong hopDong : hopDongRepository.findByTrangThaiWithPhong(TrangThaiHD.HIEU_LUC)) {
            if (hopDong.getNgayHetHan().isEqual(ngay.plusDays(30)) && guiNhacHetHanNeuChuaGui(hopDong)) {
                hopDongSapHetHan.add(hopDong.getMaHopDong());
            }
        }

        return new KetQuaChayHangNgay(nhacHan, quaHan, hopDongSapHetHan);
    }

    private boolean guiNhacHanNeuChuaGui(ThongBaoTien tb) {
        if (tb.getHopDong().getMaTaiKhoan() == null || daGuiHomNay(LoaiSuKien.NHAC_HAN_TT, "ThongBaoTien:" + tb.getMaThongBaoTien())) {
            return false;
        }
        thongBaoService.guiDen(
                LoaiSuKien.NHAC_HAN_TT, "Nhắc hạn thanh toán",
                "Thông báo tiền kỳ " + tb.getKyThang() + " của phòng " + tb.getHopDong().getPhong().getMaPhong() + " đến hạn thanh toán hôm nay.",
                "ThongBaoTien:" + tb.getMaThongBaoTien(), List.of(tb.getHopDong().getMaTaiKhoan()));
        return true;
    }

    private boolean guiNhacHetHanNeuChuaGui(HopDong hopDong) {
        if (daGuiHomNay(LoaiSuKien.HD_SAP_HET_HAN, "HopDong:" + hopDong.getMaHopDong())) {
            return false;
        }
        var chuNha = taiKhoanRepository.findFirstByVaiTro(VaiTro.CHU_NHA);
        List<String> nguoiNhan = new ArrayList<>();
        chuNha.ifPresent(cn -> nguoiNhan.add(cn.getMaTaiKhoan()));
        if (hopDong.getMaTaiKhoan() != null) {
            nguoiNhan.add(hopDong.getMaTaiKhoan());
        }
        thongBaoService.guiDen(
                LoaiSuKien.HD_SAP_HET_HAN, "Hợp đồng sắp hết hạn",
                "Hợp đồng phòng " + hopDong.getPhong().getMaPhong() + " hết hạn vào " + hopDong.getNgayHetHan() + " (còn 30 ngày).",
                "HopDong:" + hopDong.getMaHopDong(), nguoiNhan);
        return true;
    }

    /**
     * "Cùng ngày" ở đây là ngày thực chạy job (ThongBao.ngayTao luôn là LocalDateTime.now() thực tế,
     * không phải tham số ngay — tham số ngay có thể là một ngày giả lập khi chạy tay/bù chạy), nên so
     * sánh với ngày hệ thống hiện tại, không phải tham số ngay, để việc chạy bù cho ngày khác hôm nay
     * vẫn được chống trùng đúng theo lần chạy thực tế.
     */
    private boolean daGuiHomNay(LoaiSuKien loaiSuKien, String doiTuongLienQuan) {
        LocalDate homNayThucTe = LocalDate.now();
        return thongBaoRepository.existsByLoaiSuKienAndDoiTuongLienQuanAndNgayTaoBetween(
                loaiSuKien, doiTuongLienQuan, homNayThucTe.atStartOfDay(), homNayThucTe.plusDays(1).atStartOfDay());
    }
}
