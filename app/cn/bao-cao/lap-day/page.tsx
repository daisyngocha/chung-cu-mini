import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatPhanTram, TEN_TRANG_THAI_PHONG } from "@/lib/format";

type TinhTrangPhongApi = {
  soLuongTheoTrangThai: Record<string, number>;
};

function kyThangHienTai() {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}

export default async function BaoCaoLapDayPage({
  searchParams,
}: {
  searchParams: Promise<{ kyThang?: string }>;
}) {
  const { kyThang: kyThangParam } = await searchParams;
  const kyThang = kyThangParam || kyThangHienTai();
  const token = await getSessionToken();

  const [tinhTrangPhong, tyLeLapDay] = await Promise.all([
    goiBackend<TinhTrangPhongApi>("/api/bao-cao/tinh-trang-phong", { token }),
    goiBackend<number>(`/api/bao-cao/ty-le-lap-day?kyThang=${kyThang}`, { token }),
  ]);

  const tongSoPhong = Object.values(tinhTrangPhong.soLuongTheoTrangThai).reduce((a, b) => a + b, 0);

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Phòng & tỷ lệ lấp đầy</h1>

      <form method="GET" className="flex items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Kỳ tháng</label>
          <input
            name="kyThang"
            type="month"
            defaultValue={kyThang}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Xem kỳ
        </button>
      </form>

      <div className="rounded-lg border border-zinc-200 bg-white p-4">
        <p className="text-xs text-zinc-500">Tỷ lệ lấp đầy kỳ {kyThang}</p>
        <p className="mt-1 text-2xl font-semibold text-zinc-900">{formatPhanTram(tyLeLapDay)}</p>
      </div>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Trạng thái</th>
              <th className="px-4 py-2">Số phòng</th>
              <th className="px-4 py-2">Tỷ lệ</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {Object.entries(tinhTrangPhong.soLuongTheoTrangThai).map(([trangThai, soLuong]) => (
              <tr key={trangThai}>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  {TEN_TRANG_THAI_PHONG[trangThai] ?? trangThai}
                </td>
                <td className="px-4 py-2">{soLuong}</td>
                <td className="px-4 py-2">{tongSoPhong > 0 ? formatPhanTram(soLuong / tongSoPhong) : "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
