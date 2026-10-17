import Link from "next/link";
import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";

type ViecCanLamApi = {
  loai: string;
  moTa: string;
  doiTuongLienQuan: string;
};

const TEN_LOAI_VIEC: Record<string, string> = {
  BAO_CK_CHO_XAC_NHAN: "Báo chuyển khoản chờ xác nhận",
  THONG_BAO_QUA_HAN: "Thông báo quá hạn",
  SU_CO_MOI_MO_LAI: "Sự cố mới / mở lại",
  HOP_DONG_SAP_HET_HAN: "Hợp đồng sắp hết hạn",
  PHONG_THIEU_CHI_SO: "Phòng thiếu chỉ số",
  YEU_CAU_TRA_PHONG_MOI: "Yêu cầu trả phòng mới",
};

function duongDanDoiTuong(doiTuongLienQuan: string): string | null {
  const [loai, ma] = doiTuongLienQuan.split(":");
  switch (loai) {
    case "ThongBaoTien":
      return `/cn/thong-bao-tien/${ma}`;
    case "HopDong":
      return `/cn/hop-dong/${ma}`;
    case "SuCo":
      return `/cn/su-co/${ma}`;
    case "BaoThanhToan":
      return "/cn/xac-nhan-thanh-toan";
    default:
      return null;
  }
}

export default async function ViecCanLamPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<ViecCanLamApi[]>("/api/viec-can-lam", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Việc cần làm</h1>
      <p className="text-sm text-zinc-600">{danhSach.length} việc đang chờ xử lý.</p>

      <div className="flex flex-col gap-2">
        {danhSach.map((v, i) => {
          const href = duongDanDoiTuong(v.doiTuongLienQuan);
          const noiDung = (
            <div className="flex items-start justify-between gap-4 rounded-lg border border-zinc-200 bg-white p-4 hover:bg-zinc-50">
              <div>
                <p className="text-xs font-medium text-zinc-500">{TEN_LOAI_VIEC[v.loai] ?? v.loai}</p>
                <p className="mt-1 text-sm text-zinc-900">{v.moTa}</p>
              </div>
            </div>
          );
          return (
            <div key={`${v.doiTuongLienQuan}-${i}`}>
              {href ? (
                <Link href={href}>{noiDung}</Link>
              ) : (
                noiDung
              )}
            </div>
          );
        })}
        {danhSach.length === 0 && (
          <p className="rounded-lg border border-zinc-200 bg-white p-4 text-center text-sm text-zinc-500">
            Không có việc nào cần xử lý.
          </p>
        )}
      </div>
    </div>
  );
}
