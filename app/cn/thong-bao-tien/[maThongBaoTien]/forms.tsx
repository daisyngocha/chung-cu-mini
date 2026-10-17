"use client";

import { useActionState } from "react";
import { themDong, phatHanh, huy, ghiNhanThu, type ThongBaoTienDetailFormState } from "./actions";

const initialState: ThongBaoTienDetailFormState = {};
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

export function ThemDongForm({ maThongBaoTien }: { maThongBaoTien: string }) {
  const [state, formAction, isPending] = useActionState(
    themDong.bind(null, maThongBaoTien),
    initialState,
  );
  return (
    <Card title="Thêm dòng (phụ phí, điều chỉnh...)">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Loại khoản</label>
          <select name="loaiKhoan" required className={inputCls}>
            <option value="PHI_PHAT_SINH">Phí phát sinh</option>
            <option value="DICH_VU">Dịch vụ</option>
            <option value="DIEU_CHINH">Điều chỉnh</option>
            <option value="GIAM_TRU">Giảm trừ</option>
            <option value="TIEN_PHONG">Tiền phòng</option>
            <option value="TIEN_COC">Tiền cọc</option>
            <option value="DIEN">Điện</option>
            <option value="NUOC">Nước</option>
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Diễn giải</label>
          <input name="dienGiai" required className={`w-48 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Từ ngày</label>
          <input name="tuNgay" type="date" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Đến ngày</label>
          <input name="denNgay" type="date" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số lượng</label>
          <input name="soLuong" type="number" step="0.01" required className={`w-24 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Đơn giá</label>
          <input name="donGia" type="number" step="1" required className={`w-32 ${inputCls}`} />
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang thêm..." : "Thêm dòng"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function PhatHanhButton({ maThongBaoTien }: { maThongBaoTien: string }) {
  return (
    <form action={phatHanh.bind(null, maThongBaoTien)}>
      <button type="submit" className={btnCls}>
        Phát hành thông báo
      </button>
    </form>
  );
}

export function HuyForm({ maThongBaoTien }: { maThongBaoTien: string }) {
  const [state, formAction, isPending] = useActionState(huy.bind(null, maThongBaoTien), initialState);
  return (
    <Card title="Hủy thông báo">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Lý do</label>
          <input name="lyDo" className={`w-64 ${inputCls}`} />
        </div>
        <button
          type="submit"
          disabled={isPending}
          className="rounded-md border border-red-300 px-3 py-1.5 text-sm font-medium text-red-700 hover:bg-red-50 disabled:opacity-60"
        >
          {isPending ? "Đang hủy..." : "Hủy thông báo"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function GhiNhanThuForm({ maThongBaoTien }: { maThongBaoTien: string }) {
  const [state, formAction, isPending] = useActionState(
    ghiNhanThu.bind(null, maThongBaoTien),
    initialState,
  );
  return (
    <Card title="Ghi nhận thu trực tiếp (tiền mặt / CK ngoài hệ thống)">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số tiền</label>
          <input name="soTien" type="number" step="1" required className={`w-36 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Hình thức</label>
          <select name="hinhThuc" required className={inputCls}>
            <option value="TIEN_MAT">Tiền mặt</option>
            <option value="CHUYEN_KHOAN">Chuyển khoản</option>
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Người nộp</label>
          <input name="nguoiNop" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ngày thu</label>
          <input name="ngayThu" type="date" className={inputCls} />
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang ghi nhận..." : "Ghi nhận thu"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}
