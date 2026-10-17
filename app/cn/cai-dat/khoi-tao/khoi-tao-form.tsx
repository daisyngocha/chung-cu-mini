"use client";

import { useActionState, useState } from "react";
import { nhapHopDongDauKy, type KhoiTaoFormState } from "./actions";

type PhongTrong = { maPhong: string; coCongToNuoc: boolean };
type DichVuKhaDung = { maKhoan: string; tenKhoan: string };
type NguoiO = { hoTen: string; soCCCD: string; soDienThoai: string; ngayVao: string };

const inputCls = "rounded-md border border-zinc-300 px-2 py-1.5 text-sm";
const labelCls = "text-xs font-medium text-zinc-600";

const initialState: KhoiTaoFormState = {};

export function KhoiTaoForm({
  dsPhong,
  dsDichVuKhaDung,
}: {
  dsPhong: PhongTrong[];
  dsDichVuKhaDung: DichVuKhaDung[];
}) {
  const [state, formAction, isPending] = useActionState(nhapHopDongDauKy, initialState);
  const [maPhong, setMaPhong] = useState(dsPhong[0]?.maPhong ?? "");
  const [dsNguoiO, setDsNguoiO] = useState<NguoiO[]>([]);

  const phongChon = dsPhong.find((p) => p.maPhong === maPhong);

  function themNguoiO() {
    setDsNguoiO([...dsNguoiO, { hoTen: "", soCCCD: "", soDienThoai: "", ngayVao: "" }]);
  }

  function xoaNguoiO(i: number) {
    setDsNguoiO(dsNguoiO.filter((_, idx) => idx !== i));
  }

  function suaNguoiO(i: number, field: keyof NguoiO, value: string) {
    setDsNguoiO(dsNguoiO.map((n, idx) => (idx === i ? { ...n, [field]: value } : n)));
  }

  return (
    <form action={formAction} className="flex flex-col gap-4 rounded-lg border border-zinc-200 bg-white p-4">
      <input type="hidden" name="nguoiOJson" value={JSON.stringify(dsNguoiO)} />

      <h2 className="text-sm font-semibold text-zinc-900">Nhập một hợp đồng đang vận hành</h2>
      <p className="text-xs text-zinc-500">
        Lặp lại biểu mẫu này cho từng phòng đang có khách ở trước khi dùng hệ thống.
      </p>

      <div className="grid grid-cols-2 gap-3 sm:grid-cols-3">
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Phòng</label>
          <select
            name="maPhong"
            value={maPhong}
            onChange={(e) => setMaPhong(e.target.value)}
            required
            className={inputCls}
          >
            {dsPhong.map((p) => (
              <option key={p.maPhong} value={p.maPhong}>
                {p.maPhong}
              </option>
            ))}
          </select>
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số điện thoại khách</label>
          <input name="soDienThoai" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Họ tên khách</label>
          <input name="hoTen" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ngày sinh</label>
          <input name="ngaySinh" type="date" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Số CCCD</label>
          <input name="soCCCD" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Quê quán</label>
          <input name="queQuan" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Nơi làm việc</label>
          <input name="noiLamViec" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ngày bắt đầu HĐ</label>
          <input name="ngayBatDau" type="date" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Ngày hết hạn HĐ</label>
          <input name="ngayHetHan" type="date" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Giá thuê</label>
          <input name="giaThue" type="number" step="1000" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Tiền cọc</label>
          <input name="tienCoc" type="number" step="1000" required className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Nợ tồn (nếu có)</label>
          <input name="noTon" type="number" step="1000" className={inputCls} />
        </div>
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Chỉ số điện gần nhất</label>
          <input name="chiSoDien" type="number" min={0} required className={inputCls} />
        </div>
        {phongChon?.coCongToNuoc && (
          <div className="flex flex-col gap-1">
            <label className={labelCls}>Chỉ số nước gần nhất</label>
            <input name="chiSoNuoc" type="number" min={0} className={inputCls} />
          </div>
        )}
      </div>

      {dsDichVuKhaDung.length > 0 && (
        <div className="flex flex-col gap-2">
          <label className={labelCls}>Dịch vụ đăng ký kèm hợp đồng</label>
          <div className="flex flex-wrap gap-4">
            {dsDichVuKhaDung.map((dv) => (
              <label key={dv.maKhoan} className="flex items-center gap-1.5 text-sm text-zinc-700">
                <input type="checkbox" name="dsDichVu" value={dv.maKhoan} className="h-4 w-4" />
                {dv.tenKhoan}
              </label>
            ))}
          </div>
        </div>
      )}

      <div className="flex flex-col gap-2">
        <div className="flex items-center justify-between">
          <label className={labelCls}>Người ở cùng</label>
          <button type="button" onClick={themNguoiO} className="text-xs font-medium text-zinc-600 hover:underline">
            + Thêm người ở
          </button>
        </div>
        {dsNguoiO.map((n, i) => (
          <div key={i} className="flex flex-wrap items-end gap-2 rounded-md border border-zinc-100 p-2">
            <input
              placeholder="Họ tên"
              value={n.hoTen}
              onChange={(e) => suaNguoiO(i, "hoTen", e.target.value)}
              className={`w-40 ${inputCls}`}
            />
            <input
              placeholder="Số CCCD"
              value={n.soCCCD}
              onChange={(e) => suaNguoiO(i, "soCCCD", e.target.value)}
              className={`w-36 ${inputCls}`}
            />
            <input
              placeholder="Số điện thoại"
              value={n.soDienThoai}
              onChange={(e) => suaNguoiO(i, "soDienThoai", e.target.value)}
              className={`w-36 ${inputCls}`}
            />
            <input
              type="date"
              value={n.ngayVao}
              onChange={(e) => suaNguoiO(i, "ngayVao", e.target.value)}
              className={inputCls}
            />
            <button type="button" onClick={() => xoaNguoiO(i)} className="text-xs font-medium text-red-700 hover:underline">
              Xóa
            </button>
          </div>
        ))}
      </div>

      {dsNguoiO.length > 0 && (
        <div className="flex flex-col gap-1">
          <label className={labelCls}>Người được cấp tài khoản</label>
          <select name="viTriNguoiOCapTaiKhoan" defaultValue="" className={`w-64 ${inputCls}`}>
            <option value="">Khách đứng tên hợp đồng</option>
            {dsNguoiO.map((n, i) => (
              <option key={i} value={i}>
                {n.hoTen || `Người ở #${i + 1}`}
              </option>
            ))}
          </select>
        </div>
      )}

      <button
        type="submit"
        disabled={isPending}
        className="self-start rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
      >
        {isPending ? "Đang nhập..." : "Nhập hợp đồng này"}
      </button>

      {state.error && (
        <p className="rounded-md bg-red-50 px-3 py-2 text-sm text-red-700">{state.error}</p>
      )}
      {state.ketQua && (
        <div className="rounded-md bg-green-50 px-3 py-2 text-sm text-green-700">
          <p>
            Đã tạo hợp đồng {state.ketQua.maHopDong} (phòng {state.ketQua.maPhong}).
          </p>
          <p className="mt-1 font-medium">
            Tài khoản khách: {state.ketQua.tenDangNhap} — mật khẩu tạm:{" "}
            <span className="font-mono">{state.ketQua.matKhauTam}</span>
          </p>
          <p className="mt-1 text-xs">Ghi lại mật khẩu này ngay — hệ thống chỉ hiện một lần.</p>
        </div>
      )}
    </form>
  );
}
