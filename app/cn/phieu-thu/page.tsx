import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay, TEN_HINH_THUC_TT } from "@/lib/format";
import { HuyForm } from "./huy-form";

type PhieuThuApi = {
  soPhieuThu: string;
  maThongBaoTien: string;
  maPhong: string;
  maBaoTT: string | null;
  ngayThu: string;
  nguoiNop: string | null;
  soTien: number;
  hinhThuc: string;
  trangThai: "HIEU_LUC" | "DA_HUY";
  lyDoHuy: string | null;
};

export default async function PhieuThuPage({
  searchParams,
}: {
  searchParams: Promise<{ maThongBaoTien?: string }>;
}) {
  const { maThongBaoTien } = await searchParams;
  const token = await getSessionToken();
  const danhSach = maThongBaoTien
    ? await goiBackend<PhieuThuApi[]>(
        `/api/phieu-thu?maThongBaoTien=${encodeURIComponent(maThongBaoTien)}`,
        { token },
      )
    : [];

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Phiếu thu</h1>

      <form method="GET" className="flex items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Mã thông báo tiền</label>
          <input
            name="maThongBaoTien"
            defaultValue={maThongBaoTien}
            placeholder="vd. TB2610101"
            className="w-56 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Tìm
        </button>
      </form>

      {maThongBaoTien && (
        <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
          <table className="w-full text-sm">
            <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
              <tr>
                <th className="px-4 py-2">Số phiếu thu</th>
                <th className="px-4 py-2">Phòng</th>
                <th className="px-4 py-2">Ngày thu</th>
                <th className="px-4 py-2">Người nộp</th>
                <th className="px-4 py-2">Số tiền</th>
                <th className="px-4 py-2">Hình thức</th>
                <th className="px-4 py-2">Trạng thái</th>
                <th className="px-4 py-2" />
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-100">
              {danhSach.map((p) => (
                <tr key={p.soPhieuThu}>
                  <td className="px-4 py-2 font-medium text-zinc-900">{p.soPhieuThu}</td>
                  <td className="px-4 py-2">{p.maPhong}</td>
                  <td className="px-4 py-2">{formatNgay(p.ngayThu)}</td>
                  <td className="px-4 py-2">{p.nguoiNop ?? "—"}</td>
                  <td className="px-4 py-2">{formatTien(p.soTien)}</td>
                  <td className="px-4 py-2">{TEN_HINH_THUC_TT[p.hinhThuc] ?? p.hinhThuc}</td>
                  <td className="px-4 py-2">
                    {p.trangThai === "DA_HUY" ? (
                      <span className="rounded-full bg-zinc-100 px-2 py-0.5 text-xs font-medium text-zinc-500">
                        Đã hủy{p.lyDoHuy ? `: ${p.lyDoHuy}` : ""}
                      </span>
                    ) : (
                      <span className="rounded-full bg-green-100 px-2 py-0.5 text-xs font-medium text-green-700">
                        Hiệu lực
                      </span>
                    )}
                  </td>
                  <td className="px-4 py-2 text-right">
                    <div className="flex flex-col items-end gap-2">
                      <a
                        href={`/cn/phieu-thu/${p.soPhieuThu}/file`}
                        className="text-xs font-medium text-zinc-600 hover:underline"
                      >
                        Xuất PDF
                      </a>
                      {p.trangThai === "HIEU_LUC" && (
                        <HuyForm soPhieuThu={p.soPhieuThu} maThongBaoTien={maThongBaoTien} />
                      )}
                    </div>
                  </td>
                </tr>
              ))}
              {danhSach.length === 0 && (
                <tr>
                  <td colSpan={8} className="px-4 py-6 text-center text-zinc-500">
                    Chưa có phiếu thu nào cho thông báo này.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
