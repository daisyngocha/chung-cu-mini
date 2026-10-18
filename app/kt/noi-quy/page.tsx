import { getSessionToken } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";

type ToaNhaApi = { tenToa: string; noiQuy: string | null };

export default async function NoiQuyPage() {
  const token = await getSessionToken();
  const toaNha = await goiBackend<ToaNhaApi>("/api/toa-nha/mac-dinh", { token });

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Nội quy tòa {toaNha.tenToa}</h1>
      <div className="rounded-lg border border-zinc-200 bg-white p-4">
        {toaNha.noiQuy ? (
          <p className="whitespace-pre-wrap text-sm text-zinc-700">{toaNha.noiQuy}</p>
        ) : (
          <p className="text-sm text-zinc-500">Chủ nhà chưa cập nhật nội quy.</p>
        )}
      </div>
    </div>
  );
}
