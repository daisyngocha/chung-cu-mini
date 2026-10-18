import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay, TEN_HINH_THUC_TT } from "@/lib/format";

type PhieuThuApi = {
  soPhieuThu: string;
  ngayThu: string;
  nguoiNop: string | null;
  soTien: number;
  hinhThuc: string;
  trangThai: "HIEU_LUC" | "DA_HUY";
};

export default async function KhachPhieuThuPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<PhieuThuApi[]>("/api/khach/phieu-thu", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Phiếu thu</h1>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Số phiếu thu</th>
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
                <td className="px-4 py-2">{formatNgay(p.ngayThu)}</td>
                <td className="px-4 py-2">{p.nguoiNop ?? "—"}</td>
                <td className="px-4 py-2">{formatTien(p.soTien)}</td>
                <td className="px-4 py-2">{TEN_HINH_THUC_TT[p.hinhThuc] ?? p.hinhThuc}</td>
                <td className="px-4 py-2">{p.trangThai === "DA_HUY" ? "Đã hủy" : "Hiệu lực"}</td>
                <td className="px-4 py-2 text-right">
                  <a href={`/kt/phieu-thu/${p.soPhieuThu}/file`} className="text-xs font-medium text-zinc-600 hover:underline">
                    Tải PDF
                  </a>
                </td>
              </tr>
            ))}
            {danhSach.length === 0 && (
              <tr>
                <td colSpan={7} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có phiếu thu nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
