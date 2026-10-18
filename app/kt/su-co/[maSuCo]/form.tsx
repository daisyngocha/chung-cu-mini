"use client";

import { useActionState } from "react";
import { moLaiSuCo, type MoLaiFormState } from "./actions";

const initialState: MoLaiFormState = {};

export function MoLaiForm({ maSuCo }: { maSuCo: string }) {
  const [state, formAction, isPending] = useActionState(moLaiSuCo.bind(null, maSuCo), initialState);

  return (
    <form action={formAction} className="flex flex-wrap items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Vì sao chưa được khắc phục?</label>
        <input name="ghiChu" required className="w-64 rounded-md border border-zinc-300 px-2 py-1.5 text-sm" />
      </div>
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md border border-red-300 px-3 py-1.5 text-sm font-medium text-red-700 hover:bg-red-50 disabled:opacity-60"
      >
        {isPending ? "Đang gửi..." : "Mở lại sự cố"}
      </button>
      {state.error && (
        <p className="w-full rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
    </form>
  );
}
