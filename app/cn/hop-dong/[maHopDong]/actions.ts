"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type HopDongDetailFormState = { error?: string };

function layLoi(e: unknown, macDinh: string) {
  return { error: e instanceof BackendError ? e.message : macDinh };
}

export async function xacNhanNhanPhong(
  maHopDong: string,
  _prevState: HopDongDetailFormState,
  formData: FormData,
): Promise<HopDongDetailFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const lyDo = String(formData.get("lyDo") ?? "").trim();
  const maNguoiOCapTK = String(formData.get("maNguoiOCapTK") ?? "").trim();

  try {
    await goiBackend(`/api/hop-dong/${maHopDong}/nhan-phong`, {
      method: "PATCH",
      token,
      body: { lyDo: lyDo || null, maNguoiOCapTK: maNguoiOCapTK || null },
    });
  } catch (e) {
    return layLoi(e, "Không xác nhận nhận phòng được. Vui lòng thử lại.");
  }
  revalidatePath(`/cn/hop-dong/${maHopDong}`);
  revalidatePath("/cn/hop-dong");
  revalidatePath("/cn/phong");
  return {};
}

export async function themNguoiO(
  maHopDong: string,
  _prevState: HopDongDetailFormState,
  formData: FormData,
): Promise<HopDongDetailFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const hoTen = String(formData.get("hoTen") ?? "").trim();
  const soCCCD = String(formData.get("soCCCD") ?? "").trim();
  const soDienThoai = String(formData.get("soDienThoai") ?? "").trim();
  const ngayVao = String(formData.get("ngayVao") ?? "").trim();

  if (!hoTen) {
    return { error: "Vui lòng nhập họ tên người ở." };
  }

  try {
    await goiBackend(`/api/hop-dong/${maHopDong}/nguoi-o`, {
      method: "POST",
      token,
      body: {
        hoTen,
        soCCCD: soCCCD || null,
        soDienThoai: soDienThoai || null,
        ngayVao: ngayVao || null,
      },
    });
  } catch (e) {
    return layLoi(e, "Không thêm được người ở. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/hop-dong/${maHopDong}`);
  return {};
}

export async function nguoiORoiDi(
  maHopDong: string,
  maNguoiO: string,
  formData: FormData,
) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const ngayRa = String(formData.get("ngayRa") ?? "").trim();

  await goiBackend(`/api/nguoi-o/${maNguoiO}/roi-di`, {
    method: "PATCH",
    token,
    body: { ngayRa },
  });
  revalidatePath(`/cn/hop-dong/${maHopDong}`);
}

export async function giaHan(
  maHopDong: string,
  _prevState: HopDongDetailFormState,
  formData: FormData,
): Promise<HopDongDetailFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const ngayHetHanMoi = String(formData.get("ngayHetHanMoi") ?? "").trim();
  const giaMoi = String(formData.get("giaMoi") ?? "").trim();
  const anhPhuLuc = String(formData.get("anhPhuLuc") ?? "").trim();

  if (!ngayHetHanMoi || !anhPhuLuc) {
    return { error: "Vui lòng nhập hạn mới và ảnh phụ lục." };
  }

  try {
    await goiBackend(`/api/hop-dong/${maHopDong}/gia-han`, {
      method: "PATCH",
      token,
      body: { ngayHetHanMoi, giaMoi: giaMoi ? Number(giaMoi) : null, anhPhuLuc },
    });
  } catch (e) {
    return layLoi(e, "Không gia hạn được. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/hop-dong/${maHopDong}`);
  revalidatePath("/cn/hop-dong");
  return {};
}

export async function dieuChinhGia(
  maHopDong: string,
  _prevState: HopDongDetailFormState,
  formData: FormData,
): Promise<HopDongDetailFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const giaMoi = String(formData.get("giaMoi") ?? "").trim();
  const ngayApDung = String(formData.get("ngayApDung") ?? "").trim();
  const anhPhuLuc = String(formData.get("anhPhuLuc") ?? "").trim();

  if (!giaMoi || !ngayApDung || !anhPhuLuc) {
    return { error: "Vui lòng nhập giá mới, ngày áp dụng (ngày 1 của tháng) và ảnh phụ lục." };
  }

  try {
    await goiBackend(`/api/hop-dong/${maHopDong}/dieu-chinh-gia`, {
      method: "PATCH",
      token,
      body: { giaMoi: Number(giaMoi), ngayApDung, anhPhuLuc },
    });
  } catch (e) {
    return layLoi(e, "Không điều chỉnh được giá. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/hop-dong/${maHopDong}`);
  revalidatePath("/cn/hop-dong");
  return {};
}

export async function baoTraPhong(
  maHopDong: string,
  _prevState: HopDongDetailFormState,
  formData: FormData,
): Promise<HopDongDetailFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const ngayTraDuKien = String(formData.get("ngayTraDuKien") ?? "").trim();
  const loaiKetThuc = String(formData.get("loaiKetThuc") ?? "").trim();

  if (!ngayTraDuKien || !loaiKetThuc) {
    return { error: "Vui lòng nhập ngày trả dự kiến và loại kết thúc." };
  }

  try {
    await goiBackend(`/api/hop-dong/${maHopDong}/bao-tra`, {
      method: "PATCH",
      token,
      body: { ngayTraDuKien, loaiKetThuc },
    });
  } catch (e) {
    return layLoi(e, "Không ghi nhận được báo trả phòng. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/hop-dong/${maHopDong}`);
  revalidatePath("/cn/hop-dong");
  return {};
}

export async function xacNhanThuHoiChiaKhoa(maHopDong: string) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  await goiBackend(`/api/hop-dong/${maHopDong}/thu-hoi-chia-khoa`, { method: "PATCH", token });
  revalidatePath(`/cn/hop-dong/${maHopDong}`);
}
