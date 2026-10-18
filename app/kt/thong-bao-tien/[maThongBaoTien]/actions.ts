"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type BaoChuyenKhoanFormState = { error?: string; thanhCong?: boolean };

export async function baoDaChuyenKhoan(
  maThongBaoTien: string,
  _prevState: BaoChuyenKhoanFormState,
  formData: FormData,
): Promise<BaoChuyenKhoanFormState> {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();
  const anhBienLai = String(formData.get("anhBienLai") ?? "").trim();

  try {
    await goiBackend("/api/khach/bao-thanh-toan", {
      method: "POST",
      token,
      body: { maThongBaoTien, anhBienLai: anhBienLai || null },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không gửi được báo chuyển khoản. Vui lòng thử lại.",
    };
  }

  revalidatePath(`/kt/thong-bao-tien/${maThongBaoTien}`);
  return { thanhCong: true };
}
