package vn.chungcumini.backend.banggia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.chungcumini.backend.domain.BangGia;
import vn.chungcumini.backend.domain.LoaiKhoan;
import vn.chungcumini.backend.nhatky.NhatKyThaoTacService;
import vn.chungcumini.backend.repository.BangGiaRepository;
import vn.chungcumini.backend.repository.ToaNhaRepository;

@Service
public class BangGiaService {

    private final BangGiaRepository bangGiaRepository;
    private final ToaNhaRepository toaNhaRepository;
    private final NhatKyThaoTacService nhatKyThaoTacService;

    public BangGiaService(
            BangGiaRepository bangGiaRepository, ToaNhaRepository toaNhaRepository, NhatKyThaoTacService nhatKyThaoTacService) {
        this.bangGiaRepository = bangGiaRepository;
        this.toaNhaRepository = toaNhaRepository;
        this.nhatKyThaoTacService = nhatKyThaoTacService;
    }

    public List<BangGiaResponse> layDanhSach() {
        return bangGiaRepository.findAllByOrderByTenKhoanAscNgayApDungDesc().stream().map(BangGiaResponse::tu).toList();
    }

    /** BangGia.layDonGiaTai: đơn giá hiệu lực của loaiKhoan tại một ngày (BR-DM-01). Chỉ dùng cho DIEN/NUOC/NUOC_THEO_NGUOI. */
    public BigDecimal layDonGiaTai(LoaiKhoan loaiKhoan, LocalDate ngay) {
        return bangGiaRepository
                .timMotHieuLucTai(loaiKhoan, ngay)
                .map(BangGia::getDonGia)
                .orElseThrow(() -> new IllegalArgumentException("Chưa có đơn giá " + loaiKhoan + " hiệu lực tại ngày " + ngay + "."));
    }

    /** Không chồng lấn hiệu lực giữa các bản ghi cùng tenKhoan (ràng buộc dữ liệu ở sheet 2_ThuocTinh). */
    @Transactional
    public BangGiaResponse taoKhoan(TaoBangGiaRequest req) {
        if (bangGiaRepository.existsByTenKhoanAndNgayHetHieuLucIsNull(req.tenKhoan())) {
            throw new IllegalArgumentException(
                    "Khoản \"" + req.tenKhoan() + "\" đã có bản ghi đang hiệu lực, dùng đổi đơn giá thay vì tạo mới.");
        }
        BangGia khoan = new BangGia();
        khoan.setMaKhoan("BG" + String.format("%04d", bangGiaRepository.count() + 1));
        khoan.setToaNha(toaNhaRepository.findById("TOA01").orElseThrow());
        khoan.setTenKhoan(req.tenKhoan());
        khoan.setLoaiKhoan(req.loaiKhoan());
        khoan.setDonGia(req.donGia());
        khoan.setDonViTinh(req.donViTinh());
        khoan.setNgayApDung(req.ngayApDung());
        return BangGiaResponse.tu(bangGiaRepository.save(khoan));
    }

    /** BangGia.doiDonGia: đóng bản ghi hiện hành, tạo bản ghi mới với đơn giá mới (BR-DM-01). */
    @Transactional
    public BangGiaResponse doiDonGia(String maKhoanHienHanh, BigDecimal giaMoi, LocalDate ngayApDung, String maTaiKhoanThucHien) {
        if (giaMoi == null || giaMoi.signum() < 0) {
            throw new IllegalArgumentException("Đơn giá mới không được âm.");
        }
        BangGia hienHanh = bangGiaRepository
                .findById(maKhoanHienHanh)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khoản giá \"" + maKhoanHienHanh + "\"."));
        if (!ngayApDung.isAfter(hienHanh.getNgayApDung())) {
            throw new IllegalArgumentException("Ngày áp dụng mới phải sau ngày áp dụng hiện hành.");
        }

        hienHanh.setNgayHetHieuLuc(ngayApDung.minusDays(1));
        bangGiaRepository.save(hienHanh);

        BangGia moi = new BangGia();
        moi.setMaKhoan("BG" + String.format("%04d", bangGiaRepository.count() + 1));
        moi.setToaNha(hienHanh.getToaNha());
        moi.setTenKhoan(hienHanh.getTenKhoan());
        moi.setLoaiKhoan(hienHanh.getLoaiKhoan());
        moi.setDonGia(giaMoi);
        moi.setDonViTinh(hienHanh.getDonViTinh());
        moi.setNgayApDung(ngayApDung);
        moi = bangGiaRepository.save(moi);

        nhatKyThaoTacService.ghiNhan(
                maTaiKhoanThucHien,
                "BangGia",
                hienHanh.getMaKhoan(),
                "SUA",
                "Đổi đơn giá \"" + hienHanh.getTenKhoan() + "\" từ " + hienHanh.getDonGia() + " sang " + giaMoi
                        + " áp dụng từ " + ngayApDung + ".");

        return BangGiaResponse.tu(moi);
    }
}
