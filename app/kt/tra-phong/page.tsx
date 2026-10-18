import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay } from "@/lib/format";
import { BaoTraPhongForm, PhanHoiQuyetToanForm } from "./forms";

type HopDongApi = {
  ngayHetHan: string;
  ngayBaoTra: string | null;
  ngayTraDuKien: string | null;
  trangThai: "CHO_NHAN_PHONG" | "HIEU_LUC" | "DA_THANH_LY";
};

type DongApi = { maDong: string; loaiKhoan: string; soTien: number; ghiChu: string | null };

type QuyetToanApi = {
  maQuyetToan: string;
  tienCoc: number;
  tienPhongHoanLai: number;
  tongKhauTru: number;
  soTienQuyetToan: number;
  trangThai: "CHO_PHAN_HOI" | "KHACH_KHONG_DONG_Y" | "DA_CHOT" | "HOAN_TAT";
  khachDongY: boolean | null;
  yKienKhach: string | null;
  dong: DongApi[];
};

const TEN_LOAI_KHOAN_QT: Record<string, string> = {
  TIEN_COC: "Tiền cọc",
  HOAN_TIEN_PHONG: "Hoàn tiền phòng",
  DIEN: "Điện",
  NUOC: "Nước",
  DICH_VU: "Dịch vụ",
  NO_CHUA_TRA: "Nợ chưa trả",
  BOI_THUONG_HU_HONG: "Bồi thường hư hỏng",
  PHAT_TRA_TRUOC_HAN: "Phạt trả trước hạn",
  KHAC: "Khác",
};

export default async function KhachTraPhongPage() {
  const token = await getSessionToken();
  const hopDong = await goiBackend<HopDongApi>("/api/khach/hop-dong", { token });

  let quyetToan: QuyetToanApi | null = null;
  if (hopDong.ngayBaoTra) {
    try {
      quyetToan = await goiBackend<QuyetToanApi>("/api/khach/quyet-toan", { token });
    } catch {
      quyetToan = null;
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Trả phòng & quyết toán</h1>

      {!hopDong.ngayBaoTra && hopDong.trangThai === "HIEU_LUC" && (
        <BaoTraPhongForm ngayHetHan={hopDong.ngayHetHan} />
      )}

      {hopDong.ngayBaoTra && !quyetToan && (
        <p className="rounded-md bg-blue-50 px-3 py-2 text-sm text-blue-700">
          Đã báo trả phòng, dự kiến ngày {formatNgay(hopDong.ngayTraDuKien)}. Đang chờ chủ nhà kiểm
          tra và lập quyết toán cọc.
        </p>
      )}

      {quyetToan && (
        <>
          <div className="grid grid-cols-2 gap-x-6 gap-y-2 rounded-lg border border-zinc-200 bg-white p-4 text-sm sm:grid-cols-3">
            <InfoRow nhan="Tiền cọc" giaTri={formatTien(quyetToan.tienCoc)} />
            <InfoRow nhan="Tiền phòng hoàn lại" giaTri={formatTien(quyetToan.tienPhongHoanLai)} />
            <InfoRow nhan="Tổng khấu trừ" giaTri={formatTien(quyetToan.tongKhauTru)} />
            <InfoRow
              nhan={quyetToan.soTienQuyetToan >= 0 ? "Số tiền được hoàn lại" : "Số tiền còn phải trả thêm"}
              giaTri={formatTien(Math.abs(quyetToan.soTienQuyetToan))}
            />
            {quyetToan.yKienKhach && <InfoRow nhan="Ý kiến của bạn" giaTri={quyetToan.yKienKhach} />}
          </div>

          <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
            <table className="w-full text-sm">
              <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
                <tr>
                  <th className="px-4 py-2">Khoản</th>
                  <th className="px-4 py-2">Số tiền</th>
                  <th className="px-4 py-2">Ghi chú</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-zinc-100">
                {quyetToan.dong.map((d) => (
                  <tr key={d.maDong}>
                    <td className="px-4 py-2 font-medium text-zinc-900">{TEN_LOAI_KHOAN_QT[d.loaiKhoan] ?? d.loaiKhoan}</td>
                    <td className="px-4 py-2">{formatTien(d.soTien)}</td>
                    <td className="px-4 py-2">{d.ghiChu ?? "—"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {quyetToan.trangThai === "CHO_PHAN_HOI" && quyetToan.khachDongY === null && (
            <PhanHoiQuyetToanForm maQuyetToan={quyetToan.maQuyetToan} />
          )}
          {quyetToan.trangThai !== "CHO_PHAN_HOI" && (
            <p className="text-sm text-zinc-500">
              Trạng thái: {quyetToan.trangThai === "KHACH_KHONG_DONG_Y" ? "Bạn đã phản hồi không đồng ý" : "Đã xử lý"}.
            </p>
          )}
        </>
      )}
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
