import { SignJWT, jwtVerify } from "jose";
import type { VaiTro } from "@prisma/client";

const SESSION_COOKIE = "session";
const SESSION_DURATION_SECONDS = 8 * 60 * 60; // 8 giờ

export type SessionPayload = {
  maTaiKhoan: string;
  tenDangNhap: string;
  vaiTro: VaiTro;
  phaiDoiMatKhau: boolean;
};

function getSecretKey() {
  const secret = process.env.SESSION_SECRET;
  if (!secret) {
    throw new Error("Thiếu biến môi trường SESSION_SECRET.");
  }
  return new TextEncoder().encode(secret);
}

export async function createSessionToken(payload: SessionPayload) {
  return new SignJWT({ ...payload })
    .setProtectedHeader({ alg: "HS256" })
    .setIssuedAt()
    .setExpirationTime(`${SESSION_DURATION_SECONDS}s`)
    .sign(getSecretKey());
}

export async function verifySessionToken(
  token: string,
): Promise<SessionPayload | null> {
  try {
    const { payload } = await jwtVerify(token, getSecretKey());
    return payload as unknown as SessionPayload;
  } catch {
    return null;
  }
}

export { SESSION_COOKIE, SESSION_DURATION_SECONDS };
