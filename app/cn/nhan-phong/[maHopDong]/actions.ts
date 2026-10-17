"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type GhiNhanTinhTrangFormState = { error?: string };

export async function ghiNhanTinhTrangNhanPhong(
  maHopDong: string,
  maTaiSan: string,
  _prevState: GhiNhanTinhTrangFormState,
  formData: FormData,
): Promise<GhiNhanTinhTrangFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const tinhTrang = String(formData.get("tinhTrang") ?? "").trim();
  const anh = String(formData.get("anh") ?? "").trim();

  if (!tinhTrang) {
    return { error: "Vui lòng nhập tình trạng tài sản." };
  }

  try {
    await goiBackend(`/api/hop-dong/${maHopDong}/ban-giao`, {
      method: "POST",
      token,
      body: { maTaiSan, loaiBanGiao: "NHAN_PHONG", tinhTrang, anh: anh || null, chiPhiHuHong: null },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không ghi nhận được tình trạng tài sản. Vui lòng thử lại.",
    };
  }

  revalidatePath(`/cn/nhan-phong/${maHopDong}`);
  return {};
}
