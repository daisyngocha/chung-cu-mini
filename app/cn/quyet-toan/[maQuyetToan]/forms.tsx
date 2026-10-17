"use client";

import Link from "next/link";
import { useActionState } from "react";
import {
  themKhoanTru,
  suaKhoanTru,
  xoaKhoanTru,
  guiKhach,
  chotQuyetToan,
  lapPhieuChi,
  lapThongBaoThuThem,
  type QuyetToanFormState,
  type LapPhieuChiFormState,
  type LapThongBaoThuThemFormState,
} from "./actions";

const initialState: QuyetToanFormState = {};
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

const TUY_CHON_LOAI_TRU = [
  { value: "DIEN", label: "Điện" },
  { value: "NUOC", label: "Nước" },
  { value: "DICH_VU", label: "Dịch vụ" },
  { value: "NO_CHUA_TRA", label: "Nợ chưa trả" },
  { value: "BOI_THUONG_HU_HONG", label: "Bồi thường hư hỏng" },
  { value: "PHAT_TRA_TRUOC_HAN", label: "Phạt trả trước hạn" },
  { value: "KHAC", label: "Khác" },
];

export function ThemKhoanTruForm({ maQuyetToan }: { maQuyetToan: string }) {
  const [state, formAction, isPending] = useActionState(
    themKhoanTru.bind(null, maQuyetToan),
    initialState,
  );
  return (
    <Card title="Thêm khoản trừ">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Loại khoản</label>
          <select name="loaiKhoan" required className={inputCls}>
            {TUY_CHON_LOAI_TRU.map((o) => (
              <option key={o.value} value={o.value}>
                {o.label}
              </option>
            ))}
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số tiền</label>
          <input name="soTien" type="number" step="1" required className={`w-32 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ghi chú (bắt buộc)</label>
          <input name="ghiChu" required className={`w-48 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ảnh bằng chứng (bắt buộc)</label>
          <input name="anhBangChung" required className={`w-48 ${inputCls}`} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Lý do điều chỉnh (nếu khách không đồng ý)</label>
          <input name="lyDoDieuChinh" className={`w-56 ${inputCls}`} />
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang thêm..." : "Thêm khoản trừ"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function SuaKhoanTruForm({ maQuyetToan, maDong }: { maQuyetToan: string; maDong: string }) {
  const [state, formAction, isPending] = useActionState(
    suaKhoanTru.bind(null, maQuyetToan, maDong),
    initialState,
  );
  return (
    <form action={formAction} className="flex flex-wrap items-end gap-2">
      <input name="soTien" type="number" step="1" placeholder="Số tiền mới" required className={`w-28 ${inputCls}`} />
      <input name="ghiChu" placeholder="Ghi chú mới" required className={`w-40 ${inputCls}`} />
      <input name="anhBangChung" placeholder="Ảnh bằng chứng mới" required className={`w-40 ${inputCls}`} />
      <input name="lyDo" placeholder="Lý do sửa" className={`w-40 ${inputCls}`} />
      <button type="submit" disabled={isPending} className="text-xs font-medium text-zinc-600 hover:underline">
        {isPending ? "Đang lưu..." : "Sửa"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
    </form>
  );
}

export function XoaKhoanTruForm({ maQuyetToan, maDong }: { maQuyetToan: string; maDong: string }) {
  const [state, formAction, isPending] = useActionState(
    xoaKhoanTru.bind(null, maQuyetToan, maDong),
    initialState,
  );
  return (
    <form action={formAction} className="flex items-center gap-2">
      <input name="lyDo" placeholder="Lý do xóa" required className={`w-32 ${inputCls}`} />
      <button
        type="submit"
        disabled={isPending}
        className="text-xs font-medium text-red-700 hover:underline"
      >
        {isPending ? "Đang xóa..." : "Xóa"}
      </button>
      {state.error && <span className="text-xs text-red-700">{state.error}</span>}
    </form>
  );
}

export function GuiKhachButton({ maQuyetToan }: { maQuyetToan: string }) {
  return (
    <form action={guiKhach.bind(null, maQuyetToan)}>
      <button type="submit" className={btnCls}>
        Gửi khách xác nhận
      </button>
    </form>
  );
}

export function ChotQuyetToanForm({ maQuyetToan }: { maQuyetToan: string }) {
  const [state, formAction, isPending] = useActionState(
    chotQuyetToan.bind(null, maQuyetToan),
    initialState,
  );
  return (
    <Card title="Chốt quyết toán">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Lý do (nếu khách không đồng ý)</label>
          <input name="lyDo" className={`w-64 ${inputCls}`} />
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang chốt..." : "Chốt quyết toán"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
    </Card>
  );
}

export function LapPhieuChiForm({ maQuyetToan }: { maQuyetToan: string }) {
  const initial: LapPhieuChiFormState = {};
  const [state, formAction, isPending] = useActionState(lapPhieuChi.bind(null, maQuyetToan), initial);
  return (
    <Card title="Lập phiếu chi (hoàn tiền cho khách)">
      <form action={formAction} className="flex flex-wrap items-end gap-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Hình thức chi</label>
          <select name="hinhThuc" required className={inputCls}>
            <option value="TIEN_MAT">Tiền mặt</option>
            <option value="CHUYEN_KHOAN">Chuyển khoản</option>
          </select>
        </div>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang lập..." : "Lập phiếu chi"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
      {state.soPhieuChi && (
        <p className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">
          Đã lập phiếu chi {state.soPhieuChi} —{" "}
          <a href={`/cn/phieu-chi/${state.soPhieuChi}/file`} className="underline">
            tải PDF
          </a>
        </p>
      )}
    </Card>
  );
}

export function LapThongBaoThuThemButton({ maQuyetToan }: { maQuyetToan: string }) {
  const initial: LapThongBaoThuThemFormState = {};
  const [state, formAction, isPending] = useActionState(lapThongBaoThuThem.bind(null, maQuyetToan), initial);
  return (
    <Card title="Lập thông báo thu thêm (khách còn nợ)">
      <form action={formAction}>
        <button type="submit" disabled={isPending} className={btnCls}>
          {isPending ? "Đang lập..." : "Lập thông báo thu thêm"}
        </button>
      </form>
      {state.error && <p className={errCls}>{state.error}</p>}
      {state.maThongBaoTien && (
        <p className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">
          Đã lập thông báo{" "}
          <Link href={`/cn/thong-bao-tien/${state.maThongBaoTien}`} className="underline">
            {state.maThongBaoTien}
          </Link>
        </p>
      )}
    </Card>
  );
}
