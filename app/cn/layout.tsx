import Link from "next/link";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { dangXuat } from "@/app/dang-xuat/actions";

const NAV = [
  { href: "/cn/phong", label: "Phòng & bảng giá" },
  { href: "/cn/khach-thue", label: "Khách thuê" },
  { href: "/cn/hop-dong", label: "Hợp đồng" },
  { href: "/cn/tam-tru", label: "Danh sách tạm trú" },
];

export default async function ChuNhaLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const toaNha = await goiBackend<{ tenToa: string }>("/api/toa-nha/mac-dinh", { token });

  return (
    <div className="flex min-h-full">
      <aside className="flex w-56 flex-col border-r border-zinc-200 bg-white px-3 py-4">
        <div className="mb-6 px-2">
          <p className="text-sm font-semibold text-zinc-900">
            {toaNha.tenToa}
          </p>
          <p className="text-xs text-zinc-500">Chủ nhà</p>
        </div>

        <nav className="flex flex-1 flex-col gap-1">
          {NAV.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className="rounded-md px-2 py-2 text-sm font-medium text-zinc-700 hover:bg-zinc-100"
            >
              {item.label}
            </Link>
          ))}
        </nav>

        <div className="flex flex-col gap-1 border-t border-zinc-200 pt-3">
          <Link
            href="/doi-mat-khau"
            className="rounded-md px-2 py-2 text-sm text-zinc-700 hover:bg-zinc-100"
          >
            Đổi mật khẩu
          </Link>
          <form action={dangXuat}>
            <button
              type="submit"
              className="w-full rounded-md px-2 py-2 text-left text-sm text-zinc-700 hover:bg-zinc-100"
            >
              Đăng xuất
            </button>
          </form>
        </div>
      </aside>

      <main className="flex-1 px-6 py-6">{children}</main>
    </div>
  );
}
