"use server";

import { redirect } from "next/navigation";
import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type GhiNhanTinhTrangFormState = { error?: string };

export async function ghiNhanTinhTrangTraPhong(
  maHopDong: string,
  maTaiSan: string,
  _prevState: GhiNhanTinhTrangFormState,
  formData: FormData,
): Promise<GhiNhanTinhTrangFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const tinhTrang = String(formData.get("tinhTrang") ?? "").trim();
  const anh = String(formData.get("anh") ?? "").trim();
  const chiPhiHuHong = String(formData.get("chiPhiHuHong") ?? "").trim();

  if (!tinhTrang) {
    return { error: "Vui lòng nhập tình trạng tài sản." };
  }

  try {
    await goiBackend(`/api/hop-dong/${maHopDong}/ban-giao`, {
      method: "POST",
      token,
      body: {
        maTaiSan,
        loaiBanGiao: "TRA_PHONG",
        tinhTrang,
        anh: anh || null,
        chiPhiHuHong: chiPhiHuHong ? Number(chiPhiHuHong) : null,
      },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không ghi nhận được tình trạng tài sản. Vui lòng thử lại.",
    };
  }

  revalidatePath(`/cn/tra-phong/${maHopDong}`);
  return {};
}

export type TinhQuyetToanFormState = { error?: string };

export async function tinhQuyetToan(
  maHopDong: string,
  _prevState: TinhQuyetToanFormState,
  _formData: FormData,
): Promise<TinhQuyetToanFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  let maQuyetToan: string;
  try {
    const qt = await goiBackend<{ maQuyetToan: string }>(`/api/hop-dong/${maHopDong}/quyet-toan`, {
      method: "POST",
      token,
    });
    maQuyetToan = qt.maQuyetToan;
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không tính được quyết toán. Vui lòng thử lại.",
    };
  }

  revalidatePath(`/cn/tra-phong/${maHopDong}`);
  redirect(`/cn/quyet-toan/${maQuyetToan}`);
}
