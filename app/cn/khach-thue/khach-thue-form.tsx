"use client";

import { useActionState } from "react";
import { capNhatHoSoKhach, type KhachThueFormState } from "./actions";

type KhachThueDaCo = {
  soDienThoai: string;
  hoTen: string;
  ngaySinh: string | null;
  soCCCD: string;
  anhCCCD: string | null;
  queQuan: string | null;
  noiLamViec: string | null;
};

const initialState: KhachThueFormState = {};

export function KhachThueForm({ khach }: { khach?: KhachThueDaCo }) {
  const [state, formAction, isPending] = useActionState(
    capNhatHoSoKhach,
    initialState,
  );

  return (
    <form
      action={formAction}
      className="flex flex-col gap-3 rounded-lg border border-zinc-200 bg-white p-4"
    >
      <h2 className="text-sm font-semibold text-zinc-900">
        {khach ? "Cập nhật hồ sơ khách" : "Thêm khách thuê mới"}
      </h2>
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Số điện thoại</label>
          <input
            name="soDienThoai"
            required
            defaultValue={khach?.soDienThoai}
            placeholder="0901234567"
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Họ tên</label>
          <input
            name="hoTen"
            required
            defaultValue={khach?.hoTen}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Ngày sinh</label>
          <input
            name="ngaySinh"
            type="date"
            required
            defaultValue={khach?.ngaySinh ?? undefined}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Số CCCD</label>
          <input
            name="soCCCD"
            required
            defaultValue={khach?.soCCCD}
            placeholder="12 chữ số"
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Ảnh CCCD (URL)</label>
          <input
            name="anhCCCD"
            defaultValue={khach?.anhCCCD ?? undefined}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Quê quán</label>
          <input
            name="queQuan"
            defaultValue={khach?.queQuan ?? undefined}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Nơi làm việc</label>
          <input
            name="noiLamViec"
            defaultValue={khach?.noiLamViec ?? undefined}
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
      </div>
      <div>
        <button
          type="submit"
          disabled={isPending}
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
        >
          {isPending ? "Đang lưu..." : khach ? "Lưu thay đổi" : "Thêm khách"}
        </button>
      </div>
      {state.error && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">
          {state.error}
        </p>
      )}
    </form>
  );
}
