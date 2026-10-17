import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { CreateBangGiaForm } from "../create-bang-gia-form";
import { BangGiaTable, type BangGiaApi } from "../bang-gia-table";

const LOAI_PHI_DICH_VU = ["DICH_VU_THEO_PHONG", "DICH_VU_THEO_XE"];
const TUY_CHON_LOAI = [
  { value: "DICH_VU_THEO_PHONG", label: "Dịch vụ theo phòng" },
  { value: "DICH_VU_THEO_XE", label: "Dịch vụ theo xe" },
];

export default async function BangGiaPhiDichVuPage() {
  const token = await getSessionToken();
  const danhSach = await goiBackend<BangGiaApi[]>("/api/bang-gia", { token });
  const danhSachPhiDichVu = danhSach.filter((bg) => LOAI_PHI_DICH_VU.includes(bg.loaiKhoan));

  return (
    <div>
      <h1 className="mb-4 text-xl font-semibold text-zinc-900">Phí dịch vụ cố định</h1>
      <CreateBangGiaForm loaiKhoanOptions={TUY_CHON_LOAI} />
      <BangGiaTable danhSach={danhSachPhiDichVu} />
    </div>
  );
}
