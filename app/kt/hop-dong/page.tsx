import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay, TEN_TRANG_THAI_HOP_DONG } from "@/lib/format";

type NguoiOApi = {
  maNguoiO: string;
  hoTen: string;
  soCCCD: string | null;
  soDienThoai: string | null;
  ngayVao: string | null;
  ngayRa: string | null;
};

type HopDongApi = {
  maHopDong: string;
  maPhong: string;
  tenKhach: string;
  soCCCDKhach: string | null;
  ngayBatDau: string;
  ngayHetHan: string;
  ngayNhanPhong: string | null;
  giaThue: number;
  tienCoc: number;
  anhHopDong: string | null;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
  dsNguoiO: NguoiOApi[];
};

function sapHetHan(ngayHetHan: string) {
  const ngay = new Date(ngayHetHan);
  const conLai = (ngay.getTime() - Date.now()) / (1000 * 60 * 60 * 24);
  return conLai >= 0 && conLai <= 30;
}

export default async function KhachHopDongPage() {
  const token = await getSessionToken();
  const hopDong = await goiBackend<HopDongApi>("/api/khach/hop-dong", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Hồ sơ & hợp đồng</h1>

      {hopDong.trangThai === "HIEU_LUC" && sapHetHan(hopDong.ngayHetHan) && (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Hợp đồng sắp hết hạn vào ngày {formatNgay(hopDong.ngayHetHan)}.
        </p>
      )}

      <div className="grid grid-cols-2 gap-x-6 gap-y-2 rounded-lg border border-zinc-200 bg-white p-4 text-sm sm:grid-cols-3">
        <InfoRow nhan="Mã hợp đồng" giaTri={hopDong.maHopDong} />
        <InfoRow nhan="Phòng" giaTri={hopDong.maPhong} />
        <InfoRow nhan="Trạng thái" giaTri={TEN_TRANG_THAI_HOP_DONG[hopDong.trangThai]} />
        <InfoRow nhan="Người đứng tên" giaTri={hopDong.tenKhach} />
        <InfoRow nhan="Số CCCD" giaTri={hopDong.soCCCDKhach ?? "—"} />
        <InfoRow nhan="Ngày nhận phòng" giaTri={formatNgay(hopDong.ngayNhanPhong)} />
        <InfoRow nhan="Từ ngày" giaTri={formatNgay(hopDong.ngayBatDau)} />
        <InfoRow nhan="Đến ngày" giaTri={formatNgay(hopDong.ngayHetHan)} />
        <InfoRow nhan="Giá thuê" giaTri={formatTien(hopDong.giaThue)} />
        <InfoRow nhan="Tiền cọc" giaTri={formatTien(hopDong.tienCoc)} />
      </div>

      {hopDong.anhHopDong && (
        <div className="rounded-lg border border-zinc-200 bg-white p-4">
          <a href={hopDong.anhHopDong} target="_blank" rel="noreferrer" className="text-sm underline">
            Xem ảnh hợp đồng
          </a>
        </div>
      )}

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <div className="border-b border-zinc-200 px-4 py-2 text-sm font-semibold text-zinc-900">
          Người ở cùng
        </div>
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Họ tên</th>
              <th className="px-4 py-2">Điện thoại</th>
              <th className="px-4 py-2">Ngày vào</th>
              <th className="px-4 py-2">Ngày ra</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {hopDong.dsNguoiO.map((n) => (
              <tr key={n.maNguoiO}>
                <td className="px-4 py-2 font-medium text-zinc-900">{n.hoTen}</td>
                <td className="px-4 py-2">{n.soDienThoai ?? "—"}</td>
                <td className="px-4 py-2">{formatNgay(n.ngayVao)}</td>
                <td className="px-4 py-2">{formatNgay(n.ngayRa)}</td>
              </tr>
            ))}
            {hopDong.dsNguoiO.length === 0 && (
              <tr>
                <td colSpan={4} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có người ở cùng.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
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
