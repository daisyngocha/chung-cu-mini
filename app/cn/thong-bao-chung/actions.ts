"use server";

import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type GuiThongBaoChungFormState = { error?: string; thanhCong?: boolean };

export async function guiThongBaoChung(
  _prevState: GuiThongBaoChungFormState,
  formData: FormData,
): Promise<GuiThongBaoChungFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const tieuDe = String(formData.get("tieuDe") ?? "").trim();
  const noiDung = String(formData.get("noiDung") ?? "").trim();

  if (!tieuDe || !noiDung) {
    return { error: "Vui lòng nhập tiêu đề và nội dung." };
  }

  try {
    await goiBackend("/api/thong-bao/gui-chung", {
      method: "POST",
      token,
      body: { tieuDe, noiDung },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không gửi được thông báo. Vui lòng thử lại.",
    };
  }

  return { thanhCong: true };
}
