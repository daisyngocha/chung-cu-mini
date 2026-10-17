"use client";

import { useActionState } from "react";
import { ghiChiSo, type ChiSoFormState } from "@/app/cn/chi-so/actions";
import {
  ghiNhanTinhTrangTraPhong,
  tinhQuyetToan,
  type GhiNhanTinhTrangFormState,
  type TinhQuyetToanFormState,
} from "./actions";

const inputCls = "rounded-md border border-zinc-300 px-2 py-1.5 text-sm";
const labelCls = "text-xs font-medium text-zinc-600";
const btnCls =
  "rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60";
const errCls = "rounded-md bg-red-50 px-3 py-2 text-sm text-red-700";

export function GhiChiSoCuoiKyForm({
  maHopDong,
  loaiCongTo,
  kyThangMacDinh,
}: {
  maHopDong: string;
  loaiCongTo: "DIEN" | "NUOC";
  kyThangMacDinh: string;
}) {
  const initialState: ChiSoFormState = {};
  const [state, formAction, isPending] = useActionState(ghiChiSo.bind(null, maHopDong), initialState);
  return (
    <form action={formAction} className="flex flex-wrap items-end gap-3">
      <input type="hidden" name="loaiCongTo" value={loaiCongTo} />
      <input type="hidden" name="loaiGhi" value="CUOI_KY" />
      <div className="flex flex-col gap-1">
        <label className={labelCls}>Kỳ tháng</label>
        <input name="kyThang" type="month" defaultValue={kyThangMacDinh} required className={inputCls} />
      </div>
      <div className="flex flex-col gap-1">
        <label className={labelCls}>Chỉ số cuối ({loaiCongTo === "DIEN" ? "điện" : "nước"})</label>
        <input name="chiSoMoi" type="number" min={0} required className={`w-28 ${inputCls}`} />
      </div>
      <div className="flex flex-col gap-1">
        <label className={labelCls}>Ảnh công tơ (URL)</label>
        <input name="anhCongTo" className={`w-48 ${inputCls}`} />
      </div>
      <button type="submit" disabled={isPending} className={btnCls}>
        {isPending ? "Đang ghi..." : "Ghi chỉ số cuối"}
      </button>
      {state.error && <p className={`w-full ${errCls}`}>{state.error}</p>}
    </form>
  );
}

export function GhiNhanTaiSanTraPhongForm({
  maHopDong,
  maTaiSan,
  tenTaiSan,
}: {
  maHopDong: string;
  maTaiSan: string;
  tenTaiSan: string;
}) {
  const initialState: GhiNhanTinhTrangFormState = {};
  const [state, formAction, isPending] = useActionState(
    ghiNhanTinhTrangTraPhong.bind(null, maHopDong, maTaiSan),
    initialState,
  );
  return (
    <form action={formAction} className="flex flex-wrap items-end gap-2 border-t border-zinc-100 py-2 first:border-t-0">
      <span className="w-40 text-sm font-medium text-zinc-900">{tenTaiSan}</span>
      <input name="tinhTrang" placeholder="Tình trạng lúc trả" required className={`w-48 ${inputCls}`} />
      <input name="anh" placeholder="Ảnh bàn giao (URL)" className={`w-40 ${inputCls}`} />
      <input
        name="chiPhiHuHong"
        type="number"
        step="1"
        placeholder="Chi phí hư hỏng (nếu có)"
        className={`w-40 ${inputCls}`}
      />
      <button type="submit" disabled={isPending} className="text-xs font-medium text-zinc-600 hover:underline">
        {isPending ? "Đang lưu..." : "Ghi nhận"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
    </form>
  );
}

export function TinhQuyetToanButton({ maHopDong }: { maHopDong: string }) {
  const initialState: TinhQuyetToanFormState = {};
  const [state, formAction, isPending] = useActionState(tinhQuyetToan.bind(null, maHopDong), initialState);
  return (
    <form action={formAction} className="flex flex-col items-start gap-2">
      <button type="submit" disabled={isPending} className={btnCls}>
        {isPending ? "Đang tính..." : "Tính quyết toán cọc"}
      </button>
      {state.error && <p className={errCls}>{state.error}</p>}
    </form>
  );
}
