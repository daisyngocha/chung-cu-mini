"use client";

import { useActionState } from "react";
import { doiDonGia, type BangGiaFormState } from "./actions";

const initialState: BangGiaFormState = {};

export function DoiDonGiaForm({ maKhoan, loaiKhoan }: { maKhoan: string; loaiKhoan: string }) {
  const [state, formAction, isPending] = useActionState(
    doiDonGia.bind(null, maKhoan, loaiKhoan),
    initialState,
  );

  return (
    <form action={formAction} className="flex items-center justify-end gap-2">
      <input
        name="giaMoi"
        type="number"
        step="1"
        required
        placeholder="Đơn giá mới"
        className="w-28 rounded-md border border-zinc-300 px-2 py-1 text-xs"
      />
      <input
        name="ngayApDung"
        type="date"
        required
        className="rounded-md border border-zinc-300 px-2 py-1 text-xs"
      />
      <button type="submit" disabled={isPending} className="text-xs font-medium text-zinc-600 hover:underline">
        {isPending ? "Đang lưu..." : "Đổi đơn giá"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
    </form>
  );
}
