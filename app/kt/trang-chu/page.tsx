import { requireSession } from "@/lib/auth";

export default async function TrangChuKhachThuePage() {
  await requireSession("KHACH_THUE");

  return (
    <div className="px-6 py-6">
      <h1 className="text-xl font-semibold text-zinc-900">Phòng của tôi</h1>
      <p className="mt-2 text-sm text-zinc-500">
        Màn hình khách thuê đang được xây dựng.
      </p>
    </div>
  );
}
