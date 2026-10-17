"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type PhongFormState = { error?: string };

export async function taoPhong(
  _prevState: PhongFormState,
  formData: FormData,
): Promise<PhongFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const maPhong = String(formData.get("maPhong") ?? "").trim();
  const tang = Number(formData.get("tang"));
  const dienTich = String(formData.get("dienTich") ?? "").trim();
  const soNguoiToiDa = Number(formData.get("soNguoiToiDa"));
  const giaNiemYet = String(formData.get("giaNiemYet") ?? "").trim();
  const coCongToNuoc = formData.get("coCongToNuoc") === "on";

  if (!maPhong || !Number.isFinite(tang) || !Number.isFinite(soNguoiToiDa)) {
    return { error: "Vui lòng nhập đầy đủ mã phòng, tầng và số người tối đa." };
  }
  if (!dienTich || !giaNiemYet) {
    return { error: "Vui lòng nhập diện tích và giá niêm yết." };
  }

  try {
    await goiBackend("/api/phong", {
      method: "POST",
      token,
      body: {
        maPhong,
        tang,
        dienTich: Number(dienTich),
        soNguoiToiDa,
        giaNiemYet: Number(giaNiemYet),
        coCongToNuoc,
      },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không thêm được phòng. Vui lòng thử lại.",
    };
  }

  revalidatePath("/cn/phong");
  return {};
}

export async function chuyenTrangThaiPhong(maPhong: string) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  await goiBackend(`/api/phong/${maPhong}/trang-thai`, { method: "PATCH", token });
  revalidatePath("/cn/phong");
}

// TODO: cổng "Xóa phòng" (BR-DM-03) sang Java khi HopDong được map sang Entity —
// cần đếm số hợp đồng theo phòng trước khi cho xóa.
