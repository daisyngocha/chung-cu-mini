"use client";

import { useActionState } from "react";
import { ghiChiSo, type ChiSoFormState } from "@/app/cn/chi-so/actions";
import { xacNhanNhanPhong, type HopDongDetailFormState } from "@/app/cn/hop-dong/[maHopDong]/actions";
import { ghiNhanTinhTrangNhanPhong, type GhiNhanTinhTrangFormState } from "./actions";

const inputCls = "rounded-md border border-zinc-300 px-2 py-1.5 text-sm";
const labelCls = "text-xs font-medium text-zinc-600";
const btnCls =
  "rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60";
const errCls = "rounded-md bg-red-50 px-3 py-2 text-sm text-red-700";

export function GhiChiSoDauKyForm({
  maHopDong,
  loaiCongTo,
  kyThangMacDinh,
}: {
  maHopDong: string;
  loaiCongTo: "DIEN" | "NUOC";
  kyThangMacDinh: string;
}) {
  const initialState: ChiSoFormState = {};
  const [state, formAction, isPending] = useActionState(ghiChiSo.bind(null, maHopDong), initialState);
  return (
    <form action={formAction} className="flex flex-wrap items-end gap-3">
      <input type="hidden" name="loaiCongTo" value={loaiCongTo} />
      <input type="hidden" name="loaiGhi" value="DAU_KY" />
      <div className="flex flex-col gap-1">
        <label className={labelCls}>Kỳ tháng</label>
        <input name="kyThang" type="month" defaultValue={kyThangMacDinh} required className={inputCls} />
      </div>
      <div className="flex flex-col gap-1">
        <label className={labelCls}>Chỉ số đầu ({loaiCongTo === "DIEN" ? "điện" : "nước"})</label>
        <input name="chiSoMoi" type="number" min={0} required className={`w-28 ${inputCls}`} />
      </div>
      <div className="flex flex-col gap-1">
        <label className={labelCls}>Ảnh công tơ (URL)</label>
        <input name="anhCongTo" className={`w-48 ${inputCls}`} />
      </div>
      <button type="submit" disabled={isPending} className={btnCls}>
        {isPending ? "Đang ghi..." : "Ghi chỉ số đầu"}
      </button>
      {state.error && <p className={`w-full ${errCls}`}>{state.error}</p>}
    </form>
  );
}

export function GhiNhanTaiSanForm({ maHopDong, maTaiSan, tenTaiSan }: { maHopDong: string; maTaiSan: string; tenTaiSan: string }) {
  const initialState: GhiNhanTinhTrangFormState = {};
  const [state, formAction, isPending] = useActionState(
    ghiNhanTinhTrangNhanPhong.bind(null, maHopDong, maTaiSan),
    initialState,
  );
  return (
    <form action={formAction} className="flex flex-wrap items-end gap-2 border-t border-zinc-100 py-2 first:border-t-0">
      <span className="w-40 text-sm font-medium text-zinc-900">{tenTaiSan}</span>
      <input name="tinhTrang" placeholder="Tình trạng (vd. Hoạt động tốt)" required className={`w-56 ${inputCls}`} />
      <input name="anh" placeholder="Ảnh bàn giao (URL)" className={`w-48 ${inputCls}`} />
      <button type="submit" disabled={isPending} className="text-xs font-medium text-zinc-600 hover:underline">
        {isPending ? "Đang lưu..." : "Ghi nhận"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
    </form>
  );
}

export function XacNhanNhanPhongForm({
  maHopDong,
  dsNguoiO,
}: {
  maHopDong: string;
  dsNguoiO: { maNguoiO: string; hoTen: string }[];
}) {
  const initialState: HopDongDetailFormState = {};
  const [state, formAction, isPending] = useActionState(
    xacNhanNhanPhong.bind(null, maHopDong),
    initialState,
  );
  return (
    <form action={formAction} className="flex flex-wrap items-end gap-3">
      <div className="flex flex-col gap-1">
        <label className={labelCls}>Lý do (nếu chưa thu đủ tiền ký HĐ)</label>
        <input name="lyDo" className={`w-64 ${inputCls}`} />
      </div>
      <div className="flex flex-col gap-1">
        <label className={labelCls}>Người được cấp tài khoản</label>
        <select name="maNguoiOCapTK" defaultValue="" className={inputCls}>
          <option value="">Khách đứng tên hợp đồng</option>
          {dsNguoiO.map((n) => (
            <option key={n.maNguoiO} value={n.maNguoiO}>
              {n.hoTen}
            </option>
          ))}
        </select>
      </div>
      <button type="submit" disabled={isPending} className={btnCls}>
        {isPending ? "Đang xác nhận..." : "Xác nhận nhận phòng"}
      </button>
      {state.error && <p className={`w-full ${errCls}`}>{state.error}</p>}
    </form>
  );
}
