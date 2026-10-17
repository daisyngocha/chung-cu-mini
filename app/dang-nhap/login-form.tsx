"use client";

import { useActionState } from "react";
import { dangNhap, type DangNhapState } from "./actions";

const initialState: DangNhapState = {};

export function LoginForm() {
  const [state, formAction, isPending] = useActionState(dangNhap, initialState);

  return (
    <form action={formAction} className="flex flex-col gap-4">
      <div className="flex flex-col gap-1">
        <label htmlFor="tenDangNhap" className="text-sm font-medium text-zinc-700">
          Tên đăng nhập
        </label>
        <input
          id="tenDangNhap"
          name="tenDangNhap"
          autoComplete="username"
          required
          className="rounded-md border border-zinc-300 px-3 py-2 text-sm focus:border-zinc-500 focus:outline-none"
        />
      </div>

      <div className="flex flex-col gap-1">
        <label htmlFor="matKhau" className="text-sm font-medium text-zinc-700">
          Mật khẩu
        </label>
        <input
          id="matKhau"
          name="matKhau"
          type="password"
          autoComplete="current-password"
          required
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
        {isPending ? "Đang đăng nhập..." : "Đăng nhập"}
      </button>
    </form>
  );
}
