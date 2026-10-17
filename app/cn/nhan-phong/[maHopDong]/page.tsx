import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, TEN_TRANG_THAI_TBT } from "@/lib/format";
import { GhiChiSoDauKyForm, GhiNhanTaiSanForm, XacNhanNhanPhongForm } from "./forms";

type HopDongApi = {
  maHopDong: string;
  maPhong: string;
  tenKhach: string;
  ngayBatDau: string;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

type PhongApi = { maPhong: string; coCongToNuoc: boolean };
type ChiSoApi = { maChiSo: string; loaiCongTo: "DIEN" | "NUOC"; loaiGhi: string };
type TaiSanApi = { maTaiSan: string; tenTaiSan: string };
type BanGiaoApi = { maTaiSan: string };
type NguoiOApi = { maNguoiO: string; hoTen: string };
type ThongBaoTienApi = {
  maThongBaoTien: string;
  tongTien: number;
  soDaThu: number;
  trangThai: string;
} | null;

export default async function NhanPhongDetailPage({
  params,
}: {
  params: Promise<{ maHopDong: string }>;
}) {
  const { maHopDong } = await params;
  const token = await getSessionToken();

  const [hopDong, danhSachPhong] = await Promise.all([
    goiBackend<HopDongApi>(`/api/hop-dong/${maHopDong}`, { token }),
    goiBackend<PhongApi[]>("/api/phong", { token }),
  ]);
  const phong = danhSachPhong.find((p) => p.maPhong === hopDong.maPhong);

  const [thongBaoKyHD, dsChiSo, dsTaiSan, dsBanGiao, dsNguoiO] = await Promise.all([
    goiBackend<ThongBaoTienApi>(`/api/thong-bao-tien/hop-dong/${maHopDong}/ky-hop-dong`, { token }),
    goiBackend<ChiSoApi[]>(`/api/hop-dong/${maHopDong}/chi-so`, { token }),
    goiBackend<TaiSanApi[]>(`/api/phong/${hopDong.maPhong}/tai-san`, { token }),
    goiBackend<BanGiaoApi[]>(`/api/hop-dong/${maHopDong}/ban-giao?loaiBanGiao=NHAN_PHONG`, { token }),
    goiBackend<NguoiOApi[]>(`/api/hop-dong/${maHopDong}/nguoi-o`, { token }),
  ]);

  let vietQR: string | null = null;
  if (thongBaoKyHD && thongBaoKyHD.trangThai !== "NHAP" && thongBaoKyHD.trangThai !== "DA_HUY" && thongBaoKyHD.trangThai !== "DA_THU_DU") {
    try {
      const res = await goiBackend<{ url: string }>(
        `/api/thong-bao-tien/${thongBaoKyHD.maThongBaoTien}/viet-qr`,
        { token },
      );
      vietQR = res.url;
    } catch {
      vietQR = null;
    }
  }

  const daGhiDien = dsChiSo.some((cs) => cs.loaiCongTo === "DIEN" && cs.loaiGhi === "DAU_KY");
  const daGhiNuoc = dsChiSo.some((cs) => cs.loaiCongTo === "NUOC" && cs.loaiGhi === "DAU_KY");
  const maTaiSanDaBanGiao = new Set(dsBanGiao.map((b) => b.maTaiSan));
  const kyThangMacDinh = hopDong.ngayBatDau.slice(0, 7);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/cn/nhan-phong" className="text-sm text-zinc-500 hover:underline">
          ← Nhận phòng
        </Link>
        <h1 className="mt-1 text-xl font-semibold text-zinc-900">
          Nhận phòng {hopDong.maPhong} — {hopDong.tenKhach}
        </h1>
      </div>

      {hopDong.trangThai !== "CHO_NHAN_PHONG" ? (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Hợp đồng này không ở trạng thái chờ nhận phòng.
        </p>
      ) : (
        <>
          <div className="rounded-lg border border-zinc-200 bg-white p-4">
            <h3 className="mb-2 text-sm font-semibold text-zinc-900">Thông báo tiền ký hợp đồng</h3>
            {thongBaoKyHD ? (
              <div className="flex flex-col gap-1 text-sm">
                <p>
                  Trạng thái: <strong>{TEN_TRANG_THAI_TBT[thongBaoKyHD.trangThai] ?? thongBaoKyHD.trangThai}</strong>
                  {" — "}đã thu {formatTien(thongBaoKyHD.soDaThu)} / {formatTien(thongBaoKyHD.tongTien)}
                </p>
                <Link href={`/cn/thong-bao-tien/${thongBaoKyHD.maThongBaoTien}`} className="text-xs underline">
                  Xem chi tiết thông báo
                </Link>
                {vietQR && (
                  // eslint-disable-next-line @next/next/no-img-element
                  <img src={vietQR} alt="Mã VietQR" className="mt-2 h-48 w-48" />
                )}
              </div>
            ) : (
              <p className="text-sm text-zinc-500">Chưa lập thông báo tiền ký hợp đồng.</p>
            )}
          </div>

          <div className="flex flex-col gap-3 rounded-lg border border-zinc-200 bg-white p-4">
            <h3 className="text-sm font-semibold text-zinc-900">Chỉ số đầu kỳ</h3>
            {daGhiDien ? (
              <p className="text-sm text-green-700">Đã ghi chỉ số điện đầu kỳ.</p>
            ) : (
              <GhiChiSoDauKyForm maHopDong={maHopDong} loaiCongTo="DIEN" kyThangMacDinh={kyThangMacDinh} />
            )}
            {phong?.coCongToNuoc &&
              (daGhiNuoc ? (
                <p className="text-sm text-green-700">Đã ghi chỉ số nước đầu kỳ.</p>
              ) : (
                <GhiChiSoDauKyForm maHopDong={maHopDong} loaiCongTo="NUOC" kyThangMacDinh={kyThangMacDinh} />
              ))}
          </div>

          <div className="flex flex-col rounded-lg border border-zinc-200 bg-white p-4">
            <h3 className="mb-2 text-sm font-semibold text-zinc-900">Tình trạng tài sản bàn giao</h3>
            {dsTaiSan.length === 0 && (
              <p className="text-sm text-zinc-500">Phòng chưa khai báo tài sản nào.</p>
            )}
            {dsTaiSan.map((ts) =>
              maTaiSanDaBanGiao.has(ts.maTaiSan) ? (
                <p key={ts.maTaiSan} className="border-t border-zinc-100 py-2 text-sm text-green-700 first:border-t-0">
                  {ts.tenTaiSan} — đã ghi nhận
                </p>
              ) : (
                <GhiNhanTaiSanForm key={ts.maTaiSan} maHopDong={maHopDong} maTaiSan={ts.maTaiSan} tenTaiSan={ts.tenTaiSan} />
              ),
            )}
          </div>

          <div className="rounded-lg border border-zinc-200 bg-white p-4">
            <h3 className="mb-3 text-sm font-semibold text-zinc-900">Xác nhận nhận phòng</h3>
            <XacNhanNhanPhongForm maHopDong={maHopDong} dsNguoiO={dsNguoiO} />
          </div>
        </>
      )}
    </div>
  );
}
