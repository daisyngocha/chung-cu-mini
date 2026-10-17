"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type ChiSoFormState = { error?: string };

export async function ghiChiSo(
  maHopDong: string,
  _prevState: ChiSoFormState,
  formData: FormData,
): Promise<ChiSoFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const loaiCongTo = String(formData.get("loaiCongTo") ?? "").trim();
  const loaiGhi = String(formData.get("loaiGhi") ?? "").trim();
  const kyThang = String(formData.get("kyThang") ?? "").trim();
  const chiSoMoi = String(formData.get("chiSoMoi") ?? "").trim();
  const anhCongTo = String(formData.get("anhCongTo") ?? "").trim();

  if (!loaiCongTo || !loaiGhi || !kyThang || !chiSoMoi) {
    return { error: "Vui lòng chọn loại công tơ, loại ghi, kỳ tháng và nhập chỉ số mới." };
  }

  try {
    await goiBackend(`/api/hop-dong/${maHopDong}/chi-so`, {
      method: "POST",
      token,
      body: {
        loaiCongTo,
        loaiGhi,
        kyThang,
        chiSoMoi: Number(chiSoMoi),
        anhCongTo: anhCongTo || null,
      },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không ghi được chỉ số. Vui lòng thử lại.",
    };
  }

  revalidatePath("/cn/chi-so");
  revalidatePath("/cn/nhan-phong");
  return {};
}
