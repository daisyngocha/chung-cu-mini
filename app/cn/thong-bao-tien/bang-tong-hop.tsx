"use client";

import Link from "next/link";
import { useActionState } from "react";
import { formatTien, TEN_TRANG_THAI_TBT } from "@/lib/format";
import { phatHanhHangLoat, type PhatHanhHangLoatState } from "./actions";

export type TongHopKyApi = {
  maThongBaoTien: string;
  maPhong: string;
  dien: number;
  nuoc: number;
  dichVu: number;
  tienPhong: number;
  phatSinh: number;
  noCu: number;
  tongTien: number;
  trangThai: string;
};

const MAU_TRANG_THAI: Record<string, string> = {
  NHAP: "bg-zinc-100 text-zinc-600",
  DA_PHAT_HANH: "bg-blue-100 text-blue-700",
  CHO_XAC_NHAN: "bg-amber-100 text-amber-700",
  DA_THU_MOT_PHAN: "bg-amber-100 text-amber-700",
  DA_THU_DU: "bg-green-100 text-green-700",
  QUA_HAN: "bg-red-100 text-red-700",
  DA_HUY: "bg-zinc-100 text-zinc-400",
};

const initialState: PhatHanhHangLoatState = {};

export function BangTongHop({ danhSach }: { danhSach: TongHopKyApi[] }) {
  const [state, formAction, isPending] = useActionState(phatHanhHangLoat, initialState);
  const coNhap = danhSach.some((r) => r.trangThai === "NHAP");

  return (
    <form action={formAction} className="flex flex-col gap-3">
      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-3 py-2" />
              <th className="px-3 py-2">Phòng</th>
              <th className="px-3 py-2">Điện</th>
              <th className="px-3 py-2">Nước</th>
              <th className="px-3 py-2">Dịch vụ</th>
              <th className="px-3 py-2">Tiền phòng</th>
              <th className="px-3 py-2">Phát sinh</th>
              <th className="px-3 py-2">Nợ cũ</th>
              <th className="px-3 py-2">Tổng tiền</th>
              <th className="px-3 py-2">Trạng thái</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {danhSach.map((r) => (
              <tr key={r.maThongBaoTien}>
                <td className="px-3 py-2">
                  {r.trangThai === "NHAP" && (
                    <input type="checkbox" name="chon" value={r.maThongBaoTien} className="h-4 w-4" />
                  )}
                </td>
                <td className="px-3 py-2 font-medium text-zinc-900">
                  <Link href={`/cn/thong-bao-tien/${r.maThongBaoTien}`} className="hover:underline">
                    {r.maPhong}
                  </Link>
                </td>
                <td className="px-3 py-2">{formatTien(r.dien)}</td>
                <td className="px-3 py-2">{formatTien(r.nuoc)}</td>
                <td className="px-3 py-2">{formatTien(r.dichVu)}</td>
                <td className="px-3 py-2">{formatTien(r.tienPhong)}</td>
                <td className="px-3 py-2">{formatTien(r.phatSinh)}</td>
                <td className="px-3 py-2">{formatTien(r.noCu)}</td>
                <td className="px-3 py-2 font-medium">{formatTien(r.tongTien)}</td>
                <td className="px-3 py-2">
                  <span className={"rounded-full px-2 py-0.5 text-xs font-medium " + (MAU_TRANG_THAI[r.trangThai] ?? "")}>
                    {TEN_TRANG_THAI_TBT[r.trangThai] ?? r.trangThai}
                  </span>
                </td>
              </tr>
            ))}
            {danhSach.length === 0 && (
              <tr>
                <td colSpan={10} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có thông báo nào cho kỳ này. Bấm &quot;Lập thông báo hàng loạt&quot; ở trên.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {coNhap && (
        <button
          type="submit"
          disabled={isPending}
          className="self-start rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
        >
          {isPending ? "Đang phát hành..." : "Phát hành các thông báo đã chọn"}
        </button>
      )}
      {state.error && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
      {state.thanhCong && (
        <p className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">
          Đã phát hành: {state.thanhCong.join(", ") || "(không có)"}
        </p>
      )}
      {state.canhBaoHetHan && state.canhBaoHetHan.length > 0 && (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Hợp đồng sắp hết hạn: {state.canhBaoHetHan.join(", ")}
        </p>
      )}
      {state.loi && state.loi.length > 0 && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">Lỗi: {state.loi.join(", ")}</p>
      )}
    </form>
  );
}
