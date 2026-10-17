export function formatTien(so: number | string) {
  return new Intl.NumberFormat("vi-VN").format(Number(so)) + " đ";
}

export function formatPhanTram(ti: number | string) {
  return new Intl.NumberFormat("vi-VN", { style: "percent", maximumFractionDigits: 1 }).format(Number(ti));
}

export const TEN_TRANG_THAI_PHONG: Record<string, string> = {
  TRONG: "Trống",
  DANG_THUE: "Đang thuê",
  NGUNG_KHAI_THAC: "Ngừng khai thác",
};

export const TEN_TRANG_THAI_HOP_DONG: Record<string, string> = {
  CHO_NHAN_PHONG: "Chờ nhận phòng",
  HIEU_LUC: "Hiệu lực",
  DA_THANH_LY: "Đã thanh lý",
};

export const TEN_LOAI_KET_THUC: Record<string, string> = {
  KHACH_BAO_TRA: "Khách báo trả",
  HET_HAN: "Hết hạn hợp đồng",
  CHAM_DUT_VI_PHAM: "Chấm dứt do vi phạm",
};

export const TEN_LOAI_KHOAN: Record<string, string> = {
  DIEN: "Điện",
  NUOC: "Nước (theo đồng hồ)",
  NUOC_THEO_NGUOI: "Nước (theo người)",
  DICH_VU_THEO_PHONG: "Dịch vụ theo phòng",
  DICH_VU_THEO_XE: "Dịch vụ theo xe",
};

export const TEN_LOAI_CONG_TO: Record<string, string> = {
  DIEN: "Điện",
  NUOC: "Nước",
};

export const TEN_LOAI_GHI: Record<string, string> = {
  DAU_KY: "Đầu kỳ",
  HANG_THANG: "Hàng tháng",
  CUOI_KY: "Cuối kỳ",
};

export const TEN_TRANG_THAI_TBT: Record<string, string> = {
  NHAP: "Nháp",
  DA_PHAT_HANH: "Đã phát hành",
  CHO_XAC_NHAN: "Chờ xác nhận",
  DA_THU_MOT_PHAN: "Đã thu một phần",
  DA_THU_DU: "Đã thu đủ",
  QUA_HAN: "Quá hạn",
  DA_HUY: "Đã hủy",
};

export const TEN_LOAI_KHOAN_TBT: Record<string, string> = {
  TIEN_PHONG: "Tiền phòng",
  TIEN_COC: "Tiền cọc",
  DIEN: "Điện",
  NUOC: "Nước",
  DICH_VU: "Dịch vụ",
  PHI_PHAT_SINH: "Phí phát sinh",
  GIAM_TRU: "Giảm trừ",
  DIEU_CHINH: "Điều chỉnh",
};

export const TEN_HINH_THUC_TT: Record<string, string> = {
  CHUYEN_KHOAN: "Chuyển khoản",
  TIEN_MAT: "Tiền mặt",
};

export const TEN_LOAI_BAN_GIAO: Record<string, string> = {
  NHAN_PHONG: "Nhận phòng",
  TRA_PHONG: "Trả phòng",
};

export const TEN_LOAI_SU_CO: Record<string, string> = {
  DIEN: "Điện",
  NUOC: "Nước",
  THIET_BI: "Thiết bị",
  INTERNET: "Internet",
  KHAC: "Khác",
};

export const TEN_TRANG_THAI_SU_CO: Record<string, string> = {
  MOI: "Mới",
  DANG_XU_LY: "Đang xử lý",
  DA_XU_LY: "Đã xử lý",
  TU_CHOI: "Từ chối",
  MO_LAI: "Mở lại",
};

export function formatNgay(ngay: string | null | undefined) {
  if (!ngay) return "—";
  return new Intl.DateTimeFormat("vi-VN").format(new Date(ngay));
}

export function formatNgayGio(ngayGio: string | null | undefined) {
  if (!ngayGio) return "—";
  return new Intl.DateTimeFormat("vi-VN", { dateStyle: "short", timeStyle: "short" }).format(new Date(ngayGio));
}
