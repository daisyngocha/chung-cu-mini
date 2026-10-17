import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgay } from "@/lib/format";

type HopDongSummaryApi = {
  maHopDong: string;
  maPhong: string;
  ngayBatDau: string;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

export default async function NhanPhongPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<HopDongSummaryApi[]>("/api/hop-dong", { token });
  const choNhanPhong = danhSach.filter((hd) => hd.trangThai === "CHO_NHAN_PHONG");

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Nhận phòng</h1>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Mã hợp đồng</th>
              <th className="px-4 py-2">Phòng</th>
              <th className="px-4 py-2">Ngày bắt đầu</th>
              <th className="px-4 py-2" />
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {choNhanPhong.map((hd) => (
              <tr key={hd.maHopDong}>
                <td className="px-4 py-2 font-medium text-zinc-900">{hd.maHopDong}</td>
                <td className="px-4 py-2">{hd.maPhong}</td>
                <td className="px-4 py-2">{formatNgay(hd.ngayBatDau)}</td>
                <td className="px-4 py-2 text-right">
                  <Link href={`/cn/nhan-phong/${hd.maHopDong}`} className="text-xs font-medium text-zinc-900 hover:underline">
                    Xử lý nhận phòng
                  </Link>
                </td>
              </tr>
            ))}
            {choNhanPhong.length === 0 && (
              <tr>
                <td colSpan={4} className="px-4 py-6 text-center text-zinc-500">
                  Không có hợp đồng nào đang chờ nhận phòng.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
