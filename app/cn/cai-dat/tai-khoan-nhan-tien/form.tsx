"use client";

import { useActionState } from "react";
import { capNhatTaiKhoanNhanTien, type CapNhatTaiKhoanFormState } from "./actions";

type ToaNha = { nganHang: string; soTaiKhoan: string; chuTaiKhoan: string };

const initialState: CapNhatTaiKhoanFormState = {};
const inputCls = "rounded-md border border-zinc-300 px-2 py-1.5 text-sm";
const labelCls = "text-xs font-medium text-zinc-600";

export function TaiKhoanNhanTienForm({ toaNha }: { toaNha: ToaNha }) {
  const [state, formAction, isPending] = useActionState(capNhatTaiKhoanNhanTien, initialState);

  return (
    <form action={formAction} className="flex flex-col gap-4 rounded-lg border border-zinc-200 bg-white p-4">
      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Mã ngân hàng (VietQR)</label>
          <input
            name="nganHang"
            defaultValue={toaNha.nganHang}
            placeholder="vd. mbbank, vietcombank"
            required
            className={inputCls}
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số tài khoản</label>
          <input name="soTaiKhoan" defaultValue={toaNha.soTaiKhoan} required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Chủ tài khoản</label>
          <input name="chuTaiKhoan" defaultValue={toaNha.chuTaiKhoan} required className={inputCls} />
        </div>
      </div>
      <p className="text-xs text-zinc-500">
        Dùng để sinh mã VietQR cho khách quét thanh toán và in trên phiếu thu. Mã ngân hàng nhập theo
        định danh của img.vietqr.io (vd. mbbank, vietcombank, techcombank...).
      </p>
      <button
        type="submit"
        disabled={isPending}
        className="self-start rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang lưu..." : "Lưu tài khoản nhận tiền"}
      </button>
      {state.error && <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>}
      {state.thanhCong && <p className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">Đã lưu.</p>}
    </form>
  );
}
