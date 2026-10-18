"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";

export async function danhDauDaDoc(maThongBao: string) {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();
  await goiBackend(`/api/thong-bao/${maThongBao}/da-doc`, { method: "PATCH", token });
  revalidatePath("/kt/thong-bao");
}
