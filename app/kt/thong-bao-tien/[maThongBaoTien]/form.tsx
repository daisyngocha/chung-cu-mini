"use client";

import { useActionState } from "react";
import { baoDaChuyenKhoan, type BaoChuyenKhoanFormState } from "./actions";

const initialState: BaoChuyenKhoanFormState = {};

export function BaoDaChuyenKhoanForm({ maThongBaoTien }: { maThongBaoTien: string }) {
  const [state, formAction, isPending] = useActionState(
    baoDaChuyenKhoan.bind(null, maThongBaoTien),
    initialState,
  );

  if (state.thanhCong) {
    return (
      <p className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">
        Đã báo cho chủ nhà. Chờ xác nhận.
      </p>
    );
  }

  return (
    <form action={formAction} className="flex flex-wrap items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4">
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Ảnh biên lai (URL, tùy chọn)</label>
        <input
          name="anhBienLai"
          className="w-64 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang gửi..." : "Tôi đã chuyển khoản"}
      </button>
      {state.error && (
        <p className="w-full rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
    </form>
  );
}
