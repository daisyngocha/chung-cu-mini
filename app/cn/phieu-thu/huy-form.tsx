"use client";

import { useActionState } from "react";
import { huyPhieuThu, type PhieuThuFormState } from "./actions";

const initialState: PhieuThuFormState = {};

export function HuyForm({ soPhieuThu, maThongBaoTien }: { soPhieuThu: string; maThongBaoTien: string }) {
  const [state, formAction, isPending] = useActionState(
    huyPhieuThu.bind(null, soPhieuThu, maThongBaoTien),
    initialState,
  );
  return (
    <form action={formAction} className="flex items-center gap-2">
      <input
        name="lyDo"
        required
        placeholder="Lý do hủy"
        className="w-36 rounded-md border border-zinc-300 px-2 py-1 text-xs"
      />
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md border border-red-300 px-2 py-1 text-xs font-medium text-red-700 hover:bg-red-50 disabled:opacity-60"
      >
        {isPending ? "Đang hủy..." : "Hủy"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
    </form>
  );
}
