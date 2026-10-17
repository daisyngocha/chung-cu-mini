import { formatTien, formatNgay, TEN_LOAI_KHOAN } from "@/lib/format";
import { DoiDonGiaForm } from "./doi-don-gia-form";

export type BangGiaApi = {
  maKhoan: string;
  tenKhoan: string;
  loaiKhoan: string;
  donGia: number;
  donViTinh: string;
  ngayApDung: string;
  ngayHetHieuLuc: string | null;
};

export function BangGiaTable({ danhSach }: { danhSach: BangGiaApi[] }) {
  return (
    <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
      <table className="w-full text-sm">
        <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
          <tr>
            <th className="px-4 py-2">Tên khoản</th>
            <th className="px-4 py-2">Loại</th>
            <th className="px-4 py-2">Đơn giá</th>
            <th className="px-4 py-2">Đơn vị tính</th>
            <th className="px-4 py-2">Áp dụng từ</th>
            <th className="px-4 py-2">Hết hiệu lực</th>
            <th className="px-4 py-2" />
          </tr>
        </thead>
        <tbody className="divide-y divide-zinc-100">
          {danhSach.map((bg) => (
            <tr key={bg.maKhoan}>
              <td className="px-4 py-2 font-medium text-zinc-900">{bg.tenKhoan}</td>
              <td className="px-4 py-2">{TEN_LOAI_KHOAN[bg.loaiKhoan] ?? bg.loaiKhoan}</td>
              <td className="px-4 py-2">{formatTien(bg.donGia)}</td>
              <td className="px-4 py-2">{bg.donViTinh}</td>
              <td className="px-4 py-2">{formatNgay(bg.ngayApDung)}</td>
              <td className="px-4 py-2">
                {bg.ngayHetHieuLuc ? (
                  formatNgay(bg.ngayHetHieuLuc)
                ) : (
                  <span className="rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-700">
                    Đang hiệu lực
                  </span>
                )}
              </td>
              <td className="px-4 py-2 text-right">
                {!bg.ngayHetHieuLuc && (
                  <DoiDonGiaForm maKhoan={bg.maKhoan} loaiKhoan={bg.loaiKhoan} />
                )}
              </td>
            </tr>
          ))}
          {danhSach.length === 0 && (
            <tr>
              <td colSpan={7} className="px-4 py-6 text-center text-zinc-500">
                Chưa có khoản giá nào. Thêm khoản đầu tiên ở trên.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
