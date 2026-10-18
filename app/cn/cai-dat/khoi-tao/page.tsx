import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { KhoiTaoForm } from "./khoi-tao-form";

type PhongApi = { maPhong: string; coCongToNuoc: boolean; trangThai: string };
type BangGiaApi = { maKhoan: string; tenKhoan: string; loaiKhoan: string; ngayHetHieuLuc: string | null };

const LOAI_DICH_VU_HOP_DONG = ["DICH_VU_THEO_PHONG", "DICH_VU_THEO_XE"];

export default async function KhoiTaoPage() {
  const token = await getSessionToken();
  const [danhSachPhong, danhSachBangGia] = await Promise.all([
    goiBackend<PhongApi[]>("/api/phong", { token }),
    goiBackend<BangGiaApi[]>("/api/bang-gia", { token }),
  ]);

  const dsPhong = danhSachPhong
    .filter((p) => p.trangThai === "TRONG")
    .map((p) => ({ maPhong: p.maPhong, coCongToNuoc: p.coCongToNuoc }));
  const dsDichVuKhaDung = danhSachBangGia
    .filter((bg) => LOAI_DICH_VU_HOP_DONG.includes(bg.loaiKhoan) && !bg.ngayHetHieuLuc)
    .map((bg) => ({ maKhoan: bg.maKhoan, tenKhoan: bg.tenKhoan }));

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Nhập dữ liệu đầu kỳ</h1>
      <p className="text-sm text-zinc-600">
        Dùng một lần khi bàn giao một tòa đang vận hành (đã có khách ở) sang hệ thống — tạo hợp đồng
        hiệu lực, cấp tài khoản khách, ghi nhận chỉ số và nợ tồn mà không qua luồng nhận phòng thông
        thường.
      </p>

      {dsPhong.length === 0 ? (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Không còn phòng trống để nhập dữ liệu đầu kỳ — mọi phòng đã có hợp đồng.
        </p>
      ) : (
        <KhoiTaoForm dsPhong={dsPhong} dsDichVuKhaDung={dsDichVuKhaDung} />
      )}
    </div>
  );
}
