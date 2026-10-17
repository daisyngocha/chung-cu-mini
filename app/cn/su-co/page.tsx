import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgayGio, TEN_LOAI_SU_CO, TEN_TRANG_THAI_SU_CO } from "@/lib/format";

type SuCoApi = {
  maSuCo: string;
  maPhong: string;
  loaiSuCo: string;
  moTa: string;
  ngayBao: string;
  trangThai: string;
};

const MAU_TRANG_THAI: Record<string, string> = {
  MOI: "bg-amber-100 text-amber-700",
  DANG_XU_LY: "bg-blue-100 text-blue-700",
  DA_XU_LY: "bg-green-100 text-green-700",
  TU_CHOI: "bg-zinc-100 text-zinc-500",
  MO_LAI: "bg-red-100 text-red-700",
};

export default async function SuCoPage({
  searchParams,
}: {
  searchParams: Promise<{ dangMo?: string }>;
}) {
  const { dangMo } = await searchParams;
  const token = await getSessionToken();
  const danhSach = await goiBackend<SuCoApi[]>(
    dangMo ? "/api/su-co/dang-mo" : "/api/su-co",
    { token },
  );

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Sự cố</h1>

      <div className="flex gap-2 text-sm">
        <Link
          href="/cn/su-co"
          className={`rounded-md px-3 py-1.5 font-medium ${!dangMo ? "bg-zinc-900 text-white" : "border border-zinc-300 text-zinc-700"}`}
        >
          Tất cả
        </Link>
        <Link
          href="/cn/su-co?dangMo=1"
          className={`rounded-md px-3 py-1.5 font-medium ${dangMo ? "bg-zinc-900 text-white" : "border border-zinc-300 text-zinc-700"}`}
        >
          Đang mở
        </Link>
      </div>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Mã sự cố</th>
              <th className="px-4 py-2">Phòng</th>
              <th className="px-4 py-2">Loại</th>
              <th className="px-4 py-2">Mô tả</th>
              <th className="px-4 py-2">Ngày báo</th>
              <th className="px-4 py-2">Trạng thái</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {danhSach.map((sc) => (
              <tr key={sc.maSuCo}>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  <Link href={`/cn/su-co/${sc.maSuCo}`} className="hover:underline">
                    {sc.maSuCo}
                  </Link>
                </td>
                <td className="px-4 py-2">{sc.maPhong}</td>
                <td className="px-4 py-2">{TEN_LOAI_SU_CO[sc.loaiSuCo] ?? sc.loaiSuCo}</td>
                <td className="px-4 py-2 max-w-xs truncate">{sc.moTa}</td>
                <td className="px-4 py-2">{formatNgayGio(sc.ngayBao)}</td>
                <td className="px-4 py-2">
                  <span className={"rounded-full px-2 py-0.5 text-xs font-medium " + (MAU_TRANG_THAI[sc.trangThai] ?? "")}>
                    {TEN_TRANG_THAI_SU_CO[sc.trangThai] ?? sc.trangThai}
                  </span>
                </td>
              </tr>
            ))}
            {danhSach.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-zinc-500">
                  Không có sự cố nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
