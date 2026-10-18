import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { TaiKhoanNhanTienForm } from "./form";

type ToaNhaApi = { nganHang: string; soTaiKhoan: string; chuTaiKhoan: string };

export default async function CaiDatTaiKhoanNhanTienPage() {
  const token = await getSessionToken();
  const toaNha = await goiBackend<ToaNhaApi>("/api/toa-nha/mac-dinh", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Tài khoản nhận tiền & phiếu thu</h1>
      <TaiKhoanNhanTienForm toaNha={toaNha} />
    </div>
  );
}
