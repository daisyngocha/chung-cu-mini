import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, formatNgay } from "@/lib/format";
import {
  ThemKhoanTruForm,
  SuaKhoanTruForm,
  XoaKhoanTruForm,
  GuiKhachButton,
  ChotQuyetToanForm,
  LapPhieuChiForm,
  LapThongBaoThuThemButton,
} from "./forms";

type DongApi = {
  maDong: string;
  loaiKhoan: string;
  soTien: number;
  ghiChu: string | null;
  anhBangChung: string | null;
};

type QuyetToanApi = {
  maQuyetToan: string;
  maHopDong: string;
  maPhong: string;
  ngayTra: string;
  tienCoc: number;
  tienPhongHoanLai: number;
  tongKhauTru: number;
  soTienQuyetToan: number;
  trangThai: "NHAP" | "CHO_PHAN_HOI" | "KHACH_KHONG_DONG_Y" | "DA_CHOT" | "HOAN_TAT";
  lyDoDieuChinh: string | null;
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

const TEN_TRANG_THAI_QT: Record<string, string> = {
  NHAP: "Nháp",
  CHO_PHAN_HOI: "Chờ khách phản hồi",
  KHACH_KHONG_DONG_Y: "Khách không đồng ý",
  DA_CHOT: "Đã chốt",
  HOAN_TAT: "Hoàn tất",
};

const DONG_KHONG_SUA_XOA = new Set(["TIEN_COC", "HOAN_TIEN_PHONG"]);

export default async function QuyetToanDetailPage({
  params,
}: {
  params: Promise<{ maQuyetToan: string }>;
}) {
  const { maQuyetToan } = await params;
  const token = await getSessionToken();
  const qt = await goiBackend<QuyetToanApi>(`/api/quyet-toan/${maQuyetToan}`, { token });

  const dangSua = qt.trangThai === "NHAP" || qt.trangThai === "KHACH_KHONG_DONG_Y";
  const choChot = qt.trangThai === "CHO_PHAN_HOI" || qt.trangThai === "KHACH_KHONG_DONG_Y";

  let phieuChi: { soPhieuChi: string } | null = null;
  let thongBaoThuThem: { maThongBaoTien: string } | null = null;
  if (qt.trangThai === "HOAN_TAT") {
    [phieuChi, thongBaoThuThem] = await Promise.all([
      goiBackend<{ soPhieuChi: string } | null>(`/api/quyet-toan/${maQuyetToan}/phieu-chi`, { token }),
      goiBackend<{ maThongBaoTien: string } | null>(`/api/quyet-toan/${maQuyetToan}/thong-bao-thu-them`, { token }),
    ]);
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <Link href={`/cn/hop-dong/${qt.maHopDong}`} className="text-sm text-zinc-500 hover:underline">
          ← Hợp đồng {qt.maHopDong}
        </Link>
        <h1 className="mt-1 text-xl font-semibold text-zinc-900">
          Quyết toán cọc {qt.maQuyetToan} — phòng {qt.maPhong}
        </h1>
      </div>

      <div className="grid grid-cols-2 gap-x-6 gap-y-2 rounded-lg border border-zinc-200 bg-white p-4 text-sm sm:grid-cols-3">
        <InfoRow nhan="Ngày trả" giaTri={formatNgay(qt.ngayTra)} />
        <InfoRow nhan="Trạng thái" giaTri={TEN_TRANG_THAI_QT[qt.trangThai]} />
        <InfoRow nhan="Tiền cọc" giaTri={formatTien(qt.tienCoc)} />
        <InfoRow nhan="Tiền phòng hoàn lại" giaTri={formatTien(qt.tienPhongHoanLai)} />
        <InfoRow nhan="Tổng khấu trừ" giaTri={formatTien(qt.tongKhauTru)} />
        <InfoRow
          nhan={qt.soTienQuyetToan >= 0 ? "Số tiền hoàn lại khách" : "Số tiền khách còn nợ"}
          giaTri={formatTien(Math.abs(qt.soTienQuyetToan))}
        />
        {qt.yKienKhach && <InfoRow nhan="Ý kiến khách" giaTri={qt.yKienKhach} />}
        {qt.lyDoDieuChinh && <InfoRow nhan="Lý do điều chỉnh/chốt" giaTri={qt.lyDoDieuChinh} />}
      </div>

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Loại khoản</th>
              <th className="px-4 py-2">Số tiền</th>
              <th className="px-4 py-2">Ghi chú</th>
              <th className="px-4 py-2">Ảnh bằng chứng</th>
              {dangSua && <th className="px-4 py-2" />}
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {qt.dong.map((d) => (
              <tr key={d.maDong}>
                <td className="px-4 py-2 font-medium text-zinc-900">{TEN_LOAI_KHOAN_QT[d.loaiKhoan] ?? d.loaiKhoan}</td>
                <td className="px-4 py-2">{formatTien(d.soTien)}</td>
                <td className="px-4 py-2">{d.ghiChu ?? "—"}</td>
                <td className="px-4 py-2">
                  {d.anhBangChung ? (
                    <a href={d.anhBangChung} target="_blank" rel="noreferrer" className="underline">
                      Xem ảnh
                    </a>
                  ) : (
                    "—"
                  )}
                </td>
                {dangSua && (
                  <td className="px-4 py-2">
                    {!DONG_KHONG_SUA_XOA.has(d.loaiKhoan) && (
                      <div className="flex flex-col items-end gap-2">
                        <SuaKhoanTruForm maQuyetToan={maQuyetToan} maDong={d.maDong} />
                        <XoaKhoanTruForm maQuyetToan={maQuyetToan} maDong={d.maDong} />
                      </div>
                    )}
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {dangSua && (
        <>
          <ThemKhoanTruForm maQuyetToan={maQuyetToan} />
          <GuiKhachButton maQuyetToan={maQuyetToan} />
        </>
      )}

      {choChot && <ChotQuyetToanForm maQuyetToan={maQuyetToan} />}

      {qt.trangThai === "DA_CHOT" && qt.soTienQuyetToan > 0 && <LapPhieuChiForm maQuyetToan={maQuyetToan} />}
      {qt.trangThai === "DA_CHOT" && qt.soTienQuyetToan < 0 && (
        <LapThongBaoThuThemButton maQuyetToan={maQuyetToan} />
      )}
      {qt.trangThai === "DA_CHOT" && qt.soTienQuyetToan === 0 && (
        <p className="rounded-md bg-amber-50 px-3 py-2 text-sm text-amber-700">
          Số quyết toán bằng 0 — hệ thống hiện chưa có thao tác tự động hoàn tất cho trường hợp này, cần xử lý thủ công.
        </p>
      )}

      {qt.trangThai === "HOAN_TAT" && (
        <div className="rounded-lg border border-zinc-200 bg-white p-4 text-sm">
          <h3 className="mb-2 font-semibold text-zinc-900">Đã hoàn tất quyết toán</h3>
          {phieuChi && (
            <p>
              Phiếu chi:{" "}
              <a href={`/cn/phieu-chi/${phieuChi.soPhieuChi}/file`} className="underline">
                {phieuChi.soPhieuChi} (tải PDF)
              </a>
            </p>
          )}
          {thongBaoThuThem && (
            <p>
              Thông báo thu thêm:{" "}
              <Link href={`/cn/thong-bao-tien/${thongBaoThuThem.maThongBaoTien}`} className="underline">
                {thongBaoThuThem.maThongBaoTien}
              </Link>
            </p>
          )}
          <p className="mt-2 text-zinc-600">
            Quay lại{" "}
            <Link href={`/cn/hop-dong/${qt.maHopDong}`} className="underline">
              trang hợp đồng
            </Link>{" "}
            để thanh lý hợp đồng.
          </p>
        </div>
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
