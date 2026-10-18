"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type CapNhatThongTinFormState = { error?: string; thanhCong?: boolean };

export async function capNhatThongTin(
  _prevState: CapNhatThongTinFormState,
  formData: FormData,
): Promise<CapNhatThongTinFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const tenToa = String(formData.get("tenToa") ?? "").trim();
  const diaChi = String(formData.get("diaChi") ?? "").trim();
  const noiQuy = String(formData.get("noiQuy") ?? "").trim();
  const ngayChotChiSo = String(formData.get("ngayChotChiSo") ?? "").trim();
  const hanThanhToan = String(formData.get("hanThanhToan") ?? "").trim();
  const tenChuNha = String(formData.get("tenChuNha") ?? "").trim();
  const sdtChuNha = String(formData.get("sdtChuNha") ?? "").trim();
  const diaChiChuNha = String(formData.get("diaChiChuNha") ?? "").trim();

  if (!tenToa || !diaChi || !ngayChotChiSo || !hanThanhToan || !tenChuNha || !sdtChuNha) {
    return { error: "Vui lòng nhập đầy đủ các trường bắt buộc." };
  }

  try {
    await goiBackend("/api/toa-nha/mac-dinh/thong-tin", {
      method: "PATCH",
      token,
      body: {
        tenToa,
        diaChi,
        noiQuy: noiQuy || null,
        ngayChotChiSo: Number(ngayChotChiSo),
        hanThanhToan: Number(hanThanhToan),
        tenChuNha,
        sdtChuNha,
        diaChiChuNha: diaChiChuNha || null,
      },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không lưu được thông tin tòa. Vui lòng thử lại.",
    };
  }

  revalidatePath("/cn/cai-dat/toa-nha");
  revalidatePath("/cn");
  return { thanhCong: true };
}
