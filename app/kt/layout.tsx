import Link from "next/link";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { dangXuat } from "@/app/dang-xuat/actions";

const NAV = [
  { href: "/kt/trang-chu", label: "Trang chủ" },
  { href: "/kt/noi-quy", label: "Nội quy tòa" },
  { href: "/kt/thong-bao-tien", label: "Thông báo tiền" },
  { href: "/kt/phieu-thu", label: "Phiếu thu" },
  { href: "/kt/hop-dong", label: "Hồ sơ & hợp đồng" },
  { href: "/kt/tra-phong", label: "Trả phòng & quyết toán" },
  { href: "/kt/su-co", label: "Sự cố" },
  { href: "/kt/thong-bao", label: "Thông báo" },
];

export default async function KhachThueLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();
  const [toaNha, demChuaDoc] = await Promise.all([
    goiBackend<{ tenToa: string }>("/api/toa-nha/mac-dinh", { token }),
    goiBackend<{ soLuong: number }>("/api/thong-bao/chua-doc", { token }),
  ]);

  return (
    <div className="flex min-h-full">
      <aside className="flex w-56 flex-col border-r border-zinc-200 bg-white px-3 py-4">
        <div className="mb-6 px-2">
          <p className="text-sm font-semibold text-zinc-900">{toaNha.tenToa}</p>
          <p className="text-xs text-zinc-500">Khách thuê</p>
        </div>

        <nav className="flex flex-1 flex-col gap-1">
          {NAV.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className="flex items-center justify-between rounded-md px-2 py-2 text-sm font-medium text-zinc-700 hover:bg-zinc-100"
            >
              {item.label}
              {item.href === "/kt/thong-bao" && demChuaDoc.soLuong > 0 && (
                <span className="rounded-full bg-red-500 px-1.5 py-0.5 text-xs font-semibold text-white">
                  {demChuaDoc.soLuong}
                </span>
              )}
            </Link>
          ))}
        </nav>

        <div className="flex flex-col gap-1 border-t border-zinc-200 pt-3">
          <Link href="/doi-mat-khau" className="rounded-md px-2 py-2 text-sm text-zinc-700 hover:bg-zinc-100">
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
