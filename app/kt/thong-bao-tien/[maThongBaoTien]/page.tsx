import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import {
  formatTien,
  formatNgay,
  TEN_TRANG_THAI_TBT,
  TEN_LOAI_KHOAN_TBT,
} from "@/lib/format";
import { BaoDaChuyenKhoanForm } from "./form";

type DongApi = {
  maDong: string;
  loaiKhoan: string;
  dienGiai: string;
  soLuong: number;
  donGia: number;
  thanhTien: number;
};

type ThongBaoTienApi = {
  maThongBaoTien: string;
  kyThang: string;
  hanThanhToan: string | null;
  noCu: number;
  tongTien: number;
  soDaThu: number;
  trangThai: string;
  dong: DongApi[];
};

export default async function KhachThongBaoTienDetailPage({
  params,
}: {
  params: Promise<{ maThongBaoTien: string }>;
}) {
  const { maThongBaoTien } = await params;
  const token = await getSessionToken();
  const tb = await goiBackend<ThongBaoTienApi>(`/api/khach/thong-bao-tien/${maThongBaoTien}`, { token });

  let vietQR: string | null = null;
  const choThanhToan = ["DA_PHAT_HANH", "DA_THU_MOT_PHAN", "QUA_HAN"].includes(tb.trangThai);
  if (choThanhToan) {
    try {
      const res = await goiBackend<{ url: string }>(`/api/khach/thong-bao-tien/${maThongBaoTien}/viet-qr`, { token });
      vietQR = res.url;
    } catch {
      vietQR = null;
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/kt/thong-bao-tien" className="text-sm text-zinc-500 hover:underline">
          ← Thông báo tiền
        </Link>
        <h1 className="mt-1 text-xl font-semibold text-zinc-900">Thông báo tiền kỳ {tb.kyThang}</h1>
      </div>

      <div className="grid grid-cols-2 gap-x-6 gap-y-2 rounded-lg border border-zinc-200 bg-white p-4 text-sm sm:grid-cols-3">
        <InfoRow nhan="Trạng thái" giaTri={TEN_TRANG_THAI_TBT[tb.trangThai] ?? tb.trangThai} />
        <InfoRow nhan="Hạn thanh toán" giaTri={formatNgay(tb.hanThanhToan)} />
        <InfoRow nhan="Nợ cũ" giaTri={formatTien(tb.noCu)} />
        <InfoRow nhan="Tổng tiền" giaTri={formatTien(tb.tongTien)} />
        <InfoRow nhan="Đã thu" giaTri={formatTien(tb.soDaThu)} />
        <InfoRow nhan="Còn thiếu" giaTri={formatTien(tb.tongTien - tb.soDaThu)} />
      </div>

      {vietQR && (
        <div className="flex flex-col items-start gap-2 rounded-lg border border-zinc-200 bg-white p-4">
          <h3 className="text-sm font-semibold text-zinc-900">Quét mã để thanh toán</h3>
          {/* eslint-disable-next-line @next/next/no-img-element */}
          <img src={vietQR} alt="Mã VietQR" className="h-56 w-56" />
        </div>
      )}

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Khoản</th>
              <th className="px-4 py-2">Diễn giải</th>
              <th className="px-4 py-2">Số lượng</th>
              <th className="px-4 py-2">Đơn giá</th>
              <th className="px-4 py-2">Thành tiền</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {tb.dong.map((d) => (
              <tr key={d.maDong}>
                <td className="px-4 py-2 font-medium text-zinc-900">{TEN_LOAI_KHOAN_TBT[d.loaiKhoan] ?? d.loaiKhoan}</td>
                <td className="px-4 py-2">{d.dienGiai}</td>
                <td className="px-4 py-2">{d.soLuong}</td>
                <td className="px-4 py-2">{formatTien(d.donGia)}</td>
                <td className="px-4 py-2">{formatTien(d.thanhTien)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {choThanhToan && <BaoDaChuyenKhoanForm maThongBaoTien={maThongBaoTien} />}
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
