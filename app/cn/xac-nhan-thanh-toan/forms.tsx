"use client";

import { useActionState } from "react";
import { xacNhanBaoThanhToan, tuChoiBaoThanhToan, type BaoThanhToanFormState } from "./actions";

const initialState: BaoThanhToanFormState = {};

export function XacNhanForm({ maBaoTT }: { maBaoTT: string }) {
  const [state, formAction, isPending] = useActionState(
    xacNhanBaoThanhToan.bind(null, maBaoTT),
    initialState,
  );
  return (
    <form action={formAction} className="flex items-center gap-2">
      <input
        name="soTienThucNhan"
        type="number"
        step="1"
        required
        placeholder="Số tiền thực nhận"
        className="w-36 rounded-md border border-zinc-300 px-2 py-1 text-xs"
      />
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md bg-zinc-900 px-2 py-1 text-xs font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang xác nhận..." : "Xác nhận"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
    </form>
  );
}

export function TuChoiForm({ maBaoTT }: { maBaoTT: string }) {
  const [state, formAction, isPending] = useActionState(
    tuChoiBaoThanhToan.bind(null, maBaoTT),
    initialState,
  );
  return (
    <form action={formAction} className="flex items-center gap-2">
      <input
        name="lyDo"
        required
        placeholder="Lý do từ chối"
        className="w-40 rounded-md border border-zinc-300 px-2 py-1 text-xs"
      />
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md border border-red-300 px-2 py-1 text-xs font-medium text-red-700 hover:bg-red-50 disabled:opacity-60"
      >
        {isPending ? "Đang từ chối..." : "Từ chối"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
    </form>
  );
}
