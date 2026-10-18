import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgayGio, TEN_LOAI_SU_CO, TEN_TRANG_THAI_SU_CO } from "@/lib/format";
import { MoLaiForm } from "./form";

type SuCoApi = {
  maSuCo: string;
  loaiSuCo: string;
  moTa: string;
  anh: string | null;
  ngayBao: string;
  lichHen: string | null;
  ketQua: string | null;
  chiPhi: number;
  doKhachGay: boolean;
  trangThai: "MOI" | "DANG_XU_LY" | "DA_XU_LY" | "TU_CHOI" | "MO_LAI";
  lyDoTuChoi: string | null;
};

type LichSuApi = {
  maLichSu: string;
  thoiDiem: string;
  trangThaiCu: string | null;
  trangThaiMoi: string;
  vaiTroThucHien: "CHU_NHA" | "KHACH_THUE";
  ghiChu: string | null;
};

export default async function KhachSuCoDetailPage({
  params,
}: {
  params: Promise<{ maSuCo: string }>;
}) {
  const { maSuCo } = await params;
  const token = await getSessionToken();
  const [suCo, lichSu] = await Promise.all([
    goiBackend<SuCoApi>(`/api/khach/su-co/${maSuCo}`, { token }),
    goiBackend<LichSuApi[]>(`/api/khach/su-co/${maSuCo}/lich-su`, { token }),
  ]);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/kt/su-co" className="text-sm text-zinc-500 hover:underline">
          ← Sự cố của tôi
        </Link>
        <h1 className="mt-1 text-xl font-semibold text-zinc-900">
          {TEN_LOAI_SU_CO[suCo.loaiSuCo] ?? suCo.loaiSuCo}
        </h1>
      </div>

      <div className="grid grid-cols-2 gap-x-6 gap-y-2 rounded-lg border border-zinc-200 bg-white p-4 text-sm sm:grid-cols-3">
        <InfoRow nhan="Trạng thái" giaTri={TEN_TRANG_THAI_SU_CO[suCo.trangThai] ?? suCo.trangThai} />
        <InfoRow nhan="Ngày báo" giaTri={formatNgayGio(suCo.ngayBao)} />
        <InfoRow nhan="Lịch hẹn" giaTri={formatNgayGio(suCo.lichHen)} />
        <InfoRow nhan="Kết quả" giaTri={suCo.ketQua ?? "—"} />
        <InfoRow nhan="Chi phí" giaTri={formatTien(suCo.chiPhi)} />
        {suCo.lyDoTuChoi && <InfoRow nhan="Lý do từ chối" giaTri={suCo.lyDoTuChoi} />}
      </div>

      <div className="rounded-lg border border-zinc-200 bg-white p-4 text-sm">
        <h3 className="mb-2 font-semibold text-zinc-900">Mô tả</h3>
        <p className="text-zinc-700">{suCo.moTa}</p>
        {suCo.anh && (
          <a href={suCo.anh} target="_blank" rel="noreferrer" className="mt-2 inline-block text-xs underline">
            Xem ảnh đã gửi
          </a>
        )}
      </div>

      {suCo.trangThai === "DA_XU_LY" && <MoLaiForm maSuCo={maSuCo} />}

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <div className="border-b border-zinc-200 px-4 py-2 text-sm font-semibold text-zinc-900">
          Lịch sử xử lý
        </div>
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Thời điểm</th>
              <th className="px-4 py-2">Trạng thái</th>
              <th className="px-4 py-2">Ghi chú</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {lichSu.map((l) => (
              <tr key={l.maLichSu}>
                <td className="px-4 py-2">{formatNgayGio(l.thoiDiem)}</td>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  {TEN_TRANG_THAI_SU_CO[l.trangThaiMoi] ?? l.trangThaiMoi}
                </td>
                <td className="px-4 py-2">{l.ghiChu ?? "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function InfoRow({ nhan, giaTri }: { nhan: string; giaTri: string }) {
  return (
    <div className="flex justify-between gap-2 sm:block">
      <dt className="text-xs text-zinc-500">{nhan}</dt>
      <dd className="font-medium text-zinc-900">{giaTri}</dd>
    </div>
  );
}
