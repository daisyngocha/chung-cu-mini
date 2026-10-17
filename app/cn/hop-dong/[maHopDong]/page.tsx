import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import {
  formatTien,
  formatNgay,
  TEN_TRANG_THAI_HOP_DONG,
  TEN_LOAI_KET_THUC,
} from "@/lib/format";
import {
  ThemNguoiOForm,
  GiaHanForm,
  DieuChinhGiaForm,
  BaoTraPhongForm,
  ThuHoiChiaKhoaButton,
  NguoiORoiDiButton,
} from "./forms";

type HopDongApi = {
  maHopDong: string;
  maPhong: string;
  maKhach: string;
  tenKhach: string;
  maTaiKhoan: string | null;
  ngayBatDau: string;
  ngayHetHan: string;
  giaThue: number;
  tienCoc: number;
  mucPhatTraTruoc: number;
  anhHopDong: string | null;
  ngayNhanPhong: string | null;
  ngayBaoTra: string | null;
  ngayTraDuKien: string | null;
  ngayTraThucTe: string | null;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
  lyDoNhanPhongChuaThuDu: string | null;
  loaiKetThuc: "KHACH_BAO_TRA" | "HET_HAN" | "CHAM_DUT_VI_PHAM" | null;
  daThuHoiChiaKhoa: boolean;
};

type NguoiOApi = {
  maNguoiO: string;
  maHopDong: string;
  hoTen: string;
  soCCCD: string | null;
  soDienThoai: string | null;
  ngayVao: string | null;
  ngayRa: string | null;
};

export default async function HopDongDetailPage({
  params,
}: {
  params: Promise<{ maHopDong: string }>;
}) {
  const { maHopDong } = await params;
  const token = await getSessionToken();
  const [hopDong, dsNguoiO] = await Promise.all([
    goiBackend<HopDongApi>(`/api/hop-dong/${maHopDong}`, { token }),
    goiBackend<NguoiOApi[]>(`/api/hop-dong/${maHopDong}/nguoi-o`, { token }),
  ]);

  const choNhanPhong = hopDong.trangThai === "CHO_NHAN_PHONG";
  const hieuLuc = hopDong.trangThai === "HIEU_LUC";
  const chuaBaoTra = hieuLuc && !hopDong.ngayBaoTra;
  const choThuHoiChiaKhoa = hieuLuc && hopDong.ngayBaoTra && !hopDong.daThuHoiChiaKhoa;

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href="/cn/hop-dong" className="text-sm text-zinc-500 hover:underline">
          ← Khách thuê & hợp đồng
        </Link>
        <h1 className="mt-1 text-xl font-semibold text-zinc-900">
          Hợp đồng {hopDong.maHopDong}
        </h1>
      </div>

      <div className="grid grid-cols-2 gap-x-6 gap-y-2 rounded-lg border border-zinc-200 bg-white p-4 text-sm sm:grid-cols-3">
        <InfoRow nhan="Phòng" giaTri={hopDong.maPhong} />
        <InfoRow nhan="Khách thuê" giaTri={`${hopDong.tenKhach} (${hopDong.maKhach})`} />
        <InfoRow nhan="Trạng thái" giaTri={TEN_TRANG_THAI_HOP_DONG[hopDong.trangThai]} />
        <InfoRow nhan="Từ ngày" giaTri={formatNgay(hopDong.ngayBatDau)} />
        <InfoRow nhan="Đến ngày" giaTri={formatNgay(hopDong.ngayHetHan)} />
        <InfoRow nhan="Ngày nhận phòng" giaTri={formatNgay(hopDong.ngayNhanPhong)} />
        <InfoRow nhan="Giá thuê" giaTri={formatTien(hopDong.giaThue)} />
        <InfoRow nhan="Tiền cọc" giaTri={formatTien(hopDong.tienCoc)} />
        <InfoRow nhan="Mức phạt trả trước" giaTri={formatTien(hopDong.mucPhatTraTruoc)} />
        {hopDong.ngayBaoTra && (
          <>
            <InfoRow nhan="Ngày báo trả" giaTri={formatNgay(hopDong.ngayBaoTra)} />
            <InfoRow nhan="Ngày trả dự kiến" giaTri={formatNgay(hopDong.ngayTraDuKien)} />
            <InfoRow
              nhan="Loại kết thúc"
              giaTri={hopDong.loaiKetThuc ? TEN_LOAI_KET_THUC[hopDong.loaiKetThuc] : "—"}
            />
            <InfoRow nhan="Đã thu hồi chìa khóa" giaTri={hopDong.daThuHoiChiaKhoa ? "Có" : "Chưa"} />
          </>
        )}
      </div>

      {choNhanPhong && (
        <Link
          href={`/cn/nhan-phong/${maHopDong}`}
          className="self-start rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Xử lý nhận phòng
        </Link>
      )}

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <div className="border-b border-zinc-200 px-4 py-2 text-sm font-semibold text-zinc-900">
          Người ở cùng
        </div>
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Họ tên</th>
              <th className="px-4 py-2">Số CCCD</th>
              <th className="px-4 py-2">Điện thoại</th>
              <th className="px-4 py-2">Ngày vào</th>
              <th className="px-4 py-2">Ngày ra</th>
              <th className="px-4 py-2" />
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {dsNguoiO.map((n) => (
              <tr key={n.maNguoiO}>
                <td className="px-4 py-2 font-medium text-zinc-900">{n.hoTen}</td>
                <td className="px-4 py-2">{n.soCCCD ?? "—"}</td>
                <td className="px-4 py-2">{n.soDienThoai ?? "—"}</td>
                <td className="px-4 py-2">{formatNgay(n.ngayVao)}</td>
                <td className="px-4 py-2">{formatNgay(n.ngayRa)}</td>
                <td className="px-4 py-2 text-right">
                  {!n.ngayRa && <NguoiORoiDiButton maHopDong={maHopDong} maNguoiO={n.maNguoiO} />}
                </td>
              </tr>
            ))}
            {dsNguoiO.length === 0 && (
              <tr>
                <td colSpan={6} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có người ở cùng.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {hieuLuc && <ThemNguoiOForm maHopDong={maHopDong} />}

      {chuaBaoTra && (
        <>
          <GiaHanForm maHopDong={maHopDong} />
          <DieuChinhGiaForm maHopDong={maHopDong} />
          <BaoTraPhongForm maHopDong={maHopDong} />
        </>
      )}

      {choThuHoiChiaKhoa && <ThuHoiChiaKhoaButton maHopDong={maHopDong} />}
    </div>
  );
}

function InfoRow({ nhan, giaTri }: { nhan: string; giaTri: string }) {
  return (
    <div className="flex justify-between gap-2 sm:block">
      <dt className="text-xs text-zinc-500">{nhan}</dt>
      <dd className="font-medium text-zinc-900">{giaTri}</dd>
    </div>
  );
}
