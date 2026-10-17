"use server";

import bcrypt from "bcrypt";
import { redirect } from "next/navigation";
import { prisma } from "@/lib/prisma";
import { setSessionCookie } from "@/lib/auth";

const SO_LAN_SAI_TOI_DA = 5;
const THOI_GIAN_KHOA_TAM_PHUT = 15;

export type DangNhapState = { error?: string };

export async function dangNhap(
  _prevState: DangNhapState,
  formData: FormData,
): Promise<DangNhapState> {
  const tenDangNhap = String(formData.get("tenDangNhap") ?? "").trim();
  const matKhau = String(formData.get("matKhau") ?? "");

  if (!tenDangNhap || !matKhau) {
    return { error: "Vui lòng nhập tên đăng nhập và mật khẩu." };
  }

  const taiKhoan = await prisma.taiKhoan.findUnique({ where: { tenDangNhap } });
  if (!taiKhoan) {
    return { error: "Sai tên đăng nhập hoặc mật khẩu." };
  }

  if (taiKhoan.trangThai === "DA_KHOA") {
    return { error: "Tài khoản đã bị khóa. Vui lòng liên hệ chủ nhà." };
  }

  if (taiKhoan.trangThai === "KHOA_TAM") {
    if (taiKhoan.khoaDenLuc && taiKhoan.khoaDenLuc > new Date()) {
      const phutConLai = Math.ceil(
        (taiKhoan.khoaDenLuc.getTime() - Date.now()) / 60_000,
      );
      return {
        error: `Tài khoản tạm khóa do nhập sai mật khẩu nhiều lần. Thử lại sau ${phutConLai} phút.`,
      };
    }
    // Hết thời gian khóa tạm: mở khóa trước khi kiểm tra mật khẩu.
    await prisma.taiKhoan.update({
      where: { maTaiKhoan: taiKhoan.maTaiKhoan },
      data: { trangThai: "HOAT_DONG", soLanSai: 0, khoaDenLuc: null },
    });
    taiKhoan.soLanSai = 0;
  }

  const khop = await bcrypt.compare(matKhau, taiKhoan.matKhauHash);

  if (!khop) {
    const soLanSaiMoi = taiKhoan.soLanSai + 1;
    const bLockNow = soLanSaiMoi >= SO_LAN_SAI_TOI_DA;
    await prisma.taiKhoan.update({
      where: { maTaiKhoan: taiKhoan.maTaiKhoan },
      data: bLockNow
        ? {
            soLanSai: soLanSaiMoi,
            trangThai: "KHOA_TAM",
            khoaDenLuc: new Date(Date.now() + THOI_GIAN_KHOA_TAM_PHUT * 60_000),
          }
        : { soLanSai: soLanSaiMoi },
    });
    return bLockNow
      ? {
          error: `Sai mật khẩu quá ${SO_LAN_SAI_TOI_DA} lần. Tài khoản tạm khóa ${THOI_GIAN_KHOA_TAM_PHUT} phút.`,
        }
      : { error: "Sai tên đăng nhập hoặc mật khẩu." };
  }

  if (taiKhoan.soLanSai > 0) {
    await prisma.taiKhoan.update({
      where: { maTaiKhoan: taiKhoan.maTaiKhoan },
      data: { soLanSai: 0 },
    });
  }

  await setSessionCookie({
    maTaiKhoan: taiKhoan.maTaiKhoan,
    tenDangNhap: taiKhoan.tenDangNhap,
    vaiTro: taiKhoan.vaiTro,
    phaiDoiMatKhau: taiKhoan.phaiDoiMatKhau,
  });

  if (taiKhoan.phaiDoiMatKhau) redirect("/doi-mat-khau");
  redirect(taiKhoan.vaiTro === "CHU_NHA" ? "/cn/phong" : "/kt/trang-chu");
}
