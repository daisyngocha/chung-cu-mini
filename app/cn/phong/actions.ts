"use server";

import { revalidatePath } from "next/cache";
import { Prisma } from "@prisma/client";
import { prisma } from "@/lib/prisma";
import { requireSession } from "@/lib/auth";

export type PhongFormState = { error?: string };

export async function taoPhong(
  _prevState: PhongFormState,
  formData: FormData,
): Promise<PhongFormState> {
  await requireSession("CHU_NHA");

  const maPhong = String(formData.get("maPhong") ?? "").trim();
  const tang = Number(formData.get("tang"));
  const dienTich = String(formData.get("dienTich") ?? "").trim();
  const soNguoiToiDa = Number(formData.get("soNguoiToiDa"));
  const giaNiemYet = String(formData.get("giaNiemYet") ?? "").trim();
  const coCongToNuoc = formData.get("coCongToNuoc") === "on";

  if (!maPhong || !Number.isFinite(tang) || !Number.isFinite(soNguoiToiDa)) {
    return { error: "Vui lòng nhập đầy đủ mã phòng, tầng và số người tối đa." };
  }
  if (!dienTich || !giaNiemYet) {
    return { error: "Vui lòng nhập diện tích và giá niêm yết." };
  }

  try {
    await prisma.phong.create({
      data: {
        maPhong,
        tang,
        dienTich: new Prisma.Decimal(dienTich),
        soNguoiToiDa,
        giaNiemYet: new Prisma.Decimal(giaNiemYet),
        coCongToNuoc,
      },
    });
  } catch (e) {
    if (e instanceof Prisma.PrismaClientKnownRequestError && e.code === "P2002") {
      return { error: `Mã phòng "${maPhong}" đã tồn tại.` };
    }
    return { error: "Không thêm được phòng. Vui lòng thử lại." };
  }

  revalidatePath("/cn/phong");
  return {};
}

export async function chuyenTrangThaiPhong(maPhong: string) {
  await requireSession("CHU_NHA");

  const phong = await prisma.phong.findUnique({ where: { maPhong } });
  if (!phong) return;

  // BR-DM-04: chủ nhà chỉ chuyển Trống <-> Ngừng khai thác.
  if (phong.trangThai === "TRONG") {
    await prisma.phong.update({
      where: { maPhong },
      data: { trangThai: "NGUNG_KHAI_THAC" },
    });
  } else if (phong.trangThai === "NGUNG_KHAI_THAC") {
    await prisma.phong.update({
      where: { maPhong },
      data: { trangThai: "TRONG" },
    });
  }

  revalidatePath("/cn/phong");
}

export async function xoaPhong(maPhong: string) {
  await requireSession("CHU_NHA");

  // BR-DM-03: chỉ xóa phòng chưa từng có hợp đồng.
  const soHopDong = await prisma.hopDong.count({ where: { maPhong } });
  if (soHopDong > 0) return;

  await prisma.phong.delete({ where: { maPhong } });
  revalidatePath("/cn/phong");
}
