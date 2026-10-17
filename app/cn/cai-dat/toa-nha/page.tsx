import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { ThongTinToaForm } from "./thong-tin-toa-form";

type ToaNhaApi = {
  tenToa: string;
  diaChi: string;
  noiQuy: string | null;
  ngayChotChiSo: number;
  hanThanhToan: number;
  tenChuNha: string;
  sdtChuNha: string;
  diaChiChuNha: string | null;
};

export default async function CaiDatToaNhaPage() {
  const token = await getSessionToken();
  const toaNha = await goiBackend<ToaNhaApi>("/api/toa-nha/mac-dinh", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Thông tin tòa</h1>
      <ThongTinToaForm toaNha={toaNha} />
    </div>
  );
}
