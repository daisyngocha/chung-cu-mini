"use client";

import { useActionState, useRef, useEffect } from "react";
import { guiThongBaoChung, type GuiThongBaoChungFormState } from "./actions";

const initialState: GuiThongBaoChungFormState = {};

export function GuiThongBaoChungForm() {
  const [state, formAction, isPending] = useActionState(guiThongBaoChung, initialState);
  const formRef = useRef<HTMLFormElement>(null);

  useEffect(() => {
    if (state.thanhCong) formRef.current?.reset();
  }, [state]);

  return (
    <form
      ref={formRef}
      action={formAction}
      className="flex flex-col gap-3 rounded-lg border border-zinc-200 bg-white p-4"
    >
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Tiêu đề</label>
        <input
          name="tieuDe"
          required
          className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Nội dung</label>
        <textarea
          name="noiDung"
          required
          rows={4}
          className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      <button
        type="submit"
        disabled={isPending}
        className="self-start rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang gửi..." : "Gửi cho toàn bộ khách thuê"}
      </button>
      {state.error && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
      {state.thanhCong && (
        <p className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">
          Đã gửi thông báo cho toàn bộ khách thuê đang ở.
        </p>
      )}
    </form>
  );
}
