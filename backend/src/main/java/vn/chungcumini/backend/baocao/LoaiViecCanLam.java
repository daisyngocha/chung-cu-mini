package vn.chungcumini.backend.baocao;

/**
 * Không phải enum DB (DichVuViecCanLam không lưu CSDL). BAO_CK_CHO_XAC_NHAN (module BaoThanhToan)
 * và SU_CO_MOI_MO_LAI (module SuCo) chưa gộp vào nhánh này — xem ghi chú ở DichVuViecCanLamService.
 */
public enum LoaiViecCanLam {
    BAO_CK_CHO_XAC_NHAN,
    THONG_BAO_QUA_HAN,
    SU_CO_MOI_MO_LAI,
    HOP_DONG_SAP_HET_HAN,
    PHONG_THIEU_CHI_SO,
    YEU_CAU_TRA_PHONG_MOI,
}
