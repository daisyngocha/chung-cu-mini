import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { XacNhanForm, TuChoiForm } from "./forms";

type BaoThanhToanApi = {
  maBaoTT: string;
  maThongBaoTien: string;
  maPhong: string;
  thoiDiemBao: string;
  anhBienLai: string | null;
  trangThai: string;
};

export default async function XacNhanThanhToanPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<BaoThanhToanApi[]>("/api/bao-thanh-toan/cho-xac-nhan", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Xác nhận thanh toán</h1>
      <p className="text-sm text-zinc-600">
        Danh sách báo chuyển khoản của khách đang chờ chủ nhà xác nhận (sinh phiếu thu) hoặc từ chối.
      </p>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Phòng</th>
              <th className="px-4 py-2">Thông báo tiền</th>
              <th className="px-4 py-2">Thời điểm báo</th>
              <th className="px-4 py-2">Ảnh biên lai</th>
              <th className="px-4 py-2" />
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {danhSach.map((b) => (
              <tr key={b.maBaoTT}>
                <td className="px-4 py-2 font-medium text-zinc-900">{b.maPhong}</td>
                <td className="px-4 py-2">
                  <Link href={`/cn/thong-bao-tien/${b.maThongBaoTien}`} className="hover:underline">
                    {b.maThongBaoTien}
                  </Link>
                </td>
                <td className="px-4 py-2">{new Date(b.thoiDiemBao).toLocaleString("vi-VN")}</td>
                <td className="px-4 py-2">
                  {b.anhBienLai ? (
                    <a href={b.anhBienLai} target="_blank" rel="noreferrer" className="underline">
                      Xem ảnh
                    </a>
                  ) : (
                    "—"
                  )}
                </td>
                <td className="px-4 py-2">
                  <div className="flex flex-col items-end gap-2">
                    <XacNhanForm maBaoTT={b.maBaoTT} />
                    <TuChoiForm maBaoTT={b.maBaoTT} />
                  </div>
                </td>
              </tr>
            ))}
            {danhSach.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-6 text-center text-zinc-500">
                  Không có báo chuyển khoản nào đang chờ xác nhận.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
