import "server-only";

export class BackendError extends Error {}

/**
 * Gọi API Spring Boot từ phía server của Next.js (Server Component/Action).
 * Browser không bao giờ gọi trực tiếp backend Java — tránh phải lo CORS và
 * không lộ JWT ra JS phía client.
 */
export async function goiBackend<T>(
  path: string,
  options: { method?: string; token?: string | null; body?: unknown } = {},
): Promise<T> {
  const res = await fetch(`${process.env.BACKEND_URL}${path}`, {
    method: options.method ?? "GET",
    headers: {
      "Content-Type": "application/json",
      ...(options.token ? { Authorization: `Bearer ${options.token}` } : {}),
    },
    body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    cache: "no-store",
  });

  if (!res.ok) {
    const data = await res.json().catch(() => null);
    throw new BackendError(data?.message ?? `Backend trả lỗi ${res.status}`);
  }

  if (res.status === 204 || res.headers.get("content-length") === "0") {
    return undefined as T;
  }
  return res.json() as Promise<T>;
}
