"use client";

import { useActionState } from "react";
import { tiepNhan, tuChoi, hoanThanh, type SuCoFormState } from "./actions";

const initialState: SuCoFormState = {};
const inputCls = "rounded-md border border-zinc-300 px-2 py-1.5 text-sm";
const labelCls = "text-xs font-medium text-zinc-600";
const btnCls =
  "rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60";
const errCls = "rounded-md bg-red-50 px-3 py-2 text-sm text-red-700";

function Card({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <div className="flex flex-col gap-3 rounded-lg border border-zinc-200 bg-white p-4">
      <h3 className="text-sm font-semibold text-zinc-900">{title}</h3>
      {children}
    </div>
  );
}

export function TiepNhanForm({ maSuCo }: { maSuCo: string }) {
  const [state, formAction, isPending] = useActionState(tiepNhan.bind(null, maSuCo), initialState);
  return (
    <Card title="Tiếp nhận xử lý">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Lịch hẹn</label>
          <input name="lichHen" type="datetime-local" required className={inputCls} />
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang tiếp nhận..." : "Tiếp nhận"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function TuChoiForm({ maSuCo }: { maSuCo: string }) {
  const [state, formAction, isPending] = useActionState(tuChoi.bind(null, maSuCo), initialState);
  return (
    <Card title="Từ chối sự cố">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Lý do</label>
          <input name="lyDo" required className={`w-64 ${inputCls}`} />
        </div>
        <button
          type="submit"
          disabled={isPending}
          className="rounded-md border border-red-300 px-3 py-1.5 text-sm font-medium text-red-700 hover:bg-red-50 disabled:opacity-60"
        >
          {isPending ? "Đang từ chối..." : "Từ chối"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function HoanThanhForm({ maSuCo }: { maSuCo: string }) {
  const [state, formAction, isPending] = useActionState(hoanThanh.bind(null, maSuCo), initialState);
  return (
    <Card title="Hoàn thành xử lý">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Kết quả</label>
          <input name="ketQua" required className={`w-64 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Chi phí</label>
          <input name="chiPhi" type="number" step="1" className={`w-32 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ảnh sau xử lý (URL)</label>
          <input name="anh" className={`w-48 ${inputCls}`} />
        </div>
        <label className="mb-1.5 flex items-center gap-2 text-sm text-zinc-700">
          <input name="doKhachGay" type="checkbox" className="h-4 w-4" />
          Do khách gây ra (cộng phí vào thông báo tiền)
        </label>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang lưu..." : "Hoàn thành"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}
