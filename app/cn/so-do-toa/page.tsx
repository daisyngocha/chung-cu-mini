import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgay, TEN_TRANG_THAI_PHONG } from "@/lib/format";

type PhongApi = {
  maPhong: string;
  tang: number;
  trangThai: "TRONG" | "DANG_THUE" | "NGUNG_KHAI_THAC";
  sapTra: boolean;
  ngayTraDuKien: string | null;
};

const MAU_O: Record<PhongApi["trangThai"], string> = {
  TRONG: "border-green-300 bg-green-50 text-green-800",
  DANG_THUE: "border-blue-300 bg-blue-50 text-blue-800",
  NGUNG_KHAI_THAC: "border-zinc-300 bg-zinc-100 text-zinc-500",
};

export default async function SoDoToaPage({
  searchParams,
}: {
  searchParams: Promise<{ tang?: string; trangThai?: string }>;
}) {
  const { tang: tangLoc, trangThai: trangThaiLoc } = await searchParams;
  const token = await getSessionToken();
  const danhSach = await goiBackend<PhongApi[]>("/api/phong", { token });

  const dsLoc = danhSach.filter(
    (p) =>
      (!tangLoc || String(p.tang) === tangLoc) && (!trangThaiLoc || p.trangThai === trangThaiLoc),
  );
  const dsTang = Array.from(new Set(danhSach.map((p) => p.tang))).sort((a, b) => a - b);
  const theoTang = new Map<number, PhongApi[]>();
  for (const p of dsLoc) {
    theoTang.set(p.tang, [...(theoTang.get(p.tang) ?? []), p]);
  }

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Sơ đồ tòa</h1>

      <form method="GET" className="flex items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Tầng</label>
          <select name="tang" defaultValue={tangLoc ?? ""} className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm">
            <option value="">Tất cả</option>
            {dsTang.map((t) => (
              <option key={t} value={t}>
                Tầng {t}
              </option>
            ))}
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Trạng thái</label>
          <select
            name="trangThai"
            defaultValue={trangThaiLoc ?? ""}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          >
            <option value="">Tất cả</option>
            <option value="TRONG">Trống</option>
            <option value="DANG_THUE">Đang thuê</option>
            <option value="NGUNG_KHAI_THAC">Ngừng khai thác</option>
          </select>
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Lọc
        </button>
      </form>

      <div className="flex flex-col gap-6">
        {Array.from(theoTang.keys())
          .sort((a, b) => b - a)
          .map((tang) => (
            <div key={tang}>
              <h2 className="mb-2 text-sm font-semibold text-zinc-700">Tầng {tang}</h2>
              <div className="flex flex-wrap gap-3">
                {(theoTang.get(tang) ?? []).map((p) => (
                  <Link
                    key={p.maPhong}
                    href="/cn/phong"
                    className={`flex w-28 flex-col gap-1 rounded-lg border-2 p-3 text-sm font-medium hover:opacity-80 ${MAU_O[p.trangThai]}`}
                  >
                    <span className="text-base font-semibold">{p.maPhong}</span>
                    <span className="text-xs">{TEN_TRANG_THAI_PHONG[p.trangThai]}</span>
                    {p.sapTra && (
                      <span className="rounded-full bg-amber-200 px-1.5 py-0.5 text-[11px] font-semibold text-amber-800">
                        Sắp trả {formatNgay(p.ngayTraDuKien)}
                      </span>
                    )}
                  </Link>
                ))}
              </div>
            </div>
          ))}
        {dsLoc.length === 0 && (
          <p className="rounded-lg border border-zinc-200 bg-white p-4 text-center text-sm text-zinc-500">
            Không có phòng nào khớp bộ lọc.
          </p>
        )}
      </div>
    </div>
  );
}
