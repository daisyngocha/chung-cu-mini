export function formatTien(so: number | string) {
  return new Intl.NumberFormat("vi-VN").format(Number(so)) + " đ";
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

export function formatNgay(ngay: string | null | undefined) {
  if (!ngay) return "—";
  return new Intl.DateTimeFormat("vi-VN").format(new Date(ngay));
}
