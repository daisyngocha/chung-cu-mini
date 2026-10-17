export function formatTien(so: number | string) {
  return new Intl.NumberFormat("vi-VN").format(Number(so)) + " đ";
}

export const TEN_TRANG_THAI_PHONG: Record<string, string> = {
  TRONG: "Trống",
  DANG_THUE: "Đang thuê",
  NGUNG_KHAI_THAC: "Ngừng khai thác",
};
