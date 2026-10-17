"use server";

import { revalidatePath } from "next/cache";
import { redirect } from "next/navigation";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type KhachThueFormState = { error?: string };

export async function capNhatHoSoKhach(
  _prevState: KhachThueFormState,
  formData: FormData,
): Promise<KhachThueFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const soDienThoai = String(formData.get("soDienThoai") ?? "").trim();
  const hoTen = String(formData.get("hoTen") ?? "").trim();
  const ngaySinh = String(formData.get("ngaySinh") ?? "").trim();
  const soCCCD = String(formData.get("soCCCD") ?? "").trim();
  const anhCCCD = String(formData.get("anhCCCD") ?? "").trim();
  const queQuan = String(formData.get("queQuan") ?? "").trim();
  const noiLamViec = String(formData.get("noiLamViec") ?? "").trim();

  if (!soDienThoai || !hoTen || !ngaySinh || !soCCCD) {
    return { error: "Vui lòng nhập đầy đủ số điện thoại, họ tên, ngày sinh và số CCCD." };
  }

  try {
    await goiBackend("/api/khach-thue", {
      method: "POST",
      token,
      body: {
        soDienThoai,
        hoTen,
        ngaySinh,
        soCCCD,
        anhCCCD: anhCCCD || null,
        queQuan: queQuan || null,
        noiLamViec: noiLamViec || null,
      },
    });
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không lưu được hồ sơ khách. Vui lòng thử lại.",
    };
  }

  revalidatePath("/cn/khach-thue");
  redirect(`/cn/khach-thue?sdt=${encodeURIComponent(soDienThoai)}`);
}
