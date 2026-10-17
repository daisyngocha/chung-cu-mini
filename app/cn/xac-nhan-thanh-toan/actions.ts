"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type BaoThanhToanFormState = { error?: string };

function layLoi(e: unknown, macDinh: string) {
  return { error: e instanceof BackendError ? e.message : macDinh };
}

export async function xacNhanBaoThanhToan(
  maBaoTT: string,
  _prevState: BaoThanhToanFormState,
  formData: FormData,
): Promise<BaoThanhToanFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const soTienThucNhan = String(formData.get("soTienThucNhan") ?? "").trim();
  if (!soTienThucNhan) {
    return { error: "Vui lòng nhập số tiền thực nhận." };
  }

  try {
    await goiBackend(`/api/bao-thanh-toan/${maBaoTT}/xac-nhan`, {
      method: "PATCH",
      token,
      body: { soTienThucNhan: Number(soTienThucNhan) },
    });
  } catch (e) {
    return layLoi(e, "Không xác nhận được. Vui lòng thử lại.");
  }

  revalidatePath("/cn/xac-nhan-thanh-toan");
  revalidatePath("/cn/thong-bao-tien");
  return {};
}

export async function tuChoiBaoThanhToan(
  maBaoTT: string,
  _prevState: BaoThanhToanFormState,
  formData: FormData,
): Promise<BaoThanhToanFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const lyDo = String(formData.get("lyDo") ?? "").trim();
  if (!lyDo) {
    return { error: "Vui lòng nhập lý do từ chối." };
  }

  try {
    await goiBackend(`/api/bao-thanh-toan/${maBaoTT}/tu-choi`, {
      method: "PATCH",
      token,
      body: { lyDo },
    });
  } catch (e) {
    return layLoi(e, "Không từ chối được. Vui lòng thử lại.");
  }

  revalidatePath("/cn/xac-nhan-thanh-toan");
  return {};
}
