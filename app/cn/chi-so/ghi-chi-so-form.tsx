"use client";

import { useActionState } from "react";
import { ghiChiSo, type ChiSoFormState } from "./actions";

const initialState: ChiSoFormState = {};

export function GhiChiSoForm({ maHopDong, coCongToNuoc }: { maHopDong: string; coCongToNuoc: boolean }) {
  const [state, formAction, isPending] = useActionState(
    ghiChiSo.bind(null, maHopDong),
    initialState,
  );

  return (
    <form
      action={formAction}
      className="flex flex-wrap items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4"
    >
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Loại công tơ</label>
        <select name="loaiCongTo" required className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm">
          <option value="DIEN">Điện</option>
          {coCongToNuoc && <option value="NUOC">Nước</option>}
        </select>
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Loại ghi</label>
        <select name="loaiGhi" required className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm">
          <option value="HANG_THANG">Hàng tháng</option>
          <option value="DAU_KY">Đầu kỳ</option>
          <option value="CUOI_KY">Cuối kỳ</option>
        </select>
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Kỳ tháng</label>
        <input name="kyThang" type="month" required className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm" />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Chỉ số mới</label>
        <input
          name="chiSoMoi"
          type="number"
          min={0}
          required
          className="w-28 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Ảnh công tơ (URL)</label>
        <input name="anhCongTo" className="w-48 rounded-md border border-zinc-300 px-2 py-1.5 text-sm" />
      </div>
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang ghi..." : "Ghi chỉ số"}
      </button>
      {state.error && (
        <p className="w-full rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
    </form>
  );
}
