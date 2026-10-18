"use client";

import { useActionState } from "react";
import { datLaiMatKhau, type TaiKhoanKhachFormState } from "./actions";

const initialState: TaiKhoanKhachFormState = {};

export function DatLaiMatKhauForm({ maTaiKhoan }: { maTaiKhoan: string }) {
  const [state, formAction, isPending] = useActionState(datLaiMatKhau.bind(null, maTaiKhoan), initialState);

  return (
    <form action={formAction} className="flex flex-col items-end gap-1">
      <button type="submit" disabled={isPending} className="text-xs font-medium text-zinc-600 hover:underline">
        {isPending ? "Đang đặt lại..." : "Đặt lại mật khẩu"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
      {state.matKhauTam && (
        <span className="rounded bg-green-50 px-2 py-1 text-xs text-green-700">
          Mật khẩu tạm: <span className="font-mono">{state.matKhauTam}</span>
        </span>
      )}
    </form>
  );
}
