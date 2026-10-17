"use client";

import { useActionState } from "react";
import {
  themNguoiO,
  giaHan,
  dieuChinhGia,
  baoTraPhong,
  xacNhanThuHoiChiaKhoa,
  nguoiORoiDi,
  type HopDongDetailFormState,
} from "./actions";

const initialState: HopDongDetailFormState = {};
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

export function ThemNguoiOForm({ maHopDong }: { maHopDong: string }) {
  const [state, formAction, isPending] = useActionState(
    themNguoiO.bind(null, maHopDong),
    initialState,
  );
  return (
    <Card title="Thêm người ở cùng">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Họ tên</label>
          <input name="hoTen" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số CCCD</label>
          <input name="soCCCD" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số điện thoại</label>
          <input name="soDienThoai" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ngày vào</label>
          <input name="ngayVao" type="date" className={inputCls} />
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang thêm..." : "Thêm người ở"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function GiaHanForm({ maHopDong }: { maHopDong: string }) {
  const [state, formAction, isPending] = useActionState(
    giaHan.bind(null, maHopDong),
    initialState,
  );
  return (
    <Card title="Gia hạn hợp đồng">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Hạn mới</label>
          <input name="ngayHetHanMoi" type="date" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Giá mới (để trống = giữ nguyên)</label>
          <input name="giaMoi" type="number" step="1000" className={`w-36 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ảnh phụ lục (URL)</label>
          <input name="anhPhuLuc" required className={`w-56 ${inputCls}`} />
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang gia hạn..." : "Gia hạn"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function DieuChinhGiaForm({ maHopDong }: { maHopDong: string }) {
  const [state, formAction, isPending] = useActionState(
    dieuChinhGia.bind(null, maHopDong),
    initialState,
  );
  return (
    <Card title="Điều chỉnh giá thuê">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Giá mới</label>
          <input name="giaMoi" type="number" step="1000" required className={`w-36 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ngày áp dụng (ngày 1 của tháng)</label>
          <input name="ngayApDung" type="date" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ảnh phụ lục (URL)</label>
          <input name="anhPhuLuc" required className={`w-56 ${inputCls}`} />
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang lưu..." : "Điều chỉnh giá"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function BaoTraPhongForm({ maHopDong }: { maHopDong: string }) {
  const [state, formAction, isPending] = useActionState(
    baoTraPhong.bind(null, maHopDong),
    initialState,
  );
  return (
    <Card title="Báo trả phòng">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ngày trả dự kiến</label>
          <input name="ngayTraDuKien" type="date" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Loại kết thúc</label>
          <select name="loaiKetThuc" required className={inputCls}>
            <option value="">— chọn —</option>
            <option value="KHACH_BAO_TRA">Khách báo trả</option>
            <option value="HET_HAN">Hết hạn hợp đồng</option>
            <option value="CHAM_DUT_VI_PHAM">Chấm dứt do vi phạm</option>
          </select>
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang lưu..." : "Ghi nhận báo trả"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function ThuHoiChiaKhoaButton({ maHopDong }: { maHopDong: string }) {
  return (
    <form action={xacNhanThuHoiChiaKhoa.bind(null, maHopDong)}>
      <button
        type="submit"
        className="rounded-md border border-zinc-300 px-3 py-1.5 text-sm font-medium text-zinc-700 hover:bg-zinc-50"
      >
        Xác nhận đã thu hồi chìa khóa
      </button>
    </form>
  );
}

export function NguoiORoiDiButton({
  maHopDong,
  maNguoiO,
}: {
  maHopDong: string;
  maNguoiO: string;
}) {
  return (
    <form
      action={nguoiORoiDi.bind(null, maHopDong, maNguoiO)}
      className="flex items-center gap-2"
    >
      <input name="ngayRa" type="date" required className={`${inputCls} py-1`} />
      <button type="submit" className="text-xs font-medium text-zinc-600 hover:underline">
        Rời đi
      </button>
    </form>
  );
}
