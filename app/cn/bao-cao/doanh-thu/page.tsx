import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatPhanTram, formatNgay, TEN_LOAI_KHOAN_TBT } from "@/lib/format";

type CongNoQuaHanApi = {
  maPhong: string;
  maThongBaoTien: string;
  soConNo: number;
  soNgayQuaHan: number;
};

function kyThangHienTai() {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}

function dauThang() {
  const now = new Date();
  return new Date(now.getFullYear(), now.getMonth(), 1).toISOString().slice(0, 10);
}

function homNay() {
  return new Date().toISOString().slice(0, 10);
}

export default async function BaoCaoDoanhThuPage({
  searchParams,
}: {
  searchParams: Promise<{ kyThang?: string; tuNgay?: string; denNgay?: string }>;
}) {
  const { kyThang: kyThangParam, tuNgay: tuNgayParam, denNgay: denNgayParam } = await searchParams;
  const kyThang = kyThangParam || kyThangHienTai();
  const tuNgay = tuNgayParam || dauThang();
  const denNgay = denNgayParam || homNay();
  const token = await getSessionToken();

  const [doanhThu, tyLeThu, congNoQuaHan] = await Promise.all([
    goiBackend<Record<string, number>>(`/api/bao-cao/doanh-thu?kyThang=${kyThang}`, { token }),
    goiBackend<number>(`/api/bao-cao/ty-le-thu?tuNgay=${tuNgay}&denNgay=${denNgay}`, { token }),
    goiBackend<CongNoQuaHanApi[]>("/api/bao-cao/cong-no-qua-han", { token }),
  ]);

  const tongDoanhThu = Object.values(doanhThu).reduce((a, b) => a + b, 0);

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Doanh thu, tỷ lệ thu & công nợ</h1>

      <form method="GET" className="flex flex-wrap items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Kỳ doanh thu</label>
          <input
            name="kyThang"
            type="month"
            defaultValue={kyThang}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Tỷ lệ thu — từ ngày</label>
          <input
            name="tuNgay"
            type="date"
            defaultValue={tuNgay}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Đến ngày</label>
          <input
            name="denNgay"
            type="date"
            defaultValue={denNgay}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Xem
        </button>
      </form>

      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
        <div className="rounded-lg border border-zinc-200 bg-white p-4">
          <p className="text-xs text-zinc-500">Tổng doanh thu kỳ {kyThang}</p>
          <p className="mt-1 text-xl font-semibold text-zinc-900">{formatTien(tongDoanhThu)}</p>
        </div>
        <div className="rounded-lg border border-zinc-200 bg-white p-4">
          <p className="text-xs text-zinc-500">
            Tỷ lệ thu ({formatNgay(tuNgay)} – {formatNgay(denNgay)})
          </p>
          <p className="mt-1 text-xl font-semibold text-zinc-900">{formatPhanTram(tyLeThu)}</p>
        </div>
      </div>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <div className="border-b border-zinc-200 px-4 py-2 text-sm font-semibold text-zinc-900">
          Doanh thu theo khoản — kỳ {kyThang}
        </div>
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Loại khoản</th>
              <th className="px-4 py-2">Doanh thu</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {Object.entries(doanhThu).map(([loaiKhoan, soTien]) => (
              <tr key={loaiKhoan}>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  {TEN_LOAI_KHOAN_TBT[loaiKhoan] ?? loaiKhoan}
                </td>
                <td className="px-4 py-2">{formatTien(soTien)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <div className="border-b border-zinc-200 px-4 py-2 text-sm font-semibold text-zinc-900">
          Công nợ quá hạn (tính đến hôm nay)
        </div>
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Phòng</th>
              <th className="px-4 py-2">Thông báo tiền</th>
              <th className="px-4 py-2">Còn nợ</th>
              <th className="px-4 py-2">Số ngày quá hạn</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {congNoQuaHan.map((cn) => (
              <tr key={cn.maThongBaoTien}>
                <td className="px-4 py-2 font-medium text-zinc-900">{cn.maPhong}</td>
                <td className="px-4 py-2">
                  <Link href={`/cn/thong-bao-tien/${cn.maThongBaoTien}`} className="hover:underline">
                    {cn.maThongBaoTien}
                  </Link>
                </td>
                <td className="px-4 py-2">{formatTien(cn.soConNo)}</td>
                <td className="px-4 py-2">{cn.soNgayQuaHan} ngày</td>
              </tr>
            ))}
            {congNoQuaHan.length === 0 && (
              <tr>
                <td colSpan={4} className="px-4 py-6 text-center text-zinc-500">
                  Không có công nợ quá hạn.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
