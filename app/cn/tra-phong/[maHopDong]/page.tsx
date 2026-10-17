import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatNgay } from "@/lib/format";
import { GhiChiSoCuoiKyForm, GhiNhanTaiSanTraPhongForm, TinhQuyetToanButton } from "./forms";

type HopDongApi = {
  maHopDong: string;
  maPhong: string;
  tenKhach: string;
  ngayBaoTra: string | null;
  ngayTraDuKien: string | null;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

type PhongApi = { maPhong: string; coCongToNuoc: boolean };
type ChiSoApi = { maChiSo: string; loaiCongTo: "DIEN" | "NUOC"; loaiGhi: string };
type TaiSanApi = { maTaiSan: string; tenTaiSan: string };
type BanGiaoApi = { maTaiSan: string };

export default async function TraPhongDetailPage({
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

  const [dsChiSo, dsTaiSan, dsBanGiao] = await Promise.all([
    goiBackend<ChiSoApi[]>(`/api/hop-dong/${maHopDong}/chi-so`, { token }),
    goiBackend<TaiSanApi[]>(`/api/phong/${hopDong.maPhong}/tai-san`, { token }),
    goiBackend<BanGiaoApi[]>(`/api/hop-dong/${maHopDong}/ban-giao?loaiBanGiao=TRA_PHONG`, { token }),
  ]);

  const daGhiDien = dsChiSo.some((cs) => cs.loaiCongTo === "DIEN" && cs.loaiGhi === "CUOI_KY");
  const daGhiNuoc = dsChiSo.some((cs) => cs.loaiCongTo === "NUOC" && cs.loaiGhi === "CUOI_KY");
  const maTaiSanDaBanGiao = new Set(dsBanGiao.map((b) => b.maTaiSan));
  const sanSangTinhQuyetToan =
    daGhiDien &&
    (!phong?.coCongToNuoc || daGhiNuoc) &&
    dsTaiSan.length > 0 &&
    dsTaiSan.every((ts) => maTaiSanDaBanGiao.has(ts.maTaiSan));
  const kyThangMacDinh = (hopDong.ngayTraDuKien ?? new Date().toISOString()).slice(0, 7);

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/cn/tra-phong" className="text-sm text-zinc-500 hover:underline">
          ← Trả phòng
        </Link>
        <h1 className="mt-1 text-xl font-semibold text-zinc-900">
          Trả phòng {hopDong.maPhong} — {hopDong.tenKhach}
        </h1>
      </div>

      {hopDong.ngayBaoTra === null ? (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Hợp đồng này chưa báo trả phòng.
        </p>
      ) : (
        <>
          <p className="text-sm text-zinc-600">Ngày trả dự kiến: {formatNgay(hopDong.ngayTraDuKien)}</p>

          <div className="flex flex-col gap-3 rounded-lg border border-zinc-200 bg-white p-4">
            <h3 className="text-sm font-semibold text-zinc-900">Chỉ số cuối kỳ</h3>
            {daGhiDien ? (
              <p className="text-sm text-green-700">Đã ghi chỉ số điện cuối kỳ.</p>
            ) : (
              <GhiChiSoCuoiKyForm maHopDong={maHopDong} loaiCongTo="DIEN" kyThangMacDinh={kyThangMacDinh} />
            )}
            {phong?.coCongToNuoc &&
              (daGhiNuoc ? (
                <p className="text-sm text-green-700">Đã ghi chỉ số nước cuối kỳ.</p>
              ) : (
                <GhiChiSoCuoiKyForm maHopDong={maHopDong} loaiCongTo="NUOC" kyThangMacDinh={kyThangMacDinh} />
              ))}
          </div>

          <div className="flex flex-col rounded-lg border border-zinc-200 bg-white p-4">
            <h3 className="mb-2 text-sm font-semibold text-zinc-900">Kiểm tra tình trạng tài sản khi trả phòng</h3>
            {dsTaiSan.length === 0 && (
              <p className="text-sm text-zinc-500">Phòng chưa khai báo tài sản nào.</p>
            )}
            {dsTaiSan.map((ts) =>
              maTaiSanDaBanGiao.has(ts.maTaiSan) ? (
                <p key={ts.maTaiSan} className="border-t border-zinc-100 py-2 text-sm text-green-700 first:border-t-0">
                  {ts.tenTaiSan} — đã ghi nhận
                </p>
              ) : (
                <GhiNhanTaiSanTraPhongForm key={ts.maTaiSan} maHopDong={maHopDong} maTaiSan={ts.maTaiSan} tenTaiSan={ts.tenTaiSan} />
              ),
            )}
          </div>

          <div className="rounded-lg border border-zinc-200 bg-white p-4">
            <h3 className="mb-3 text-sm font-semibold text-zinc-900">Tính quyết toán cọc</h3>
            {sanSangTinhQuyetToan ? (
              <TinhQuyetToanButton maHopDong={maHopDong} />
            ) : (
              <p className="text-sm text-zinc-500">
                Cần hoàn tất ghi chỉ số cuối kỳ và kiểm tra tình trạng tất cả tài sản trước khi tính quyết toán.
              </p>
            )}
          </div>
        </>
      )}
    </div>
  );
}
