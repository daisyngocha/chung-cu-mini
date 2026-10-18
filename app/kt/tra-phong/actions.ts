"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type TraPhongFormState = { error?: string };

export async function baoTraPhong(
  _prevState: TraPhongFormState,
  formData: FormData,
): Promise<TraPhongFormState> {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();
  const ngayTraDuKien = String(formData.get("ngayTraDuKien") ?? "").trim();

  if (!ngayTraDuKien) {
    return { error: "Vui lòng chọn ngày trả dự kiến." };
  }

  try {
    await goiBackend("/api/khach/hop-dong/bao-tra", {
      method: "PATCH",
      token,
      body: { ngayTraDuKien },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không báo trả phòng được. Vui lòng thử lại.",
    };
  }

  revalidatePath("/kt/tra-phong");
  revalidatePath("/kt/hop-dong");
  return {};
}

export async function phanHoiQuyetToan(
  maQuyetToan: string,
  _prevState: TraPhongFormState,
  formData: FormData,
): Promise<TraPhongFormState> {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();
  const dongY = formData.get("dongY") === "true";
  const yKien = String(formData.get("yKien") ?? "").trim();

  if (!dongY && !yKien) {
    return { error: "Vui lòng nhập ý kiến khi không đồng ý." };
  }

  try {
    await goiBackend(`/api/khach/quyet-toan/${maQuyetToan}/phan-hoi`, {
      method: "PATCH",
      token,
      body: { dongY, yKien: dongY ? null : yKien },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không gửi được phản hồi. Vui lòng thử lại.",
    };
  }

  revalidatePath("/kt/tra-phong");
  return {};
}
