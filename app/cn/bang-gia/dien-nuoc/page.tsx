import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { CreateBangGiaForm } from "../create-bang-gia-form";
import { BangGiaTable, type BangGiaApi } from "../bang-gia-table";

const LOAI_DIEN_NUOC = ["DIEN", "NUOC", "NUOC_THEO_NGUOI"];
const TUY_CHON_LOAI = [
  { value: "DIEN", label: "Điện" },
  { value: "NUOC", label: "Nước (theo đồng hồ)" },
  { value: "NUOC_THEO_NGUOI", label: "Nước (theo người)" },
];

export default async function BangGiaDienNuocPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<BangGiaApi[]>("/api/bang-gia", { token });
  const danhSachDienNuoc = danhSach.filter((bg) => LOAI_DIEN_NUOC.includes(bg.loaiKhoan));

  return (
    <div>
      <h1 className="mb-4 text-xl font-semibold text-zinc-900">Đơn giá điện, nước</h1>
      <CreateBangGiaForm loaiKhoanOptions={TUY_CHON_LOAI} />
      <BangGiaTable danhSach={danhSachDienNuoc} />
    </div>
  );
}
