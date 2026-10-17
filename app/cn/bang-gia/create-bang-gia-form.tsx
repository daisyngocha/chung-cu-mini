"use client";

import { useActionState, useRef, useEffect } from "react";
import { taoKhoan, type BangGiaFormState } from "./actions";

const initialState: BangGiaFormState = {};

export function CreateBangGiaForm({
  loaiKhoanOptions,
}: {
  loaiKhoanOptions: { value: string; label: string }[];
}) {
  const [state, formAction, isPending] = useActionState(taoKhoan, initialState);
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
        <label className="text-xs font-medium text-zinc-600">Tên khoản</label>
        <input
          name="tenKhoan"
          required
          placeholder="Tiền điện"
          className="w-40 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Loại khoản</label>
        <select
          name="loaiKhoan"
          required
          className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        >
          <option value="">— chọn —</option>
          {loaiKhoanOptions.map((o) => (
            <option key={o.value} value={o.value}>
              {o.label}
            </option>
          ))}
        </select>
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Đơn giá</label>
        <input
          name="donGia"
          type="number"
          step="1"
          required
          className="w-32 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Đơn vị tính</label>
        <input
          name="donViTinh"
          required
          placeholder="đ/kWh"
          className="w-28 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Ngày áp dụng</label>
        <input
          name="ngayApDung"
          type="date"
          required
          className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang thêm..." : "Thêm khoản"}
      </button>

      {state.error && (
        <p className="w-full rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">
          {state.error}
        </p>
      )}
    </form>
  );
}
