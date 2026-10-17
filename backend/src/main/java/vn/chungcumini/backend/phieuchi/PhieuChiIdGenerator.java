package vn.chungcumini.backend.phieuchi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import vn.chungcumini.backend.repository.PhieuChiRepository;

/** PhieuChi.sinhSoPhieu: PC-yyyy-NNNNN, đánh số liên tục theo năm. */
public final class PhieuChiIdGenerator {

    private static final DateTimeFormatter NAM = DateTimeFormatter.ofPattern("yyyy");

    private PhieuChiIdGenerator() {
    }

    public static String sinh(PhieuChiRepository phieuChiRepository, LocalDate ngay) {
        String tienTo = "PC-" + ngay.format(NAM) + "-";
        long soLuong = phieuChiRepository.countBySoPhieuChiStartingWith(tienTo);
        return tienTo + String.format("%05d", soLuong + 1);
    }
}
