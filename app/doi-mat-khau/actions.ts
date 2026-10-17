"use server";

import bcrypt from "bcrypt";
import { redirect } from "next/navigation";
import { prisma } from "@/lib/prisma";
import { getSession, setSessionCookie } from "@/lib/auth";

export type DoiMatKhauState = { error?: string };

export async function doiMatKhau(
  _prevState: DoiMatKhauState,
  formData: FormData,
): Promise<DoiMatKhauState> {
  const session = await getSession();
  if (!session) redirect("/dang-nhap");

  const matKhauCu = String(formData.get("matKhauCu") ?? "");
  const matKhauMoi = String(formData.get("matKhauMoi") ?? "");
  const xacNhan = String(formData.get("xacNhan") ?? "");

  if (matKhauMoi.length < 6) {
    return { error: "Mật khẩu mới phải có ít nhất 6 ký tự." };
  }
  if (matKhauMoi !== xacNhan) {
    return { error: "Xác nhận mật khẩu không khớp." };
  }

  const taiKhoan = await prisma.taiKhoan.findUnique({
    where: { maTaiKhoan: session.maTaiKhoan },
  });
  if (!taiKhoan) redirect("/dang-nhap");

  const khop = await bcrypt.compare(matKhauCu, taiKhoan.matKhauHash);
  if (!khop) {
    return { error: "Mật khẩu hiện tại không đúng." };
  }

  const matKhauHash = await bcrypt.hash(matKhauMoi, 10);
  await prisma.taiKhoan.update({
    where: { maTaiKhoan: taiKhoan.maTaiKhoan },
    data: { matKhauHash, phaiDoiMatKhau: false },
  });

  await setSessionCookie({ ...session, phaiDoiMatKhau: false });
  redirect("/");
}
