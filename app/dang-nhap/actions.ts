"use server";

import { redirect } from "next/navigation";
import { setSessionToken } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type DangNhapState = { error?: string };

type DangNhapResponse = {
  token: string;
  vaiTro: "CHU_NHA" | "KHACH_THUE";
  phaiDoiMatKhau: boolean;
};

export async function dangNhap(
  _prevState: DangNhapState,
  formData: FormData,
): Promise<DangNhapState> {
  const tenDangNhap = String(formData.get("tenDangNhap") ?? "").trim();
  const matKhau = String(formData.get("matKhau") ?? "");

  if (!tenDangNhap || !matKhau) {
    return { error: "Vui lòng nhập tên đăng nhập và mật khẩu." };
  }

  let res: DangNhapResponse;
  try {
    res = await goiBackend<DangNhapResponse>("/api/auth/dang-nhap", {
      method: "POST",
      body: { tenDangNhap, matKhau },
    });
  } catch (e) {
    return { error: e instanceof BackendError ? e.message : "Không kết nối được tới server." };
  }

  await setSessionToken(res.token);

  if (res.phaiDoiMatKhau) redirect("/doi-mat-khau");
  redirect(res.vaiTro === "CHU_NHA" ? "/cn/viec-can-lam" : "/kt/trang-chu");
}
