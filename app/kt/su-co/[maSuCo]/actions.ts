"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type MoLaiFormState = { error?: string };

export async function moLaiSuCo(
  maSuCo: string,
  _prevState: MoLaiFormState,
  formData: FormData,
): Promise<MoLaiFormState> {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();
  const ghiChu = String(formData.get("ghiChu") ?? "").trim();

  if (!ghiChu) {
    return { error: "Vui lòng mô tả vì sao sự cố chưa được khắc phục." };
  }

  try {
    await goiBackend(`/api/khach/su-co/${maSuCo}/mo-lai`, {
      method: "PATCH",
      token,
      body: { ghiChu },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không mở lại được sự cố. Vui lòng thử lại.",
    };
  }

  revalidatePath(`/kt/su-co/${maSuCo}`);
  revalidatePath("/kt/su-co");
  return {};
}
