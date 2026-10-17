"use client";

import { useActionState } from "react";
import { lapHangLoat, type LapHangLoatState } from "./actions";

const initialState: LapHangLoatState = {};

export function LapHangLoatForm({ kyThang }: { kyThang: string }) {
  const [state, formAction, isPending] = useActionState(
    lapHangLoat.bind(null, kyThang),
    initialState,
  );

  return (
    <form action={formAction} className="flex flex-col gap-2">
      <button
        type="submit"
        disabled={isPending}
        className="self-start rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang lập..." : `Lập thông báo hàng loạt cho kỳ ${kyThang}`}
      </button>
      {state.error && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
      {state.daLap && (
        <p className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">
          Đã lập {state.daLap.length} thông báo: {state.daLap.join(", ") || "(không có)"}
        </p>
      )}
      {state.phongThieuChiSo && state.phongThieuChiSo.length > 0 && (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Phòng thiếu chỉ số, chưa lập được: {state.phongThieuChiSo.join(", ")}. Vào trang Ghi chỉ số
          để bổ sung rồi lập lại.
        </p>
      )}
    </form>
  );
}
