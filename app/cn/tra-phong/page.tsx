import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgay } from "@/lib/format";

type HopDongSummaryApi = {
  maHopDong: string;
  maPhong: string;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

type HopDongApi = {
  maHopDong: string;
  ngayBaoTra: string | null;
  ngayTraDuKien: string | null;
};

export default async function TraPhongPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<HopDongSummaryApi[]>("/api/hop-dong", { token });
  const dsHieuLuc = danhSach.filter((hd) => hd.trangThai === "HIEU_LUC");

  const chiTietDaBaoTra = (
    await Promise.all(
      dsHieuLuc.map((hd) => goiBackend<HopDongApi>(`/api/hop-dong/${hd.maHopDong}`, { token })),
    )
  ).filter((hd) => hd.ngayBaoTra !== null);

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Trả phòng</h1>
      <p className="text-sm text-zinc-600">
        Hợp đồng đã báo trả phòng, chờ ghi chỉ số cuối kỳ, kiểm tra bàn giao tài sản và tính quyết toán cọc.
      </p>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Mã hợp đồng</th>
              <th className="px-4 py-2">Ngày trả dự kiến</th>
              <th className="px-4 py-2" />
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {chiTietDaBaoTra.map((hd) => (
              <tr key={hd.maHopDong}>
                <td className="px-4 py-2 font-medium text-zinc-900">{hd.maHopDong}</td>
                <td className="px-4 py-2">{formatNgay(hd.ngayTraDuKien)}</td>
                <td className="px-4 py-2 text-right">
                  <Link href={`/cn/tra-phong/${hd.maHopDong}`} className="text-xs font-medium text-zinc-900 hover:underline">
                    Xử lý trả phòng
                  </Link>
                </td>
              </tr>
            ))}
            {chiTietDaBaoTra.length === 0 && (
              <tr>
                <td colSpan={3} className="px-4 py-6 text-center text-zinc-500">
                  Không có hợp đồng nào đã báo trả phòng và đang chờ xử lý.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
