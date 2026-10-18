"use client";

import { useActionState, useState } from "react";
import { baoTraPhong, phanHoiQuyetToan, type TraPhongFormState } from "./actions";

const initialState: TraPhongFormState = {};

export function BaoTraPhongForm({ ngayHetHan }: { ngayHetHan: string }) {
  const [state, formAction, isPending] = useActionState(baoTraPhong, initialState);
  const [ngay, setNgay] = useState("");
  const truocHan = ngay && ngay < ngayHetHan;

  return (
    <form action={formAction} className="flex flex-col items-start gap-3 rounded-lg border border-zinc-200 bg-white p-4">
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Ngày trả dự kiến</label>
        <input
          name="ngayTraDuKien"
          type="date"
          required
          value={ngay}
          onChange={(e) => setNgay(e.target.value)}
          className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
        />
      </div>
      {truocHan && (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Ngày này trước hạn kết thúc hợp đồng — có thể bị áp dụng mức phạt trả trước hạn theo hợp đồng.
        </p>
      )}
      <button
        type="submit"
        disabled={isPending}
        className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang gửi..." : "Báo trả phòng"}
      </button>
      {state.error && <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>}
    </form>
  );
}

export function PhanHoiQuyetToanForm({ maQuyetToan }: { maQuyetToan: string }) {
  const [state, formAction, isPending] = useActionState(
    phanHoiQuyetToan.bind(null, maQuyetToan),
    initialState,
  );

  return (
    <form action={formAction} className="flex flex-col gap-3 rounded-lg border border-zinc-200 bg-white p-4">
      <div className="flex flex-col gap-1">
        <label className="text-xs font-medium text-zinc-600">Ý kiến (bắt buộc nếu không đồng ý)</label>
        <textarea name="yKien" rows={3} className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm" />
      </div>
      <div className="flex gap-3">
        <button
          type="submit"
          name="dongY"
          value="true"
          disabled={isPending}
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
        >
          Đồng ý
        </button>
        <button
          type="submit"
          name="dongY"
          value="false"
          disabled={isPending}
          className="rounded-md border border-red-300 px-3 py-1.5 text-sm font-medium text-red-700 hover:bg-red-50 disabled:opacity-60"
        >
          {isPending ? "Đang gửi..." : "Không đồng ý"}
        </button>
      </div>
      {state.error && <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>}
    </form>
  );
}
