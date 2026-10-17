import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay } from "@/lib/format";

type CocDangGiuApi = {
  maPhong: string;
  maHopDong: string;
  tienCoc: number;
};

type HopDongApi = {
  maHopDong: string;
  maPhong: string;
  ngayHetHan: string;
};

export default async function BaoCaoCocHopDongPage({
  searchParams,
}: {
  searchParams: Promise<{ soNgay?: string }>;
}) {
  const { soNgay: soNgayParam } = await searchParams;
  const soNgay = soNgayParam || "30";
  const token = await getSessionToken();

  const [cocDangGiu, hopDongSapHetHan] = await Promise.all([
    goiBackend<CocDangGiuApi[]>("/api/bao-cao/coc-dang-giu", { token }),
    goiBackend<HopDongApi[]>(`/api/bao-cao/hop-dong-sap-het-han?soNgay=${soNgay}`, { token }),
  ]);

  const tongTienCoc = cocDangGiu.reduce((a, c) => a + c.tienCoc, 0);

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Cọc & hợp đồng sắp hết hạn</h1>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <div className="flex items-center justify-between border-b border-zinc-200 px-4 py-2">
          <span className="text-sm font-semibold text-zinc-900">Cọc đang giữ</span>
          <span className="text-sm font-semibold text-zinc-900">Tổng: {formatTien(tongTienCoc)}</span>
        </div>
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Phòng</th>
              <th className="px-4 py-2">Hợp đồng</th>
              <th className="px-4 py-2">Tiền cọc</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {cocDangGiu.map((c) => (
              <tr key={c.maHopDong}>
                <td className="px-4 py-2 font-medium text-zinc-900">{c.maPhong}</td>
                <td className="px-4 py-2">
                  <Link href={`/cn/hop-dong/${c.maHopDong}`} className="hover:underline">
                    {c.maHopDong}
                  </Link>
                </td>
                <td className="px-4 py-2">{formatTien(c.tienCoc)}</td>
              </tr>
            ))}
            {cocDangGiu.length === 0 && (
              <tr>
                <td colSpan={3} className="px-4 py-6 text-center text-zinc-500">
                  Không có cọc nào đang giữ.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <form method="GET" className="flex items-end gap-3 border-b border-zinc-200 px-4 py-3">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-medium text-zinc-600">Hợp đồng hết hạn trong vòng (ngày)</label>
            <input
              name="soNgay"
              type="number"
              min={1}
              defaultValue={soNgay}
              className="w-24 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
            />
          </div>
          <button
            type="submit"
            className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
          >
            Lọc
          </button>
        </form>
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Phòng</th>
              <th className="px-4 py-2">Hợp đồng</th>
              <th className="px-4 py-2">Hết hạn ngày</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {hopDongSapHetHan.map((h) => (
              <tr key={h.maHopDong}>
                <td className="px-4 py-2 font-medium text-zinc-900">{h.maPhong}</td>
                <td className="px-4 py-2">
                  <Link href={`/cn/hop-dong/${h.maHopDong}`} className="hover:underline">
                    {h.maHopDong}
                  </Link>
                </td>
                <td className="px-4 py-2">{formatNgay(h.ngayHetHan)}</td>
              </tr>
            ))}
            {hopDongSapHetHan.length === 0 && (
              <tr>
                <td colSpan={3} className="px-4 py-6 text-center text-zinc-500">
                  Không có hợp đồng nào sắp hết hạn trong khoảng đã chọn.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
