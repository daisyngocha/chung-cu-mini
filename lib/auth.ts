import "server-only";
import { cookies } from "next/headers";
import { redirect } from "next/navigation";
import {
  SESSION_COOKIE,
  SESSION_DURATION_SECONDS,
  createSessionToken,
  verifySessionToken,
  type SessionPayload,
} from "./session";

export async function getSession(): Promise<SessionPayload | null> {
  const token = (await cookies()).get(SESSION_COOKIE)?.value;
  if (!token) return null;
  return verifySessionToken(token);
}

export async function setSessionCookie(payload: SessionPayload) {
  const token = await createSessionToken(payload);
  (await cookies()).set(SESSION_COOKIE, token, {
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    path: "/",
    maxAge: SESSION_DURATION_SECONDS,
  });
}

export async function clearSessionCookie() {
  (await cookies()).delete(SESSION_COOKIE);
}

/** Dùng trong Server Component / Server Action: chặn truy cập nếu chưa đăng nhập hoặc sai vai trò. */
export async function requireSession(vaiTroChoPhep?: SessionPayload["vaiTro"]) {
  const session = await getSession();
  if (!session) redirect("/dang-nhap");
  if (vaiTroChoPhep && session.vaiTro !== vaiTroChoPhep) redirect("/dang-nhap");
  return session;
}
