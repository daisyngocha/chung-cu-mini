import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay, TEN_TRANG_THAI_HOP_DONG } from "@/lib/format";
import { CreateHopDongForm } from "./create-hop-dong-form";

type HopDongSummaryApi = {
  maHopDong: string;
  maPhong: string;
  ngayBatDau: string;
  ngayHetHan: string;
  giaThue: number;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

type PhongApi = {
  maPhong: string;
  giaNiemYet: number;
  trangThai: "TRONG" | "DANG_THUE" | "NGUNG_KHAI_THAC";
};

type BangGiaApi = {
  maKhoan: string;
  tenKhoan: string;
  loaiKhoan: string;
  donGia: number;
  donViTinh: string;
  ngayHetHieuLuc: string | null;
};

const LOAI_DICH_VU_HOP_DONG = ["DICH_VU_THEO_PHONG", "DICH_VU_THEO_XE"];

const MAU_TRANG_THAI: Record<HopDongSummaryApi["trangThai"], string> = {
  CHO_NHAN_PHONG: "bg-amber-100 text-amber-700",
  HIEU_LUC: "bg-blue-100 text-blue-700",
  DA_THANH_LY: "bg-zinc-100 text-zinc-600",
};

export default async function HopDongPage() {
  const token = await getSessionToken();
  const [danhSachHopDong, danhSachPhong, danhSachBangGia] = await Promise.all([
    goiBackend<HopDongSummaryApi[]>("/api/hop-dong", { token }),
    goiBackend<PhongApi[]>("/api/phong", { token }),
    goiBackend<BangGiaApi[]>("/api/bang-gia", { token }),
  ]);
  const dsPhongTrong = danhSachPhong.filter((p) => p.trangThai === "TRONG");
  const dsDichVuKhaDung = danhSachBangGia.filter(
    (bg) => LOAI_DICH_VU_HOP_DONG.includes(bg.loaiKhoan) && !bg.ngayHetHieuLuc,
  );

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Khách thuê & hợp đồng</h1>

      <CreateHopDongForm dsPhongTrong={dsPhongTrong} dsDichVuKhaDung={dsDichVuKhaDung} />

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Mã hợp đồng</th>
              <th className="px-4 py-2">Phòng</th>
              <th className="px-4 py-2">Từ ngày</th>
              <th className="px-4 py-2">Đến ngày</th>
              <th className="px-4 py-2">Giá thuê</th>
              <th className="px-4 py-2">Trạng thái</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {danhSachHopDong.map((hd) => (
              <tr key={hd.maHopDong}>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  <Link href={`/cn/hop-dong/${hd.maHopDong}`} className="hover:underline">
                    {hd.maHopDong}
                  </Link>
                </td>
                <td className="px-4 py-2">{hd.maPhong}</td>
                <td className="px-4 py-2">{formatNgay(hd.ngayBatDau)}</td>
                <td className="px-4 py-2">{formatNgay(hd.ngayHetHan)}</td>
                <td className="px-4 py-2">{formatTien(hd.giaThue)}</td>
                <td className="px-4 py-2">
                  <span className={"rounded-full px-2 py-0.5 text-xs font-medium " + MAU_TRANG_THAI[hd.trangThai]}>
                    {TEN_TRANG_THAI_HOP_DONG[hd.trangThai]}
                  </span>
                </td>
              </tr>
            ))}
            {danhSachHopDong.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có hợp đồng nào. Tạo hợp đồng đầu tiên ở trên.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
