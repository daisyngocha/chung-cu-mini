"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type HopDongFormState = { error?: string };

export async function taoHopDong(
  _prevState: HopDongFormState,
  formData: FormData,
): Promise<HopDongFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const maPhong = String(formData.get("maPhong") ?? "").trim();
  const maKhach = String(formData.get("maKhach") ?? "").trim();
  const ngayBatDau = String(formData.get("ngayBatDau") ?? "").trim();
  const ngayHetHan = String(formData.get("ngayHetHan") ?? "").trim();
  const giaThue = String(formData.get("giaThue") ?? "").trim();
  const tienCoc = String(formData.get("tienCoc") ?? "").trim();
  const dsDichVu = formData.getAll("dsDichVu").map((maKhoan) => ({ maKhoan: String(maKhoan), soLuong: 1 }));

  if (!maPhong || !maKhach || !ngayBatDau || !ngayHetHan) {
    return { error: "Vui lòng chọn phòng, nhập mã khách và thời hạn hợp đồng." };
  }

  let maHopDong: string;
  try {
    const hopDong = await goiBackend<{ maHopDong: string }>("/api/hop-dong", {
      method: "POST",
      token,
      body: {
        maPhong,
        maKhach,
        ngayBatDau,
        ngayHetHan,
        giaThue: giaThue ? Number(giaThue) : null,
        tienCoc: tienCoc ? Number(tienCoc) : null,
        dsDichVu,
      },
    });
    maHopDong = hopDong.maHopDong;
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không tạo được hợp đồng. Vui lòng thử lại.",
    };
  }

  revalidatePath("/cn/hop-dong");
  revalidatePath("/cn/phong");
  redirect(`/cn/hop-dong/${maHopDong}`);
}
