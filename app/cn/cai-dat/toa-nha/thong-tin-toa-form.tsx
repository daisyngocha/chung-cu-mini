"use client";

import { useActionState } from "react";
import { capNhatThongTin, type CapNhatThongTinFormState } from "./actions";

type ToaNha = {
  tenToa: string;
  diaChi: string;
  noiQuy: string | null;
  ngayChotChiSo: number;
  hanThanhToan: number;
  tenChuNha: string;
  sdtChuNha: string;
  diaChiChuNha: string | null;
};

const initialState: CapNhatThongTinFormState = {};
const inputCls = "rounded-md border border-zinc-300 px-2 py-1.5 text-sm";
const labelCls = "text-xs font-medium text-zinc-600";

export function ThongTinToaForm({ toaNha }: { toaNha: ToaNha }) {
  const [state, formAction, isPending] = useActionState(capNhatThongTin, initialState);

  return (
    <form action={formAction} className="flex flex-col gap-4 rounded-lg border border-zinc-200 bg-white p-4">
      <div className="grid grid-cols-2 gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Tên tòa</label>
          <input name="tenToa" defaultValue={toaNha.tenToa} required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Địa chỉ</label>
          <input name="diaChi" defaultValue={toaNha.diaChi} required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ngày chốt chỉ số hằng tháng</label>
          <input
            name="ngayChotChiSo"
            type="number"
            min={1}
            max={31}
            defaultValue={toaNha.ngayChotChiSo}
            required
            className={inputCls}
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Hạn thanh toán (số ngày kể từ khi phát hành)</label>
          <input
            name="hanThanhToan"
            type="number"
            min={1}
            max={31}
            defaultValue={toaNha.hanThanhToan}
            required
            className={inputCls}
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Tên chủ nhà</label>
          <input name="tenChuNha" defaultValue={toaNha.tenChuNha} required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số điện thoại chủ nhà</label>
          <input name="sdtChuNha" defaultValue={toaNha.sdtChuNha} required className={inputCls} />
        </div>
        <div className="col-span-2 flex flex-col gap-1">
          <label className={labelCls}>Địa chỉ liên hệ của chủ nhà</label>
          <input name="diaChiChuNha" defaultValue={toaNha.diaChiChuNha ?? ""} className={inputCls} />
        </div>
        <div className="col-span-2 flex flex-col gap-1">
          <label className={labelCls}>Nội quy tòa (hiển thị cho khách thuê)</label>
          <textarea name="noiQuy" defaultValue={toaNha.noiQuy ?? ""} rows={6} className={inputCls} />
        </div>
      </div>
      <button
        type="submit"
        disabled={isPending}
        className="self-start rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang lưu..." : "Lưu thông tin tòa"}
      </button>
      {state.error && <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>}
      {state.thanhCong && <p className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">Đã lưu.</p>}
    </form>
  );
}
