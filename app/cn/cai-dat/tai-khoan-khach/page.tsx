import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { DatLaiMatKhauForm } from "./dat-lai-mat-khau-form";
import { KhoaMoKhoaButton } from "./khoa-form";

type TaiKhoanKhachApi = {
  maTaiKhoan: string;
  tenDangNhap: string;
  trangThai: "HOAT_DONG" | "KHOA_TAM" | "DA_KHOA";
  maHopDong: string | null;
  maPhong: string | null;
  tenKhach: string | null;
};

const TEN_TRANG_THAI_TK: Record<string, string> = {
  HOAT_DONG: "Hoạt động",
  KHOA_TAM: "Khóa tạm (sai mật khẩu nhiều lần)",
  DA_KHOA: "Đã khóa",
};

const MAU_TRANG_THAI: Record<string, string> = {
  HOAT_DONG: "bg-green-100 text-green-700",
  KHOA_TAM: "bg-amber-100 text-amber-700",
  DA_KHOA: "bg-red-100 text-red-700",
};

export default async function CaiDatTaiKhoanKhachPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<TaiKhoanKhachApi[]>("/api/tai-khoan-khach", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Tài khoản khách</h1>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Tên đăng nhập</th>
              <th className="px-4 py-2">Phòng</th>
              <th className="px-4 py-2">Khách</th>
              <th className="px-4 py-2">Trạng thái</th>
              <th className="px-4 py-2" />
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {danhSach.map((tk) => (
              <tr key={tk.maTaiKhoan}>
                <td className="px-4 py-2 font-medium text-zinc-900">{tk.tenDangNhap}</td>
                <td className="px-4 py-2">
                  {tk.maHopDong ? (
                    <Link href={`/cn/hop-dong/${tk.maHopDong}`} className="hover:underline">
                      {tk.maPhong}
                    </Link>
                  ) : (
                    "—"
                  )}
                </td>
                <td className="px-4 py-2">{tk.tenKhach ?? "—"}</td>
                <td className="px-4 py-2">
                  <span className={"rounded-full px-2 py-0.5 text-xs font-medium " + (MAU_TRANG_THAI[tk.trangThai] ?? "")}>
                    {TEN_TRANG_THAI_TK[tk.trangThai] ?? tk.trangThai}
                  </span>
                </td>
                <td className="px-4 py-2">
                  <div className="flex flex-col items-end gap-2">
                    <KhoaMoKhoaButton maTaiKhoan={tk.maTaiKhoan} dangKhoa={tk.trangThai !== "HOAT_DONG"} />
                    <DatLaiMatKhauForm maTaiKhoan={tk.maTaiKhoan} />
                  </div>
                </td>
              </tr>
            ))}
            {danhSach.length === 0 && (
              <tr>
                <td colSpan={5} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có tài khoản khách nào.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
