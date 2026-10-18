"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type CapNhatTaiKhoanFormState = { error?: string; thanhCong?: boolean };

export async function capNhatTaiKhoanNhanTien(
  _prevState: CapNhatTaiKhoanFormState,
  formData: FormData,
): Promise<CapNhatTaiKhoanFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const nganHang = String(formData.get("nganHang") ?? "").trim();
  const soTaiKhoan = String(formData.get("soTaiKhoan") ?? "").trim();
  const chuTaiKhoan = String(formData.get("chuTaiKhoan") ?? "").trim();

  if (!nganHang || !soTaiKhoan || !chuTaiKhoan) {
    return { error: "Vui lòng nhập đầy đủ ngân hàng, số tài khoản và chủ tài khoản." };
  }

  try {
    await goiBackend("/api/toa-nha/mac-dinh/tai-khoan-nhan-tien", {
      method: "PATCH",
      token,
      body: { nganHang, soTaiKhoan, chuTaiKhoan },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không lưu được tài khoản nhận tiền. Vui lòng thử lại.",
    };
  }

  revalidatePath("/cn/cai-dat/tai-khoan-nhan-tien");
  return { thanhCong: true };
}
