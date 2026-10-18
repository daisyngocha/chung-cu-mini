import Link from "next/link";
import { requireSession, getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, TEN_TRANG_THAI_HOP_DONG, TEN_LOAI_KHOAN } from "@/lib/format";

type HopDongApi = {
  maPhong: string;
  tang: number;
  dienTich: number;
  giaThue: number;
  tienCoc: number;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

type ThongBaoTienApi = { tongTien: number; soDaThu: number };
type SuCoApi = { trangThai: string };
type ToaNhaApi = { tenChuNha: string; sdtChuNha: string };
type BangGiaApi = { maKhoan: string; tenKhoan: string; loaiKhoan: string; donGia: number; donViTinh: string; ngayHetHieuLuc: string | null };

export default async function TrangChuKhachThuePage() {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();

  const [hopDong, dsThongBaoTien, dsSuCo, toaNha, dsBangGia] = await Promise.all([
    goiBackend<HopDongApi>("/api/khach/hop-dong", { token }),
    goiBackend<ThongBaoTienApi[]>("/api/khach/thong-bao-tien", { token }),
    goiBackend<SuCoApi[]>("/api/khach/su-co", { token }),
    goiBackend<ToaNhaApi>("/api/toa-nha/mac-dinh", { token }),
    goiBackend<BangGiaApi[]>("/api/bang-gia", { token }),
  ]);

  const chuaThanhToan = dsThongBaoTien.filter((tb) => tb.soDaThu < tb.tongTien);
  const tongConNo = chuaThanhToan.reduce((a, tb) => a + (tb.tongTien - tb.soDaThu), 0);
  const suCoDangMo = dsSuCo.filter((sc) => sc.trangThai === "MOI" || sc.trangThai === "DANG_XU_LY" || sc.trangThai === "MO_LAI");
  const dienNuocApDung = dsBangGia.filter(
    (bg) => !bg.ngayHetHieuLuc && (bg.loaiKhoan === "DIEN" || bg.loaiKhoan === "NUOC" || bg.loaiKhoan === "NUOC_THEO_NGUOI"),
  );

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Phòng của tôi</h1>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div className="rounded-lg border border-zinc-200 bg-white p-4">
          <h3 className="mb-2 text-sm font-semibold text-zinc-900">
            Phòng {hopDong.maPhong} — Tầng {hopDong.tang}
          </h3>
          <p className="text-sm text-zinc-700">Diện tích: {hopDong.dienTich} m²</p>
          <p className="text-sm text-zinc-700">Giá thuê: {formatTien(hopDong.giaThue)}/tháng</p>
          <p className="text-sm text-zinc-700">Tiền cọc: {formatTien(hopDong.tienCoc)}</p>
          <p className="mt-1 text-sm text-zinc-700">
            Trạng thái hợp đồng: <strong>{TEN_TRANG_THAI_HOP_DONG[hopDong.trangThai]}</strong>
          </p>
          <Link href="/kt/hop-dong" className="mt-2 inline-block text-xs text-zinc-500 underline">
            Xem hồ sơ & hợp đồng
          </Link>
        </div>

        <div className="rounded-lg border border-zinc-200 bg-white p-4">
          <h3 className="mb-2 text-sm font-semibold text-zinc-900">Đơn giá điện, nước đang áp dụng</h3>
          {dienNuocApDung.map((bg) => (
            <p key={bg.maKhoan} className="text-sm text-zinc-700">
              {TEN_LOAI_KHOAN[bg.loaiKhoan] ?? bg.tenKhoan}: {formatTien(bg.donGia)}/{bg.donViTinh}
            </p>
          ))}
          {dienNuocApDung.length === 0 && <p className="text-sm text-zinc-500">Chưa có đơn giá.</p>}
        </div>

        <Link href="/kt/thong-bao-tien" className="rounded-lg border border-zinc-200 bg-white p-4 hover:bg-zinc-50">
          <h3 className="mb-1 text-sm font-semibold text-zinc-900">Thông báo tiền chưa thanh toán</h3>
          <p className="text-2xl font-semibold text-zinc-900">{chuaThanhToan.length}</p>
          {chuaThanhToan.length > 0 && <p className="text-sm text-zinc-600">Còn thiếu: {formatTien(tongConNo)}</p>}
        </Link>

        <Link href="/kt/su-co" className="rounded-lg border border-zinc-200 bg-white p-4 hover:bg-zinc-50">
          <h3 className="mb-1 text-sm font-semibold text-zinc-900">Sự cố đang mở</h3>
          <p className="text-2xl font-semibold text-zinc-900">{suCoDangMo.length}</p>
        </Link>
      </div>

      <div className="rounded-lg border border-zinc-200 bg-white p-4">
        <h3 className="mb-2 text-sm font-semibold text-zinc-900">Liên hệ chủ nhà</h3>
        <p className="text-sm text-zinc-700">{toaNha.tenChuNha}</p>
        <a href={`tel:${toaNha.sdtChuNha}`} className="text-sm text-blue-700 underline">
          {toaNha.sdtChuNha}
        </a>
      </div>
    </div>
  );
}
