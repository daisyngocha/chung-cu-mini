import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { formatTien, TEN_TRANG_THAI_PHONG } from "@/lib/format";
import { CreatePhongForm } from "./create-phong-form";
import { chuyenTrangThaiPhong } from "./actions";

type PhongApi = {
  maPhong: string;
  tang: number;
  dienTich: number;
  soNguoiToiDa: number;
  giaNiemYet: number;
  coCongToNuoc: boolean;
  trangThai: "TRONG" | "DANG_THUE" | "NGUNG_KHAI_THAC";
};

export default async function DanhSachPhongPage() {
  const token = await getSessionToken();
  const danhSachPhong = await goiBackend<PhongApi[]>("/api/phong", { token });

  return (
    <div>
      <h1 className="mb-4 text-xl font-semibold text-zinc-900">
        Phòng & bảng giá
      </h1>

      <CreatePhongForm />

      <div className="overflow-hidden rounded-lg border border-zinc-200 bg-white">
        <table className="w-full text-sm">
          <thead className="bg-zinc-50 text-left text-xs font-medium text-zinc-500">
            <tr>
              <th className="px-4 py-2">Mã phòng</th>
              <th className="px-4 py-2">Tầng</th>
              <th className="px-4 py-2">Diện tích</th>
              <th className="px-4 py-2">Số người tối đa</th>
              <th className="px-4 py-2">Giá niêm yết</th>
              <th className="px-4 py-2">Công tơ nước riêng</th>
              <th className="px-4 py-2">Trạng thái</th>
              <th className="px-4 py-2" />
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {danhSachPhong.map((phong) => (
              <tr key={phong.maPhong}>
                <td className="px-4 py-2 font-medium text-zinc-900">
                  {phong.maPhong}
                </td>
                <td className="px-4 py-2">{phong.tang}</td>
                <td className="px-4 py-2">{phong.dienTich} m²</td>
                <td className="px-4 py-2">{phong.soNguoiToiDa}</td>
                <td className="px-4 py-2">{formatTien(phong.giaNiemYet)}</td>
                <td className="px-4 py-2">{phong.coCongToNuoc ? "Có" : "Không"}</td>
                <td className="px-4 py-2">
                  <span
                    className={
                      "rounded-full px-2 py-0.5 text-xs font-medium " +
                      (phong.trangThai === "TRONG"
                        ? "bg-green-100 text-green-700"
                        : phong.trangThai === "DANG_THUE"
                          ? "bg-blue-100 text-blue-700"
                          : "bg-zinc-100 text-zinc-600")
                    }
                  >
                    {TEN_TRANG_THAI_PHONG[phong.trangThai]}
                  </span>
                </td>
                <td className="px-4 py-2 text-right">
                  {phong.trangThai !== "DANG_THUE" && (
                    <form
                      action={chuyenTrangThaiPhong.bind(null, phong.maPhong)}
                      className="inline"
                    >
                      <button
                        type="submit"
                        className="text-xs font-medium text-zinc-600 hover:underline"
                      >
                        {phong.trangThai === "TRONG"
                          ? "Ngừng khai thác"
                          : "Khai thác lại"}
                      </button>
                    </form>
                  )}
                </td>
              </tr>
            ))}
            {danhSachPhong.length === 0 && (
              <tr>
                <td colSpan={8} className="px-4 py-6 text-center text-zinc-500">
                  Chưa có phòng nào. Thêm phòng đầu tiên ở trên.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
