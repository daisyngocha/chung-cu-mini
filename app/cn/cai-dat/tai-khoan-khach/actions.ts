"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type TaiKhoanKhachFormState = { error?: string; matKhauTam?: string };

export async function khoaTaiKhoan(maTaiKhoan: string) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  await goiBackend(`/api/tai-khoan-khach/${maTaiKhoan}/khoa`, { method: "PATCH", token });
  revalidatePath("/cn/cai-dat/tai-khoan-khach");
}

export async function moKhoaTaiKhoan(maTaiKhoan: string) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  await goiBackend(`/api/tai-khoan-khach/${maTaiKhoan}/mo-khoa`, { method: "PATCH", token });
  revalidatePath("/cn/cai-dat/tai-khoan-khach");
}

export async function datLaiMatKhau(
  maTaiKhoan: string,
  _prevState: TaiKhoanKhachFormState,
  _formData: FormData,
): Promise<TaiKhoanKhachFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  try {
    const res = await goiBackend<{ matKhauTam: string }>(`/api/tai-khoan-khach/${maTaiKhoan}/dat-lai-mat-khau`, {
      method: "PATCH",
      token,
    });
    revalidatePath("/cn/cai-dat/tai-khoan-khach");
    return { matKhauTam: res.matKhauTam };
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không đặt lại được mật khẩu. Vui lòng thử lại.",
    };
  }
}
