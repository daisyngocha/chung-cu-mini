"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type QuyetToanFormState = { error?: string };

function layLoi(e: unknown, macDinh: string) {
  return { error: e instanceof BackendError ? e.message : macDinh };
}

export async function themKhoanTru(
  maQuyetToan: string,
  _prevState: QuyetToanFormState,
  formData: FormData,
): Promise<QuyetToanFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const loaiKhoan = String(formData.get("loaiKhoan") ?? "").trim();
  const soTien = String(formData.get("soTien") ?? "").trim();
  const ghiChu = String(formData.get("ghiChu") ?? "").trim();
  const anhBangChung = String(formData.get("anhBangChung") ?? "").trim();
  const lyDoDieuChinh = String(formData.get("lyDoDieuChinh") ?? "").trim();

  if (!loaiKhoan || !soTien) {
    return { error: "Vui lòng chọn loại khoản và nhập số tiền." };
  }

  try {
    await goiBackend(`/api/quyet-toan/${maQuyetToan}/khoan-tru`, {
      method: "POST",
      token,
      body: {
        loaiKhoan,
        soTien: Number(soTien),
        ghiChu: ghiChu || null,
        anhBangChung: anhBangChung || null,
        lyDoDieuChinh: lyDoDieuChinh || null,
      },
    });
  } catch (e) {
    return layLoi(e, "Không thêm được khoản trừ. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/quyet-toan/${maQuyetToan}`);
  return {};
}

export async function suaKhoanTru(
  maQuyetToan: string,
  maDong: string,
  _prevState: QuyetToanFormState,
  formData: FormData,
): Promise<QuyetToanFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const soTien = String(formData.get("soTien") ?? "").trim();
  const ghiChu = String(formData.get("ghiChu") ?? "").trim();
  const anhBangChung = String(formData.get("anhBangChung") ?? "").trim();
  const lyDo = String(formData.get("lyDo") ?? "").trim();

  if (!soTien) {
    return { error: "Vui lòng nhập số tiền." };
  }

  try {
    await goiBackend(`/api/quyet-toan/${maQuyetToan}/khoan-tru/${maDong}`, {
      method: "PATCH",
      token,
      body: {
        soTien: Number(soTien),
        ghiChu: ghiChu || null,
        anhBangChung: anhBangChung || null,
        lyDo: lyDo || null,
      },
    });
  } catch (e) {
    return layLoi(e, "Không sửa được khoản trừ. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/quyet-toan/${maQuyetToan}`);
  return {};
}

export async function xoaKhoanTru(
  maQuyetToan: string,
  maDong: string,
  _prevState: QuyetToanFormState,
  formData: FormData,
): Promise<QuyetToanFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const lyDo = String(formData.get("lyDo") ?? "").trim();

  if (!lyDo) {
    return { error: "Vui lòng nhập lý do xóa." };
  }

  try {
    await goiBackend(`/api/quyet-toan/${maQuyetToan}/khoan-tru/${maDong}`, {
      method: "DELETE",
      token,
      body: { lyDo },
    });
  } catch (e) {
    return layLoi(e, "Không xóa được khoản trừ. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/quyet-toan/${maQuyetToan}`);
  return {};
}

export async function guiKhach(maQuyetToan: string) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  await goiBackend(`/api/quyet-toan/${maQuyetToan}/gui-khach`, { method: "PATCH", token });
  revalidatePath(`/cn/quyet-toan/${maQuyetToan}`);
}

export async function chotQuyetToan(
  maQuyetToan: string,
  _prevState: QuyetToanFormState,
  formData: FormData,
): Promise<QuyetToanFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const lyDo = String(formData.get("lyDo") ?? "").trim();

  try {
    await goiBackend(`/api/quyet-toan/${maQuyetToan}/chot`, {
      method: "PATCH",
      token,
      body: { lyDo: lyDo || null },
    });
  } catch (e) {
    return layLoi(e, "Không chốt được quyết toán. Vui lòng thử lại.");
  }

  revalidatePath(`/cn/quyet-toan/${maQuyetToan}`);
  return {};
}

export type LapPhieuChiFormState = { error?: string; soPhieuChi?: string };

export async function lapPhieuChi(
  maQuyetToan: string,
  _prevState: LapPhieuChiFormState,
  formData: FormData,
): Promise<LapPhieuChiFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const hinhThuc = String(formData.get("hinhThuc") ?? "").trim();

  try {
    const res = await goiBackend<{ soPhieuChi: string }>(`/api/quyet-toan/${maQuyetToan}/phieu-chi`, {
      method: "POST",
      token,
      body: { hinhThuc },
    });
    revalidatePath(`/cn/quyet-toan/${maQuyetToan}`);
    return { soPhieuChi: res.soPhieuChi };
  } catch (e) {
    return layLoi(e, "Không lập được phiếu chi. Vui lòng thử lại.");
  }
}

export type LapThongBaoThuThemFormState = { error?: string; maThongBaoTien?: string };

export async function lapThongBaoThuThem(
  maQuyetToan: string,
  _prevState: LapThongBaoThuThemFormState,
  _formData: FormData,
): Promise<LapThongBaoThuThemFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  try {
    const res = await goiBackend<{ maThongBaoTien: string }>(`/api/quyet-toan/${maQuyetToan}/thong-bao-thu-them`, {
      method: "POST",
      token,
    });
    revalidatePath(`/cn/quyet-toan/${maQuyetToan}`);
    return { maThongBaoTien: res.maThongBaoTien };
  } catch (e) {
    return layLoi(e, "Không lập được thông báo thu thêm. Vui lòng thử lại.");
  }
}
