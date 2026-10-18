import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgayGio, TEN_LOAI_SU_CO, TEN_TRANG_THAI_SU_CO } from "@/lib/format";
import { BaoSuCoForm } from "./bao-su-co-form";

type SuCoApi = {
  maSuCo: string;
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

export default async function KhachSuCoPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<SuCoApi[]>("/api/khach/su-co", { token });
  const sapXep = [...danhSach].sort((a, b) => b.ngayBao.localeCompare(a.ngayBao));

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Sự cố của tôi</h1>

      <BaoSuCoForm />

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Loại</th>
              <th className="px-4 py-2">Mô tả</th>
              <th className="px-4 py-2">Ngày báo</th>
              <th className="px-4 py-2">Trạng thái</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {sapXep.map((sc) => (
              <tr key={sc.maSuCo}>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  <Link href={`/kt/su-co/${sc.maSuCo}`} className="hover:underline">
                    {TEN_LOAI_SU_CO[sc.loaiSuCo] ?? sc.loaiSuCo}
                  </Link>
                </td>
                <td className="px-4 py-2 max-w-xs truncate">{sc.moTa}</td>
                <td className="px-4 py-2">{formatNgayGio(sc.ngayBao)}</td>
                <td className="px-4 py-2">
                  <span className={"rounded-full px-2 py-0.5 text-xs font-medium " + (MAU_TRANG_THAI[sc.trangThai] ?? "")}>
                    {TEN_TRANG_THAI_SU_CO[sc.trangThai] ?? sc.trangThai}
                  </span>
                </td>
              </tr>
            ))}
            {sapXep.length === 0 && (
              <tr>
                <td colSpan={4} className="px-4 py-6 text-center text-zinc-500">
                  Bạn chưa báo sự cố nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
