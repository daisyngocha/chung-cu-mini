"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type LapHangLoatState = {
  error?: string;
  daLap?: string[];
  phongThieuChiSo?: string[];
};

export async function lapHangLoat(
  kyThang: string,
  _prevState: LapHangLoatState,
  _formData: FormData,
): Promise<LapHangLoatState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  try {
    const ketQua = await goiBackend<{ daLap: string[]; phongThieuChiSo: string[] }>(
      `/api/thong-bao-tien/lap-hang-loat?kyThang=${encodeURIComponent(kyThang)}`,
      { method: "POST", token },
    );
    revalidatePath("/cn/thong-bao-tien");
    return { daLap: ketQua.daLap, phongThieuChiSo: ketQua.phongThieuChiSo };
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không lập được thông báo hàng loạt. Vui lòng thử lại.",
    };
  }
}

export type PhatHanhHangLoatState = {
  error?: string;
  thanhCong?: string[];
  canhBaoHetHan?: string[];
  loi?: string[];
};

export async function phatHanhHangLoat(
  _prevState: PhatHanhHangLoatState,
  formData: FormData,
): Promise<PhatHanhHangLoatState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const dsMaThongBaoTien = formData.getAll("chon").map(String);
  if (dsMaThongBaoTien.length === 0) {
    return { error: "Vui lòng chọn ít nhất một thông báo ở trạng thái Nháp." };
  }

  try {
    const ketQua = await goiBackend<{ thanhCong: string[]; canhBaoHetHan: string[]; loi: string[] }>(
      "/api/thong-bao-tien/phat-hanh-hang-loat",
      { method: "POST", token, body: dsMaThongBaoTien },
    );
    revalidatePath("/cn/thong-bao-tien");
    return ketQua;
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không phát hành được. Vui lòng thử lại.",
    };
  }
}
