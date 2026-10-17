package vn.chungcumini.backend.baothanhtoan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.BaoThanhToan;
import vn.chungcumini.backend.domain.HinhThucTT;
import vn.chungcumini.backend.domain.PhieuThu;
import vn.chungcumini.backend.domain.ThongBaoTien;
import vn.chungcumini.backend.domain.TrangThaiBaoTT;
import vn.chungcumini.backend.domain.TrangThaiTBT;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.phieuthu.PhieuThuIdGenerator;
import vn.chungcumini.backend.repository.BaoThanhToanRepository;
import vn.chungcumini.backend.repository.PhieuThuRepository;
import vn.chungcumini.backend.repository.ThongBaoTienRepository;
import vn.chungcumini.backend.thongbaotien.ThongBaoTienService;

/** Gửi ThongBao (KHACH_BAO_CK, XAC_NHAN_THU, TU_CHOI_BAO_CK) chưa cài — module Thông báo chưa xây. */
@Service
public class BaoThanhToanService {

    private final BaoThanhToanRepository baoThanhToanRepository;
    private final ThongBaoTienRepository thongBaoTienRepository;
    private final ThongBaoTienService thongBaoTienService;
    private final PhieuThuRepository phieuThuRepository;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public BaoThanhToanService(
            BaoThanhToanRepository baoThanhToanRepository,
            ThongBaoTienRepository thongBaoTienRepository,
            ThongBaoTienService thongBaoTienService,
            PhieuThuRepository phieuThuRepository,
            NhatKyThaoTacService nhatKyThaoTacService) {
        this.baoThanhToanRepository = baoThanhToanRepository;
        this.thongBaoTienRepository = thongBaoTienRepository;
        this.thongBaoTienService = thongBaoTienService;
        this.phieuThuRepository = phieuThuRepository;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    public BaoThanhToan timHoacNem(String maBaoTT) {
        return baoThanhToanRepository.timChiTiet(maBaoTT)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy báo thanh toán \"" + maBaoTT + "\"."));
    }

    public List<BaoThanhToan> layTheoTaiKhoanKhach(String maTaiKhoan) {
        return baoThanhToanRepository.findByThongBaoTien_HopDong_MaTaiKhoanOrderByThoiDiemBaoDesc(maTaiKhoan);
    }

    /** BaoThanhToan.guiBao: khách bấm "Tôi đã chuyển khoản" — chỉ cho TBT của phòng mình. */
    @Transactional
    public BaoThanhToan guiBao(String maThongBaoTien, String maTaiKhoanKhach, String anhBienLai) {
        ThongBaoTien tb = thongBaoTienService.timHoacNem(maThongBaoTien);
        if (!maTaiKhoanKhach.equals(tb.getHopDong().getMaTaiKhoan())) {
            throw new IllegalArgumentException("Thông báo tiền không thuộc phòng của bạn.");
        }
        if (tb.getTrangThai() != TrangThaiTBT.DA_PHAT_HANH
                && tb.getTrangThai() != TrangThaiTBT.DA_THU_MOT_PHAN
                && tb.getTrangThai() != TrangThaiTBT.QUA_HAN) {
            throw new IllegalArgumentException("Thông báo không thuộc nhóm chưa thu đủ.");
        }
        if (baoThanhToanRepository.existsByThongBaoTien_MaThongBaoTienAndTrangThai(maThongBaoTien, TrangThaiBaoTT.CHO_XAC_NHAN)) {
            throw new IllegalArgumentException("Đã có báo chuyển khoản khác đang chờ xác nhận.");
        }

        BaoThanhToan baoTT = new BaoThanhToan();
        baoTT.setMaBaoTT("BTT" + String.format("%06d", baoThanhToanRepository.count() + 1));
        baoTT.setThongBaoTien(tb);
        baoTT.setThoiDiemBao(LocalDateTime.now());
        baoTT.setAnhBienLai(anhBienLai);
        baoTT = baoThanhToanRepository.save(baoTT);

        tb.setTrangThai(TrangThaiTBT.CHO_XAC_NHAN);
        thongBaoTienRepository.save(tb);
        return baoTT;
    }

    /** BaoThanhToan.xacNhan: chủ nhà xác nhận đã nhận tiền sau khi đối chiếu ngân hàng. */
    @Transactional
    public PhieuThu xacNhan(String maBaoTT, BigDecimal soTienThucNhan, String maTaiKhoanThucHien) {
        BaoThanhToan baoTT = timHoacNem(maBaoTT);
        if (baoTT.getTrangThai() != TrangThaiBaoTT.CHO_XAC_NHAN) {
            throw new IllegalArgumentException("Báo chuyển khoản không ở trạng thái chờ xác nhận.");
        }
        if (soTienThucNhan == null || soTienThucNhan.signum() <= 0) {
            throw new IllegalArgumentException("Số tiền thực nhận phải lớn hơn 0.");
        }

        baoTT.setTrangThai(TrangThaiBaoTT.DA_XAC_NHAN);
        baoThanhToanRepository.save(baoTT);

        ThongBaoTien tb = baoTT.getThongBaoTien();
        LocalDate homNay = LocalDate.now();
        PhieuThu phieuThu = new PhieuThu();
        phieuThu.setSoPhieuThu(PhieuThuIdGenerator.sinh(phieuThuRepository, homNay));
        phieuThu.setThongBaoTien(tb);
        phieuThu.setBaoThanhToan(baoTT);
        phieuThu.setNgayThu(homNay);
        phieuThu.setNguoiNop(tb.getHopDong().getKhachThue().getHoTen());
        phieuThu.setSoTien(soTienThucNhan);
        phieuThu.setHinhThuc(HinhThucTT.CHUYEN_KHOAN);
        phieuThu = phieuThuRepository.save(phieuThu);

        thongBaoTienService.capNhatTrangThaiThu(tb.getMaThongBaoTien());

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien, "PhieuThu", phieuThu.getSoPhieuThu(), "XAC_NHAN",
                "Xác nhận báo chuyển khoản \"" + maBaoTT + "\", số tiền " + soTienThucNhan + ".");
        return phieuThu;
    }

    /** BaoThanhToan.tuChoi: chủ nhà chưa thấy tiền về. */
    @Transactional
    public void tuChoi(String maBaoTT, String lyDo, String maTaiKhoanThucHien) {
        BaoThanhToan baoTT = timHoacNem(maBaoTT);
        if (baoTT.getTrangThai() != TrangThaiBaoTT.CHO_XAC_NHAN) {
            throw new IllegalArgumentException("Báo chuyển khoản không ở trạng thái chờ xác nhận.");
        }
        if (lyDo == null || lyDo.isBlank()) {
            throw new IllegalArgumentException("Bắt buộc ghi lý do từ chối.");
        }

        baoTT.setTrangThai(TrangThaiBaoTT.TU_CHOI);
        baoTT.setLyDoTuChoi(lyDo);
        baoThanhToanRepository.save(baoTT);

        // TBT về trạng thái trước khi khách báo — tái tạo bằng cách tính lại từ soDaThu hiện có,
        // không cần cột lưu riêng "trạng thái trước" (sheet 2_ThuocTinh không có cột đó).
        String maThongBaoTien = baoTT.getThongBaoTien().getMaThongBaoTien();
        thongBaoTienService.capNhatTrangThaiThu(maThongBaoTien);

        nhatKyThaoTacService.ghiNhan(maTaiKhoanThucHien, "ThongBaoTien", maThongBaoTien, "TU_CHOI", lyDo);
    }
}
