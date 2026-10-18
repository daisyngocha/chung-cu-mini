import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgayGio } from "@/lib/format";
import { danhDauDaDoc } from "./actions";

type ThongBaoApi = {
  maThongBao: string;
  tieuDe: string;
  noiDung: string;
  ngayTao: string;
  daDoc: boolean;
};

export default async function KhachThongBaoPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<ThongBaoApi[]>("/api/thong-bao", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Hộp thông báo</h1>

      <div className="flex flex-col gap-3">
        {danhSach.map((tb) => (
          <div
            key={tb.maThongBao}
            className={
              "flex items-start justify-between gap-4 rounded-lg border p-4 " +
              (tb.daDoc ? "border-zinc-200 bg-white" : "border-blue-200 bg-blue-50")
            }
          >
            <div>
              <p className="text-sm font-semibold text-zinc-900">{tb.tieuDe}</p>
              <p className="mt-1 text-sm text-zinc-700">{tb.noiDung}</p>
              <p className="mt-1 text-xs text-zinc-500">{formatNgayGio(tb.ngayTao)}</p>
            </div>
            {!tb.daDoc && (
              <form action={danhDauDaDoc.bind(null, tb.maThongBao)}>
                <button type="submit" className="whitespace-nowrap text-xs font-medium text-zinc-600 hover:underline">
                  Đánh dấu đã đọc
                </button>
              </form>
            )}
          </div>
        ))}
        {danhSach.length === 0 && (
          <p className="rounded-lg border border-zinc-200 bg-white p-4 text-center text-sm text-zinc-500">
            Chưa có thông báo nào.
          </p>
        )}
      </div>
    </div>
  );
}
