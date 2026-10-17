import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import {
  formatTien,
  formatNgay,
  TEN_TRANG_THAI_TBT,
  TEN_LOAI_KHOAN_TBT,
} from "@/lib/format";
import { ThemDongForm, PhatHanhButton, HuyForm, GhiNhanThuForm } from "./forms";

type DongApi = {
  maDong: string;
  loaiKhoan: string;
  dienGiai: string;
  tuNgay: string | null;
  denNgay: string | null;
  soLuong: number;
  donGia: number;
  tyLeNgayO: number | null;
  thanhTien: number;
};

type ThongBaoTienApi = {
  maThongBaoTien: string;
  maHopDong: string;
  maPhong: string;
  loaiThongBao: "KY_HOP_DONG" | "HANG_THANG" | "THU_THEM_QUYET_TOAN";
  kyThang: string;
  ngayPhatHanh: string | null;
  hanThanhToan: string | null;
  noCu: number;
  tongTien: number;
  soDaThu: number;
  trangThai: string;
  lyDoHuy: string | null;
  dong: DongApi[];
};

export default async function ThongBaoTienDetailPage({
  params,
}: {
  params: Promise<{ maThongBaoTien: string }>;
}) {
  const { maThongBaoTien } = await params;
  const token = await getSessionToken();
  const tb = await goiBackend<ThongBaoTienApi>(`/api/thong-bao-tien/${maThongBaoTien}`, { token });

  let vietQR: string | null = null;
  if (tb.trangThai !== "NHAP" && tb.trangThai !== "DA_HUY") {
    try {
      const res = await goiBackend<{ url: string }>(`/api/thong-bao-tien/${maThongBaoTien}/viet-qr`, { token });
      vietQR = res.url;
    } catch {
      vietQR = null;
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/cn/thong-bao-tien" className="text-sm text-zinc-500 hover:underline">
          ← Thông báo tiền
        </Link>
        <h1 className="mt-1 text-xl font-semibold text-zinc-900">
          Thông báo {tb.maThongBaoTien} — phòng {tb.maPhong}
        </h1>
        <Link
          href={`/cn/phieu-thu?maThongBaoTien=${tb.maThongBaoTien}`}
          className="text-sm text-zinc-500 hover:underline"
        >
          Xem phiếu thu của thông báo này →
        </Link>
      </div>

      <div className="grid grid-cols-2 gap-x-6 gap-y-2 rounded-lg border border-zinc-200 bg-white p-4 text-sm sm:grid-cols-3">
        <InfoRow nhan="Hợp đồng" giaTri={tb.maHopDong} />
        <InfoRow nhan="Kỳ" giaTri={tb.kyThang} />
        <InfoRow nhan="Trạng thái" giaTri={TEN_TRANG_THAI_TBT[tb.trangThai] ?? tb.trangThai} />
        <InfoRow nhan="Ngày phát hành" giaTri={formatNgay(tb.ngayPhatHanh)} />
        <InfoRow nhan="Hạn thanh toán" giaTri={formatNgay(tb.hanThanhToan)} />
        <InfoRow nhan="Nợ cũ" giaTri={formatTien(tb.noCu)} />
        <InfoRow nhan="Tổng tiền" giaTri={formatTien(tb.tongTien)} />
        <InfoRow nhan="Đã thu" giaTri={formatTien(tb.soDaThu)} />
        <InfoRow nhan="Còn thiếu" giaTri={formatTien(tb.tongTien - tb.soDaThu)} />
        {tb.lyDoHuy && <InfoRow nhan="Lý do hủy" giaTri={tb.lyDoHuy} />}
      </div>

      {vietQR && (
        <div className="flex flex-col items-start gap-2 rounded-lg border border-zinc-200 bg-white p-4">
          <h3 className="text-sm font-semibold text-zinc-900">Mã VietQR</h3>
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img src={vietQR} alt="Mã VietQR thanh toán" className="h-56 w-56" />
        </div>
      )}

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <div className="border-b border-zinc-200 px-4 py-2 text-sm font-semibold text-zinc-900">
          Chi tiết các dòng
        </div>
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Loại khoản</th>
              <th className="px-4 py-2">Diễn giải</th>
              <th className="px-4 py-2">Từ ngày</th>
              <th className="px-4 py-2">Đến ngày</th>
              <th className="px-4 py-2">Số lượng</th>
              <th className="px-4 py-2">Đơn giá</th>
              <th className="px-4 py-2">Thành tiền</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {tb.dong.map((d) => (
              <tr key={d.maDong}>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  {TEN_LOAI_KHOAN_TBT[d.loaiKhoan] ?? d.loaiKhoan}
                </td>
                <td className="px-4 py-2">{d.dienGiai}</td>
                <td className="px-4 py-2">{formatNgay(d.tuNgay)}</td>
                <td className="px-4 py-2">{formatNgay(d.denNgay)}</td>
                <td className="px-4 py-2">{d.soLuong}</td>
                <td className="px-4 py-2">{formatTien(d.donGia)}</td>
                <td className="px-4 py-2">{formatTien(d.thanhTien)}</td>
              </tr>
            ))}
            {tb.dong.length === 0 && (
              <tr>
                <td colSpan={7} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có dòng nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {tb.trangThai === "NHAP" && (
        <>
          <ThemDongForm maThongBaoTien={maThongBaoTien} />
          <PhatHanhButton maThongBaoTien={maThongBaoTien} />
        </>
      )}

      {tb.trangThai !== "DA_HUY" && tb.trangThai !== "DA_THU_DU" && (
        <GhiNhanThuForm maThongBaoTien={maThongBaoTien} />
      )}

      {tb.trangThai !== "DA_HUY" && tb.trangThai !== "DA_THU_DU" && (
        <HuyForm maThongBaoTien={maThongBaoTien} />
      )}
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
