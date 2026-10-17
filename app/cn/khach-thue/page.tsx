import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay, TEN_TRANG_THAI_HOP_DONG } from "@/lib/format";
import { KhachThueForm } from "./khach-thue-form";

type KhachThueApi = {
  maKhach: string;
  soDienThoai: string;
  hoTen: string;
  ngaySinh: string | null;
  soCCCD: string;
  anhCCCD: string | null;
  queQuan: string | null;
  noiLamViec: string | null;
};

type HopDongSummaryApi = {
  maHopDong: string;
  maPhong: string;
  ngayBatDau: string;
  ngayHetHan: string;
  giaThue: number;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

export default async function KhachThuePage({
  searchParams,
}: {
  searchParams: Promise<{ sdt?: string }>;
}) {
  const { sdt } = await searchParams;
  const token = await getSessionToken();

  const khach = sdt
    ? await goiBackend<KhachThueApi | null>(
        `/api/khach-thue?sdt=${encodeURIComponent(sdt)}`,
        { token },
      )
    : null;

  const lichSuThue = khach
    ? await goiBackend<HopDongSummaryApi[]>(
        `/api/khach-thue/${khach.maKhach}/lich-su-thue`,
        { token },
      )
    : [];

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Khách thuê & hợp đồng</h1>

      <form
        method="GET"
        className="flex items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4"
      >
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">
            Tìm khách theo số điện thoại
          </label>
          <input
            name="sdt"
            defaultValue={sdt}
            placeholder="0901234567"
            className="w-56 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Tìm
        </button>
        {sdt && (
          <Link
            href="/cn/khach-thue"
            className="text-sm text-zinc-500 hover:underline"
          >
            Xóa tìm kiếm
          </Link>
        )}
      </form>

      {sdt && !khach && (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Không tìm thấy khách với số điện thoại &quot;{sdt}&quot;. Điền thông tin bên dưới để thêm mới.
        </p>
      )}

      <KhachThueForm
        khach={
          khach
            ? { ...khach, ngaySinh: khach.ngaySinh?.slice(0, 10) ?? null }
            : sdt
              ? { soDienThoai: sdt, hoTen: "", ngaySinh: null, soCCCD: "", anhCCCD: null, queQuan: null, noiLamViec: null }
              : undefined
        }
      />

      {khach && (
        <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
          <div className="border-b border-zinc-200 px-4 py-2 text-sm font-semibold text-zinc-900">
            Lịch sử thuê — {khach.hoTen} (mã khách: {khach.maKhach})
          </div>
          <table className="w-full text-sm">
            <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
              <tr>
                <th className="px-4 py-2">Mã hợp đồng</th>
                <th className="px-4 py-2">Phòng</th>
                <th className="px-4 py-2">Từ ngày</th>
                <th className="px-4 py-2">Đến ngày</th>
                <th className="px-4 py-2">Giá thuê</th>
                <th className="px-4 py-2">Trạng thái</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-100">
              {lichSuThue.map((hd) => (
                <tr key={hd.maHopDong}>
                  <td className="px-4 py-2 font-medium text-zinc-900">
                    <Link href={`/cn/hop-dong/${hd.maHopDong}`} className="hover:underline">
                      {hd.maHopDong}
                    </Link>
                  </td>
                  <td className="px-4 py-2">{hd.maPhong}</td>
                  <td className="px-4 py-2">{formatNgay(hd.ngayBatDau)}</td>
                  <td className="px-4 py-2">{formatNgay(hd.ngayHetHan)}</td>
                  <td className="px-4 py-2">{formatTien(hd.giaThue)}</td>
                  <td className="px-4 py-2">{TEN_TRANG_THAI_HOP_DONG[hd.trangThai]}</td>
                </tr>
              ))}
              {lichSuThue.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-4 py-6 text-center text-zinc-500">
                    Khách chưa có hợp đồng nào.
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
