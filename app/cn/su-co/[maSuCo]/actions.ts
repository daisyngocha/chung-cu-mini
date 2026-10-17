"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type SuCoFormState = { error?: string };

function layLoi(e: unknown, macDinh: string) {
  return { error: e instanceof BackendError ? e.message : macDinh };
}

export async function tiepNhan(
  maSuCo: string,
  _prevState: SuCoFormState,
  formData: FormData,
): Promise<SuCoFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const lichHen = String(formData.get("lichHen") ?? "").trim();

  if (!lichHen) {
    return { error: "Vui lòng chọn lịch hẹn xử lý." };
  }

  try {
    await goiBackend(`/api/su-co/${maSuCo}/tiep-nhan`, {
      method: "PATCH",
      token,
      body: { lichHen },
    });
  } catch (e) {
    return layLoi(e, "Không tiếp nhận được sự cố. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/su-co/${maSuCo}`);
  revalidatePath("/cn/su-co");
  return {};
}

export async function tuChoi(
  maSuCo: string,
  _prevState: SuCoFormState,
  formData: FormData,
): Promise<SuCoFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const lyDo = String(formData.get("lyDo") ?? "").trim();

  if (!lyDo) {
    return { error: "Vui lòng nhập lý do từ chối." };
  }

  try {
    await goiBackend(`/api/su-co/${maSuCo}/tu-choi`, {
      method: "PATCH",
      token,
      body: { lyDo },
    });
  } catch (e) {
    return layLoi(e, "Không từ chối được sự cố. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/su-co/${maSuCo}`);
  revalidatePath("/cn/su-co");
  return {};
}

export async function hoanThanh(
  maSuCo: string,
  _prevState: SuCoFormState,
  formData: FormData,
): Promise<SuCoFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const ketQua = String(formData.get("ketQua") ?? "").trim();
  const chiPhi = String(formData.get("chiPhi") ?? "").trim();
  const doKhachGay = formData.get("doKhachGay") === "on";
  const anh = String(formData.get("anh") ?? "").trim();

  if (!ketQua) {
    return { error: "Vui lòng nhập kết quả xử lý." };
  }

  try {
    await goiBackend(`/api/su-co/${maSuCo}/hoan-thanh`, {
      method: "PATCH",
      token,
      body: {
        ketQua,
        chiPhi: chiPhi ? Number(chiPhi) : null,
        doKhachGay,
        anh: anh || null,
      },
    });
  } catch (e) {
    return layLoi(e, "Không hoàn thành được sự cố. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/su-co/${maSuCo}`);
  revalidatePath("/cn/su-co");
  return {};
}
