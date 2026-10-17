import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatPhanTram } from "@/lib/format";

type TinhTrangPhongApi = {
  soLuongTheoTrangThai: Record<string, number>;
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

export default async function BaoCaoPage() {
  const token = await getSessionToken();
  const kyThang = kyThangHienTai();

  const [tinhTrangPhong, tyLeLapDay, doanhThu, tyLeThu, congNoQuaHan, cocDangGiu, hopDongSapHetHan, suCoDangMo] =
    await Promise.all([
      goiBackend<TinhTrangPhongApi>("/api/bao-cao/tinh-trang-phong", { token }),
      goiBackend<number>(`/api/bao-cao/ty-le-lap-day?kyThang=${kyThang}`, { token }),
      goiBackend<Record<string, number>>(`/api/bao-cao/doanh-thu?kyThang=${kyThang}`, { token }),
      goiBackend<number>(`/api/bao-cao/ty-le-thu?tuNgay=${dauThang()}&denNgay=${homNay()}`, { token }),
      goiBackend<unknown[]>(`/api/bao-cao/cong-no-qua-han`, { token }),
      goiBackend<unknown[]>("/api/bao-cao/coc-dang-giu", { token }),
      goiBackend<unknown[]>("/api/bao-cao/hop-dong-sap-het-han?soNgay=30", { token }),
      goiBackend<unknown[]>("/api/su-co/dang-mo", { token }),
    ]);

  const tongDoanhThu = Object.values(doanhThu).reduce((a, b) => a + b, 0);

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Tổng quan vận hành</h1>
      <p className="text-sm text-zinc-600">Kỳ {kyThang}</p>

      <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
        <StatCard nhan="Tỷ lệ lấp đầy" giaTri={formatPhanTram(tyLeLapDay)} />
        <StatCard nhan="Doanh thu kỳ này" giaTri={formatTien(tongDoanhThu)} />
        <StatCard nhan="Tỷ lệ thu (tháng này)" giaTri={formatPhanTram(tyLeThu)} />
        <StatCard nhan="Cọc đang giữ" giaTri={`${cocDangGiu.length} hợp đồng`} />
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div className="rounded-lg border border-zinc-200 bg-white p-4">
          <h3 className="mb-2 text-sm font-semibold text-zinc-900">Tình trạng phòng</h3>
          {Object.entries(tinhTrangPhong.soLuongTheoTrangThai).map(([trangThai, soLuong]) => (
            <p key={trangThai} className="text-sm text-zinc-700">
              {trangThai}: <strong>{soLuong}</strong>
            </p>
          ))}
        </div>
        <Link
          href="/cn/bao-cao/coc-hop-dong"
          className="rounded-lg border border-zinc-200 bg-white p-4 hover:bg-zinc-50"
        >
          <h3 className="mb-2 text-sm font-semibold text-zinc-900">Hợp đồng sắp hết hạn (30 ngày)</h3>
          <p className="text-2xl font-semibold text-zinc-900">{hopDongSapHetHan.length}</p>
        </Link>
        <Link href="/cn/bao-cao/su-co" className="rounded-lg border border-zinc-200 bg-white p-4 hover:bg-zinc-50">
          <h3 className="mb-2 text-sm font-semibold text-zinc-900">Sự cố đang mở</h3>
          <p className="text-2xl font-semibold text-zinc-900">{suCoDangMo.length}</p>
        </Link>
      </div>

      <Link
        href="/cn/bao-cao/doanh-thu"
        className="rounded-lg border border-zinc-200 bg-white p-4 hover:bg-zinc-50"
      >
        <h3 className="mb-1 text-sm font-semibold text-zinc-900">Công nợ quá hạn</h3>
        <p className="text-2xl font-semibold text-zinc-900">{congNoQuaHan.length} thông báo</p>
      </Link>
    </div>
  );
}

function StatCard({ nhan, giaTri }: { nhan: string; giaTri: string }) {
  return (
    <div className="rounded-lg border border-zinc-200 bg-white p-4">
      <p className="text-xs text-zinc-500">{nhan}</p>
      <p className="mt-1 text-xl font-semibold text-zinc-900">{giaTri}</p>
    </div>
  );
}
