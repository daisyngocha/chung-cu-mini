"use client";

import { useActionState } from "react";
import { taoHopDong, type HopDongFormState } from "./actions";

type PhongTrong = { maPhong: string; giaNiemYet: number };

const initialState: HopDongFormState = {};

export function CreateHopDongForm({ dsPhongTrong }: { dsPhongTrong: PhongTrong[] }) {
  const [state, formAction, isPending] = useActionState(taoHopDong, initialState);

  return (
    <form
      action={formAction}
      className="flex flex-col gap-3 rounded-lg border border-zinc-200 bg-white p-4"
    >
      <h2 className="text-sm font-semibold text-zinc-900">Tạo hợp đồng mới</h2>
      <div className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Phòng</label>
          <select
            name="maPhong"
            required
            className="w-32 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          >
            <option value="">— chọn —</option>
            {dsPhongTrong.map((p) => (
              <option key={p.maPhong} value={p.maPhong}>
                {p.maPhong}
              </option>
            ))}
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Mã khách thuê</label>
          <input
            name="maKhach"
            required
            placeholder="vd. KH000123"
            className="w-36 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Ngày bắt đầu</label>
          <input
            name="ngayBatDau"
            type="date"
            required
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Ngày hết hạn</label>
          <input
            name="ngayHetHan"
            type="date"
            required
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Giá thuê (để trống = theo bảng giá)</label>
          <input
            name="giaThue"
            type="number"
            step="1000"
            className="w-36 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Tiền cọc (để trống = bằng giá thuê)</label>
          <input
            name="tienCoc"
            type="number"
            step="1000"
            className="w-36 rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          disabled={isPending}
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
        >
          {isPending ? "Đang tạo..." : "Tạo hợp đồng"}
        </button>
      </div>
      <p className="text-xs text-zinc-500">
        Chưa có mã khách? Thêm khách thuê ở trang{" "}
        <a href="/cn/khach-thue" className="underline">
          Khách thuê
        </a>{" "}
        trước, mã khách sẽ hiện kèm hồ sơ.
      </p>
      {state.error && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
    </form>
  );
}
