"use client";

import { useActionState } from "react";
import { doiMatKhau, type DoiMatKhauState } from "./actions";

const initialState: DoiMatKhauState = {};

export function DoiMatKhauForm() {
  const [state, formAction, isPending] = useActionState(
    doiMatKhau,
    initialState,
  );

  return (
    <form action={formAction} className="flex flex-col gap-4">
      <div className="flex flex-col gap-1">
        <label htmlFor="matKhauCu" className="text-sm font-medium text-zinc-700">
          Mật khẩu hiện tại
        </label>
        <input
          id="matKhauCu"
          name="matKhauCu"
          type="password"
          autoComplete="current-password"
          required
          className="rounded-md border border-zinc-300 px-3 py-2 text-sm focus:border-zinc-500 focus:outline-none"
        />
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="matKhauMoi" className="text-sm font-medium text-zinc-700">
          Mật khẩu mới
        </label>
        <input
          id="matKhauMoi"
          name="matKhauMoi"
          type="password"
          autoComplete="new-password"
          required
          minLength={6}
          className="rounded-md border border-zinc-300 px-3 py-2 text-sm focus:border-zinc-500 focus:outline-none"
        />
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="xacNhan" className="text-sm font-medium text-zinc-700">
          Xác nhận mật khẩu mới
        </label>
        <input
          id="xacNhan"
          name="xacNhan"
          type="password"
          autoComplete="new-password"
          required
          minLength={6}
          className="rounded-md border border-zinc-300 px-3 py-2 text-sm focus:border-zinc-500 focus:outline-none"
        />
      </div>

      {state.error && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">
          {state.error}
        </p>
      )}

      <button
        type="submit"
        disabled={isPending}
        className="mt-2 rounded-md bg-zinc-900 px-3 py-2 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang lưu..." : "Lưu mật khẩu mới"}
      </button>
    </form>
  );
}
