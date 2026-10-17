"use client";

import { useActionState, useRef, useEffect } from "react";
import { taoPhong, type PhongFormState } from "./actions";

const initialState: PhongFormState = {};

export function CreatePhongForm() {
  const [state, formAction, isPending] = useActionState(
    taoPhong,
    initialState,
  );
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (!state.error && !isPending) formRef.current?.reset();
  }, [state, isPending]);

  return (
    <form
      ref={formRef}
      action={formAction}
      className="mb-6 flex flex-wrap items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4"
    >
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Mã phòng</label>
        <input
          name="maPhong"
          required
          placeholder="101"
          className="w-24 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Tầng</label>
        <input
          name="tang"
          type="number"
          required
          className="w-20 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">
          Diện tích (m²)
        </label>
        <input
          name="dienTich"
          type="number"
          step="0.01"
          required
          className="w-24 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">
          Số người tối đa
        </label>
        <input
          name="soNguoiToiDa"
          type="number"
          required
          className="w-20 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">
          Giá niêm yết (đ/tháng)
        </label>
        <input
          name="giaNiemYet"
          type="number"
          step="1000"
          required
          className="w-32 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <label className="mb-1.5 flex items-center gap-2 text-sm text-zinc-700">
        <input
          name="coCongToNuoc"
          type="checkbox"
          defaultChecked
          className="h-4 w-4"
        />
        Có công tơ nước riêng
      </label>
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang thêm..." : "Thêm phòng"}
      </button>

      {state.error && (
        <p className="w-full rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">
          {state.error}
        </p>
      )}
    </form>
  );
}
