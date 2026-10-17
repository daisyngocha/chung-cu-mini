"use server";

import { redirect } from "next/navigation";
import { getSession, getSessionToken, setSessionCookie } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type DoiMatKhauState = { error?: string };

export async function doiMatKhau(
  _prevState: DoiMatKhauState,
  formData: FormData,
): Promise<DoiMatKhauState> {
  const session = await getSession();
  const token = await getSessionToken();
  if (!session || !token) redirect("/dang-nhap");

  const matKhauCu = String(formData.get("matKhauCu") ?? "");
  const matKhauMoi = String(formData.get("matKhauMoi") ?? "");
  const xacNhan = String(formData.get("xacNhan") ?? "");

  if (matKhauMoi.length < 6) {
    return { error: "Mật khẩu mới phải có ít nhất 6 ký tự." };
  }
  if (matKhauMoi !== xacNhan) {
    return { error: "Xác nhận mật khẩu không khớp." };
  }

  try {
    await goiBackend("/api/auth/doi-mat-khau", {
      method: "PATCH",
      token,
      body: { matKhauCu, matKhauMoi },
    });
  } catch (e) {
    return { error: e instanceof BackendError ? e.message : "Không kết nối được tới server." };
  }

  // Phát hành lại cookie với phaiDoiMatKhau=false (vẫn ký bằng SESSION_SECRET,
  // trùng JWT_SECRET phía backend nên proxy.ts verify được như cũ).
  await setSessionCookie({ ...session, phaiDoiMatKhau: false });
  redirect("/");
}
