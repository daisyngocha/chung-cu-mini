package vn.chungcumini.backend.thongbao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiSuKien;
import vn.chungcumini.backend.domain.NguonTB;
import vn.chungcumini.backend.domain.PhamViTB;
import vn.chungcumini.backend.domain.ThongBao;
import vn.chungcumini.backend.domain.ThongBaoNguoiNhan;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.VaiTro;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.TaiKhoanRepository;
import vn.chungcumini.backend.repository.ThongBaoNguoiNhanRepository;
import vn.chungcumini.backend.repository.ThongBaoRepository;

/**
 * ThongBao — thông báo trong hệ thống (chuông), không gửi SMS/Zalo (UC08.01–UC08.03). Danh sách sự
 * kiện: Mô tả YC mục 9. Các module gọi guiDen() khi tạo thông báo tự động theo sự kiện đã có trong
 * main tại thời điểm viết GĐ7 (ThongBaoTien.phatHanh/danhDauQuaHan, HopDong.baoTraPhong); các sự
 * kiện của module chưa gộp (BaoThanhToan, QuyetToan, SuCo — GĐ4/5/6, nhánh riêng) sẽ được gọi guiDen()
 * khi các nhánh đó gộp vào main và cập nhật lại.
 */
@Service
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;
    private final ThongBaoNguoiNhanRepository thongBaoNguoiNhanRepository;
    private final HopDongRepository hopDongRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public ThongBaoService(
            ThongBaoRepository thongBaoRepository,
            ThongBaoNguoiNhanRepository thongBaoNguoiNhanRepository,
            HopDongRepository hopDongRepository,
            TaiKhoanRepository taiKhoanRepository) {
        this.thongBaoRepository = thongBaoRepository;
        this.thongBaoNguoiNhanRepository = thongBaoNguoiNhanRepository;
        this.hopDongRepository = hopDongRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    public List<ThongBaoNguoiNhan> danhSachCuaToi(String maTaiKhoan) {
        return thongBaoNguoiNhanRepository.timTheoTaiKhoan(maTaiKhoan);
    }

    /** ThongBaoNguoiNhan.demChuaDoc. */
    public long demChuaDoc(String maTaiKhoan) {
        return thongBaoNguoiNhanRepository.countByTaiKhoanAndDaDocFalse(maTaiKhoan);
    }

    /** ThongBaoNguoiNhan.danhDauDaDoc. */
    @Transactional
    public void danhDauDaDoc(String maThongBao, String maTaiKhoan) {
        ThongBaoNguoiNhan nguoiNhan = thongBaoNguoiNhanRepository.findByThongBao_MaThongBaoAndTaiKhoan(maThongBao, maTaiKhoan)
                .orElseThrow(() -> new IllegalArgumentException("Thông báo \"" + maThongBao + "\" không gửi cho tài khoản này."));
        if (!nguoiNhan.isDaDoc()) {
            nguoiNhan.setDaDoc(true);
            nguoiNhan.setThoiDiemDoc(LocalDateTime.now());
            thongBaoNguoiNhanRepository.save(nguoiNhan);
        }
    }

    /** ThongBao.guiDen: tạo 1 thông báo cá nhân (TU_DONG theo sự kiện), gửi tới 1 hoặc nhiều tài khoản. */
    @Transactional
    public ThongBao guiDen(LoaiSuKien loaiSuKien, String tieuDe, String noiDung, String doiTuongLienQuan, List<String> dsTaiKhoanNhan) {
        List<String> nguoiNhan = dsTaiKhoanNhan.stream().filter(Objects::nonNull).distinct().toList();
        if (nguoiNhan.isEmpty()) {
            return null;
        }
        ThongBao thongBao = taoThongBao(loaiSuKien, tieuDe, noiDung, NguonTB.TU_DONG, PhamViTB.CA_NHAN, doiTuongLienQuan);
        ganNguoiNhan(thongBao, nguoiNhan);
        return thongBao;
    }

    /** ThongBao.guiDen cho chủ nhà — hệ thống chỉ có 1 tài khoản chủ nhà. */
    @Transactional
    public ThongBao guiChoChuNha(LoaiSuKien loaiSuKien, String tieuDe, String noiDung, String doiTuongLienQuan) {
        return taiKhoanRepository.findFirstByVaiTro(VaiTro.CHU_NHA)
                .map(cn -> guiDen(loaiSuKien, tieuDe, noiDung, doiTuongLienQuan, List.of(cn.getMaTaiKhoan())))
                .orElse(null);
    }

    /** ThongBao.guiToanBoKhach (TB.GUI_CHUNG): chủ nhà gửi thông báo chung cho mọi khách đang thuê. */
    @Transactional
    public ThongBao guiToanBoKhach(String tieuDe, String noiDung) {
        List<String> dsKhach = hopDongRepository.findByTrangThaiWithPhong(TrangThaiHD.HIEU_LUC).stream()
                .map(HopDong::getMaTaiKhoan)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (dsKhach.isEmpty()) {
            throw new IllegalArgumentException("Hiện không có khách đang thuê để gửi thông báo.");
        }
        ThongBao thongBao = taoThongBao(LoaiSuKien.THONG_BAO_CHUNG, tieuDe, noiDung, NguonTB.CHU_NHA, PhamViTB.TOAN_TOA, null);
        ganNguoiNhan(thongBao, dsKhach);
        return thongBao;
    }

    private ThongBao taoThongBao(LoaiSuKien loaiSuKien, String tieuDe, String noiDung, NguonTB nguon, PhamViTB phamVi, String doiTuongLienQuan) {
        if (tieuDe == null || tieuDe.isBlank()) {
            throw new IllegalArgumentException("Tiêu đề không được rỗng.");
        }
        ThongBao thongBao = new ThongBao();
        thongBao.setMaThongBao("TB" + String.format("%06d", thongBaoRepository.count() + 1));
        thongBao.setLoaiSuKien(loaiSuKien);
        thongBao.setTieuDe(tieuDe);
        thongBao.setNoiDung(noiDung);
        thongBao.setNguon(nguon);
        thongBao.setPhamVi(phamVi);
        thongBao.setDoiTuongLienQuan(doiTuongLienQuan);
        return thongBaoRepository.save(thongBao);
    }

    private void ganNguoiNhan(ThongBao thongBao, List<String> dsTaiKhoan) {
        for (String maTaiKhoan : dsTaiKhoan) {
            ThongBaoNguoiNhan nguoiNhan = new ThongBaoNguoiNhan();
            nguoiNhan.setThongBao(thongBao);
            nguoiNhan.setTaiKhoan(maTaiKhoan);
            thongBaoNguoiNhanRepository.save(nguoiNhan);
        }
    }
}
