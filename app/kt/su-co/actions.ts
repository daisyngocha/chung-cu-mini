"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type BaoSuCoFormState = { error?: string };

export async function baoSuCo(
  _prevState: BaoSuCoFormState,
  formData: FormData,
): Promise<BaoSuCoFormState> {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();

  const loaiSuCo = String(formData.get("loaiSuCo") ?? "").trim();
  const moTa = String(formData.get("moTa") ?? "").trim();
  const anh = String(formData.get("anh") ?? "").trim();

  if (!loaiSuCo || !moTa) {
    return { error: "Vui lòng chọn loại sự cố và mô tả." };
  }

  try {
    await goiBackend("/api/khach/su-co", {
      method: "POST",
      token,
      body: { loaiSuCo, moTa, anh: anh || null },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không báo được sự cố. Vui lòng thử lại.",
    };
  }

  revalidatePath("/kt/su-co");
  return {};
}
