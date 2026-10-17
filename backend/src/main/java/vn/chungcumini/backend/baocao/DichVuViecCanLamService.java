package vn.chungcumini.backend.baocao;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import vn.chungcumini.backend.domain.HopDong;
import vn.chungcumini.backend.domain.LoaiBanGiao;
import vn.chungcumini.backend.domain.LoaiCongTo;
import vn.chungcumini.backend.domain.LoaiGhi;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiHD;
import vn.chungcumini.backend.domain.TrangThaiSuCo;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.repository.BanGiaoTaiSanRepository;
import vn.chungcumini.backend.repository.BaoThanhToanRepository;
import vn.chungcumini.backend.repository.ChiSoRepository;
import vn.chungcumini.backend.repository.HopDongRepository;
import vn.chungcumini.backend.repository.SuCoRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;
import vn.chungcumini.backend.repository.ToaNhaRepository;

/** DichVuViecCanLam — màn hình chính của chủ nhà (CN-01.01, UC08.04). */
@Service
public class DichVuViecCanLamService {

    private final ThongBaoTienRepository thongBaoTienRepository;
    private final HopDongRepository hopDongRepository;
    private final BanGiaoTaiSanRepository banGiaoTaiSanRepository;
    private final ChiSoRepository chiSoRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final BaoThanhToanRepository baoThanhToanRepository;
    private final SuCoRepository suCoRepository;

    public DichVuViecCanLamService(
            ThongBaoTienRepository thongBaoTienRepository,
            HopDongRepository hopDongRepository,
            BanGiaoTaiSanRepository banGiaoTaiSanRepository,
            ChiSoRepository chiSoRepository,
            ToaNhaRepository toaNhaRepository,
            BaoThanhToanRepository baoThanhToanRepository,
            SuCoRepository suCoRepository) {
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.hopDongRepository = hopDongRepository;
        this.banGiaoTaiSanRepository = banGiaoTaiSanRepository;
        this.chiSoRepository = chiSoRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.baoThanhToanRepository = baoThanhToanRepository;
        this.suCoRepository = suCoRepository;
    }

    public List<ViecCanLam> layDanhSach() {
        List<ViecCanLam> ketQua = new ArrayList<>();
        LocalDate homNay = LocalDate.now();

        for (var baoTT : baoThanhToanRepository.timHangChoXacNhan()) {
            ketQua.add(new ViecCanLam(
                    LoaiViecCanLam.BAO_CK_CHO_XAC_NHAN,
                    "Phòng " + baoTT.getThongBaoTien().getHopDong().getPhong().getMaPhong()
                            + " báo đã chuyển khoản, chờ xác nhận.",
                    "BaoThanhToan:" + baoTT.getMaBaoTT()));
        }

        for (var suCo : suCoRepository.findByTrangThaiIn(List.of(TrangThaiSuCo.MOI, TrangThaiSuCo.MO_LAI))) {
            ketQua.add(new ViecCanLam(
                    LoaiViecCanLam.SU_CO_MOI_MO_LAI,
                    "Phòng " + suCo.getPhong().getMaPhong() + " có sự cố " + suCo.getLoaiSuCo() + " chưa xử lý.",
                    "SuCo:" + suCo.getMaSuCo()));
        }

        for (ThongBaoTien tb : thongBaoTienRepository.findByTrangThaiIn(List.of(TrangThaiTBT.QUA_HAN))) {
            ketQua.add(new ViecCanLam(
                    LoaiViecCanLam.THONG_BAO_QUA_HAN,
                    "Phòng " + tb.getHopDong().getPhong().getMaPhong() + " quá hạn thanh toán kỳ " + tb.getKyThang() + ".",
                    "ThongBaoTien:" + tb.getMaThongBaoTien()));
        }

        List<HopDong> dangHieuLuc = hopDongRepository.findByTrangThaiWithPhong(TrangThaiHD.HIEU_LUC);
        int ngayChot = toaNhaRepository.findById("TOA01").map(t -> t.getNgayChotChiSo()).orElse(31);
        String kyHienTai = YearMonth.now().toString();
        boolean toiNgayChot = homNay.getDayOfMonth() >= Math.min(ngayChot, homNay.lengthOfMonth());

        for (HopDong hopDong : dangHieuLuc) {
            if (!hopDong.getNgayHetHan().isAfter(homNay.plusDays(30))) {
                ketQua.add(new ViecCanLam(
                        LoaiViecCanLam.HOP_DONG_SAP_HET_HAN,
                        "Hợp đồng phòng " + hopDong.getPhong().getMaPhong() + " hết hạn ngày " + hopDong.getNgayHetHan() + ".",
                        "HopDong:" + hopDong.getMaHopDong()));
            }

            if (hopDong.getNgayBaoTra() != null && hopDong.getNgayTraThucTe() == null
                    && banGiaoTaiSanRepository.findByHopDong_MaHopDongAndLoaiBanGiao(hopDong.getMaHopDong(), LoaiBanGiao.TRA_PHONG).isEmpty()) {
                ketQua.add(new ViecCanLam(
                        LoaiViecCanLam.YEU_CAU_TRA_PHONG_MOI,
                        "Phòng " + hopDong.getPhong().getMaPhong() + " báo trả phòng, chưa kiểm tra phòng.",
                        "HopDong:" + hopDong.getMaHopDong()));
            }

            if (toiNgayChot && thieuChiSoKyNay(hopDong, kyHienTai)) {
                ketQua.add(new ViecCanLam(
                        LoaiViecCanLam.PHONG_THIEU_CHI_SO,
                        "Phòng " + hopDong.getPhong().getMaPhong() + " còn thiếu chỉ số kỳ " + kyHienTai + ".",
                        "HopDong:" + hopDong.getMaHopDong()));
            }
        }
        return ketQua;
    }

    private boolean thieuChiSoKyNay(HopDong hopDong, String kyThang) {
        boolean thieuDien = chiSoRepository
                .findByHopDong_MaHopDongAndLoaiCongToAndKyThangAndLoaiGhi(hopDong.getMaHopDong(), LoaiCongTo.DIEN, kyThang, LoaiGhi.HANG_THANG)
                .isEmpty();
        boolean thieuNuoc = hopDong.getPhong().isCoCongToNuoc()
                && chiSoRepository
                        .findByHopDong_MaHopDongAndLoaiCongToAndKyThangAndLoaiGhi(hopDong.getMaHopDong(), LoaiCongTo.NUOC, kyThang, LoaiGhi.HANG_THANG)
                        .isEmpty();
        return thieuDien || thieuNuoc;
    }
}
