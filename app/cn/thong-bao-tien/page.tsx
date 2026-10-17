import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { LapHangLoatForm } from "./lap-hang-loat-form";
import { BangTongHop, type TongHopKyApi } from "./bang-tong-hop";

function kyThangMacDinh() {
  const now = new Date();
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, "0")}`;
}

export default async function ThongBaoTienPage({
  searchParams,
}: {
  searchParams: Promise<{ kyThang?: string }>;
}) {
  const { kyThang: kyThangParam } = await searchParams;
  const kyThang = kyThangParam || kyThangMacDinh();
  const token = await getSessionToken();
  const danhSach = await goiBackend<TongHopKyApi[]>(
    `/api/thong-bao-tien?kyThang=${encodeURIComponent(kyThang)}`,
    { token },
  );

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Thông báo tiền</h1>

      <form method="GET" className="flex items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Kỳ tháng</label>
          <input
            name="kyThang"
            type="month"
            defaultValue={kyThang}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Xem kỳ
        </button>
      </form>

      <LapHangLoatForm kyThang={kyThang} />

      <BangTongHop danhSach={danhSach} />
    </div>
  );
}
