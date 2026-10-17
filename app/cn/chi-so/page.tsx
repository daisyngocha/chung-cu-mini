import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgay, TEN_LOAI_CONG_TO, TEN_LOAI_GHI } from "@/lib/format";
import { GhiChiSoForm } from "./ghi-chi-so-form";

type HopDongSummaryApi = {
  maHopDong: string;
  maPhong: string;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

type PhongApi = { maPhong: string; coCongToNuoc: boolean };

type ChiSoApi = {
  maChiSo: string;
  loaiCongTo: "DIEN" | "NUOC";
  kyThang: string;
  loaiGhi: "DAU_KY" | "HANG_THANG" | "CUOI_KY";
  ngayGhi: string;
  chiSoCu: number;
  chiSoMoi: number;
  tieuThu: number;
};

export default async function ChiSoPage({
  searchParams,
}: {
  searchParams: Promise<{ maHopDong?: string }>;
}) {
  const { maHopDong } = await searchParams;
  const token = await getSessionToken();
  const [danhSachHopDong, danhSachPhong] = await Promise.all([
    goiBackend<HopDongSummaryApi[]>("/api/hop-dong", { token }),
    goiBackend<PhongApi[]>("/api/phong", { token }),
  ]);
  const dsHopDongDangThue = danhSachHopDong.filter(
    (hd) => hd.trangThai === "CHO_NHAN_PHONG" || hd.trangThai === "HIEU_LUC",
  );
  const hopDongChon = maHopDong ? dsHopDongDangThue.find((hd) => hd.maHopDong === maHopDong) : undefined;
  const phongChon = hopDongChon ? danhSachPhong.find((p) => p.maPhong === hopDongChon.maPhong) : undefined;

  const dsChiSo = hopDongChon
    ? await goiBackend<ChiSoApi[]>(`/api/hop-dong/${hopDongChon.maHopDong}/chi-so`, { token })
    : [];

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Ghi chỉ số điện, nước</h1>

      <form method="GET" className="flex items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Hợp đồng / phòng</label>
          <select
            name="maHopDong"
            defaultValue={maHopDong ?? ""}
            className="w-56 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          >
            <option value="">— chọn hợp đồng —</option>
            {dsHopDongDangThue.map((hd) => (
              <option key={hd.maHopDong} value={hd.maHopDong}>
                {hd.maPhong} ({hd.maHopDong})
              </option>
            ))}
          </select>
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Xem
        </button>
      </form>

      {hopDongChon && phongChon && (
        <>
          <GhiChiSoForm maHopDong={hopDongChon.maHopDong} coCongToNuoc={phongChon.coCongToNuoc} />

          <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
            <div className="border-b border-zinc-200 px-4 py-2 text-sm font-semibold text-zinc-900">
              Lịch sử ghi chỉ số — phòng {hopDongChon.maPhong}
            </div>
            <table className="w-full text-sm">
              <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
                <tr>
                  <th className="px-4 py-2">Loại công tơ</th>
                  <th className="px-4 py-2">Kỳ</th>
                  <th className="px-4 py-2">Loại ghi</th>
                  <th className="px-4 py-2">Ngày ghi</th>
                  <th className="px-4 py-2">Chỉ số cũ</th>
                  <th className="px-4 py-2">Chỉ số mới</th>
                  <th className="px-4 py-2">Tiêu thụ</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100">
                {dsChiSo.map((cs) => (
                  <tr key={cs.maChiSo}>
                    <td className="px-4 py-2 font-medium text-zinc-900">{TEN_LOAI_CONG_TO[cs.loaiCongTo]}</td>
                    <td className="px-4 py-2">{cs.kyThang}</td>
                    <td className="px-4 py-2">{TEN_LOAI_GHI[cs.loaiGhi]}</td>
                    <td className="px-4 py-2">{formatNgay(cs.ngayGhi)}</td>
                    <td className="px-4 py-2">{cs.chiSoCu}</td>
                    <td className="px-4 py-2">{cs.chiSoMoi}</td>
                    <td className="px-4 py-2">{cs.tieuThu}</td>
                  </tr>
                ))}
                {dsChiSo.length === 0 && (
                  <tr>
                    <td colSpan={7} className="px-4 py-6 text-center text-zinc-500">
                      Chưa ghi chỉ số nào cho hợp đồng này.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </>
      )}
    </div>
  );
}
