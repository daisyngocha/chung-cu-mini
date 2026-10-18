import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgayGio } from "@/lib/format";

type NhatKyApi = {
  maNhatKy: string;
  maTaiKhoan: string | null;
  thoiDiem: string;
  doiTuong: string;
  maDoiTuong: string;
  hanhDong: string;
  noiDung: string | null;
};

export default async function CaiDatNhatKyPage({
  searchParams,
}: {
  searchParams: Promise<{ doiTuong?: string }>;
}) {
  const { doiTuong } = await searchParams;
  const token = await getSessionToken();
  const danhSach = await goiBackend<NhatKyApi[]>(
    `/api/nhat-ky${doiTuong ? `?doiTuong=${encodeURIComponent(doiTuong)}` : ""}`,
    { token },
  );

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Nhật ký thao tác</h1>

      <form method="GET" className="flex items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Lọc theo đối tượng</label>
          <input
            name="doiTuong"
            defaultValue={doiTuong}
            placeholder="vd. HopDong, BangGia, QuyetToan..."
            className="w-56 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Lọc
        </button>
      </form>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Thời điểm</th>
              <th className="px-4 py-2">Tài khoản</th>
              <th className="px-4 py-2">Đối tượng</th>
              <th className="px-4 py-2">Hành động</th>
              <th className="px-4 py-2">Nội dung</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {danhSach.map((nk) => (
              <tr key={nk.maNhatKy}>
                <td className="px-4 py-2 whitespace-nowrap">{formatNgayGio(nk.thoiDiem)}</td>
                <td className="px-4 py-2">{nk.maTaiKhoan ?? "Hệ thống"}</td>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  {nk.doiTuong}:{nk.maDoiTuong}
                </td>
                <td className="px-4 py-2">{nk.hanhDong}</td>
                <td className="px-4 py-2">{nk.noiDung ?? "—"}</td>
              </tr>
            ))}
            {danhSach.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-6 text-center text-zinc-500">
                  Không có nhật ký nào khớp bộ lọc.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
