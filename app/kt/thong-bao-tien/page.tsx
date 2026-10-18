import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay, TEN_TRANG_THAI_TBT } from "@/lib/format";

type ThongBaoTienApi = {
  maThongBaoTien: string;
  kyThang: string;
  hanThanhToan: string | null;
  tongTien: number;
  soDaThu: number;
  trangThai: string;
};

const MAU_TRANG_THAI: Record<string, string> = {
  DA_PHAT_HANH: "bg-blue-100 text-blue-700",
  CHO_XAC_NHAN: "bg-amber-100 text-amber-700",
  DA_THU_MOT_PHAN: "bg-amber-100 text-amber-700",
  DA_THU_DU: "bg-green-100 text-green-700",
  QUA_HAN: "bg-red-100 text-red-700",
  DA_HUY: "bg-zinc-100 text-zinc-400",
};

export default async function KhachThongBaoTienPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<ThongBaoTienApi[]>("/api/khach/thong-bao-tien", { token });
  const sapXep = [...danhSach].sort((a, b) => b.kyThang.localeCompare(a.kyThang));

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Thông báo tiền</h1>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Kỳ</th>
              <th className="px-4 py-2">Hạn thanh toán</th>
              <th className="px-4 py-2">Tổng tiền</th>
              <th className="px-4 py-2">Đã thu</th>
              <th className="px-4 py-2">Trạng thái</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {sapXep.map((tb) => (
              <tr key={tb.maThongBaoTien}>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  <Link href={`/kt/thong-bao-tien/${tb.maThongBaoTien}`} className="hover:underline">
                    {tb.kyThang}
                  </Link>
                </td>
                <td className="px-4 py-2">{formatNgay(tb.hanThanhToan)}</td>
                <td className="px-4 py-2">{formatTien(tb.tongTien)}</td>
                <td className="px-4 py-2">{formatTien(tb.soDaThu)}</td>
                <td className="px-4 py-2">
                  <span className={"rounded-full px-2 py-0.5 text-xs font-medium " + (MAU_TRANG_THAI[tb.trangThai] ?? "")}>
                    {TEN_TRANG_THAI_TBT[tb.trangThai] ?? tb.trangThai}
                  </span>
                </td>
              </tr>
            ))}
            {sapXep.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có thông báo tiền nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
