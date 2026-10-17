"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type ThongBaoTienDetailFormState = { error?: string };

function layLoi(e: unknown, macDinh: string) {
  return { error: e instanceof BackendError ? e.message : macDinh };
}

export async function themDong(
  maThongBaoTien: string,
  _prevState: ThongBaoTienDetailFormState,
  formData: FormData,
): Promise<ThongBaoTienDetailFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const loaiKhoan = String(formData.get("loaiKhoan") ?? "").trim();
  const dienGiai = String(formData.get("dienGiai") ?? "").trim();
  const tuNgay = String(formData.get("tuNgay") ?? "").trim();
  const denNgay = String(formData.get("denNgay") ?? "").trim();
  const soLuong = String(formData.get("soLuong") ?? "").trim();
  const donGia = String(formData.get("donGia") ?? "").trim();

  if (!loaiKhoan || !dienGiai || !soLuong || !donGia) {
    return { error: "Vui lòng nhập loại khoản, diễn giải, số lượng và đơn giá." };
  }

  try {
    await goiBackend(`/api/thong-bao-tien/${maThongBaoTien}/dong`, {
      method: "POST",
      token,
      body: {
        loaiKhoan,
        dienGiai,
        tuNgay: tuNgay || null,
        denNgay: denNgay || null,
        soLuong: Number(soLuong),
        donGia: Number(donGia),
        tyLeNgayO: null,
        maChiSo: null,
        maThongBaoTienGoc: null,
      },
    });
  } catch (e) {
    return layLoi(e, "Không thêm được dòng. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/thong-bao-tien/${maThongBaoTien}`);
  return {};
}

export async function phatHanh(maThongBaoTien: string) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  await goiBackend(`/api/thong-bao-tien/${maThongBaoTien}/phat-hanh`, { method: "PATCH", token });
  revalidatePath(`/cn/thong-bao-tien/${maThongBaoTien}`);
  revalidatePath("/cn/thong-bao-tien");
}

export async function huy(
  maThongBaoTien: string,
  _prevState: ThongBaoTienDetailFormState,
  formData: FormData,
): Promise<ThongBaoTienDetailFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const lyDo = String(formData.get("lyDo") ?? "").trim();

  try {
    await goiBackend(`/api/thong-bao-tien/${maThongBaoTien}/huy`, {
      method: "PATCH",
      token,
      body: { lyDo: lyDo || null },
    });
  } catch (e) {
    return layLoi(e, "Không hủy được thông báo. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/thong-bao-tien/${maThongBaoTien}`);
  revalidatePath("/cn/thong-bao-tien");
  return {};
}

export async function ghiNhanThu(
  maThongBaoTien: string,
  _prevState: ThongBaoTienDetailFormState,
  formData: FormData,
): Promise<ThongBaoTienDetailFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const soTien = String(formData.get("soTien") ?? "").trim();
  const hinhThuc = String(formData.get("hinhThuc") ?? "").trim();
  const nguoiNop = String(formData.get("nguoiNop") ?? "").trim();
  const ngayThu = String(formData.get("ngayThu") ?? "").trim();

  if (!soTien || !hinhThuc) {
    return { error: "Vui lòng nhập số tiền và hình thức thanh toán." };
  }

  try {
    await goiBackend(`/api/thong-bao-tien/${maThongBaoTien}/ghi-nhan-thu`, {
      method: "PATCH",
      token,
      body: {
        soTien: Number(soTien),
        hinhThuc,
        nguoiNop: nguoiNop || null,
        ngayThu: ngayThu || null,
      },
    });
  } catch (e) {
    return layLoi(e, "Không ghi nhận được khoản thu. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/thong-bao-tien/${maThongBaoTien}`);
  revalidatePath("/cn/thong-bao-tien");
  return {};
}
