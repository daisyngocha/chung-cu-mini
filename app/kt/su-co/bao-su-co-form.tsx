"use client";

import { useActionState, useRef, useEffect } from "react";
import { baoSuCo, type BaoSuCoFormState } from "./actions";

const initialState: BaoSuCoFormState = {};

export function BaoSuCoForm() {
  const [state, formAction, isPending] = useActionState(baoSuCo, initialState);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (!state.error && !isPending) formRef.current?.reset();
  }, [state, isPending]);

  return (
    <form
      ref={formRef}
      action={formAction}
      className="flex flex-wrap items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4"
    >
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Loại sự cố</label>
        <select name="loaiSuCo" required className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm">
          <option value="DIEN">Điện</option>
          <option value="NUOC">Nước</option>
          <option value="THIET_BI">Thiết bị</option>
          <option value="INTERNET">Internet</option>
          <option value="KHAC">Khác</option>
        </select>
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Mô tả</label>
        <input name="moTa" required className="w-64 rounded-md border border-zinc-300 px-2 py-1.5 text-sm" />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Ảnh (URL, tùy chọn)</label>
        <input name="anh" className="w-48 rounded-md border border-zinc-300 px-2 py-1.5 text-sm" />
      </div>
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang gửi..." : "Báo sự cố"}
      </button>
      {state.error && (
        <p className="w-full rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
    </form>
  );
}
