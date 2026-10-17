"use server";

import { redirect } from "next/navigation";
import { clearSessionCookie } from "@/lib/auth";

export async function dangXuat() {
  await clearSessionCookie();
  redirect("/dang-nhap");
}
