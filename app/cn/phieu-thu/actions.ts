"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type PhieuThuFormState = { error?: string };

export async function huyPhieuThu(
  soPhieuThu: string,
  maThongBaoTien: string,
  _prevState: PhieuThuFormState,
  formData: FormData,
): Promise<PhieuThuFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const lyDo = String(formData.get("lyDo") ?? "").trim();
  if (!lyDo) {
    return { error: "Vui lòng nhập lý do hủy." };
  }

  try {
    await goiBackend(`/api/phieu-thu/${soPhieuThu}/huy`, {
      method: "PATCH",
      token,
      body: { lyDo },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không hủy được phiếu thu. Vui lòng thử lại.",
    };
  }

  revalidatePath(`/cn/phieu-thu?maThongBaoTien=${maThongBaoTien}`);
  revalidatePath(`/cn/thong-bao-tien/${maThongBaoTien}`);
  return {};
}
