"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type BangGiaFormState = { error?: string };

function duongDanTheoLoai(loaiKhoan: string) {
  return loaiKhoan === "DICH_VU_THEO_PHONG" || loaiKhoan === "DICH_VU_THEO_XE"
    ? "/cn/bang-gia/phi-dich-vu"
    : "/cn/bang-gia/dien-nuoc";
}

export async function taoKhoan(
  _prevState: BangGiaFormState,
  formData: FormData,
): Promise<BangGiaFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const tenKhoan = String(formData.get("tenKhoan") ?? "").trim();
  const loaiKhoan = String(formData.get("loaiKhoan") ?? "").trim();
  const donGia = String(formData.get("donGia") ?? "").trim();
  const donViTinh = String(formData.get("donViTinh") ?? "").trim();
  const ngayApDung = String(formData.get("ngayApDung") ?? "").trim();

  if (!tenKhoan || !loaiKhoan || !donGia || !donViTinh || !ngayApDung) {
    return { error: "Vui lòng nhập đầy đủ tên khoản, loại khoản, đơn giá, đơn vị tính và ngày áp dụng." };
  }

  try {
    await goiBackend("/api/bang-gia", {
      method: "POST",
      token,
      body: { tenKhoan, loaiKhoan, donGia: Number(donGia), donViTinh, ngayApDung },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không thêm được khoản giá. Vui lòng thử lại.",
    };
  }

  revalidatePath(duongDanTheoLoai(loaiKhoan));
  return {};
}

export async function doiDonGia(
  maKhoan: string,
  loaiKhoan: string,
  _prevState: BangGiaFormState,
  formData: FormData,
): Promise<BangGiaFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const giaMoi = String(formData.get("giaMoi") ?? "").trim();
  const ngayApDung = String(formData.get("ngayApDung") ?? "").trim();

  if (!giaMoi || !ngayApDung) {
    return { error: "Vui lòng nhập đơn giá mới và ngày áp dụng." };
  }

  try {
    await goiBackend(`/api/bang-gia/${maKhoan}/doi-don-gia`, {
      method: "PATCH",
      token,
      body: { giaMoi: Number(giaMoi), ngayApDung },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không đổi được đơn giá. Vui lòng thử lại.",
    };
  }

  revalidatePath(duongDanTheoLoai(loaiKhoan));
  return {};
}
