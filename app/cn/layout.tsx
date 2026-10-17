import Link from "next/link";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend } from "@/lib/backend";
import { dangXuat } from "@/app/dang-xuat/actions";

const NAV = [
  { href: "/cn/viec-can-lam", label: "Việc cần làm" },
  { href: "/cn/so-do-toa", label: "Sơ đồ tòa" },
  { href: "/cn/phong", label: "Phòng & bảng giá" },
  { href: "/cn/bang-gia/dien-nuoc", label: "Đơn giá điện, nước" },
  { href: "/cn/bang-gia/phi-dich-vu", label: "Phí dịch vụ cố định" },
  { href: "/cn/khach-thue", label: "Khách thuê" },
  { href: "/cn/hop-dong", label: "Hợp đồng" },
  { href: "/cn/nhan-phong", label: "Nhận phòng" },
  { href: "/cn/tra-phong", label: "Trả phòng" },
  { href: "/cn/tam-tru", label: "Danh sách tạm trú" },
  { href: "/cn/chi-so", label: "Ghi chỉ số" },
  { href: "/cn/thong-bao-tien", label: "Thông báo tiền" },
  { href: "/cn/xac-nhan-thanh-toan", label: "Xác nhận thanh toán" },
  { href: "/cn/phieu-thu", label: "Phiếu thu" },
  { href: "/cn/su-co", label: "Sự cố" },
  { href: "/cn/thong-bao", label: "Thông báo" },
  { href: "/cn/thong-bao-chung", label: "Gửi thông báo chung" },
  { href: "/cn/bao-cao", label: "Báo cáo" },
  { href: "/cn/cai-dat/toa-nha", label: "Thông tin tòa" },
  { href: "/cn/cai-dat/tai-khoan-nhan-tien", label: "Tài khoản nhận tiền" },
  { href: "/cn/cai-dat/tai-khoan-khach", label: "Tài khoản khách" },
  { href: "/cn/cai-dat/khoi-tao", label: "Nhập dữ liệu đầu kỳ" },
  { href: "/cn/cai-dat/nhat-ky", label: "Nhật ký thao tác" },
];

export default async function ChuNhaLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();
  const [toaNha, demChuaDoc, dsViecCanLam] = await Promise.all([
    goiBackend<{ tenToa: string }>("/api/toa-nha/mac-dinh", { token }),
    goiBackend<{ soLuong: number }>("/api/thong-bao/chua-doc", { token }),
    goiBackend<unknown[]>("/api/viec-can-lam", { token }),
  ]);

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
              className="flex items-center justify-between rounded-md px-2 py-2 text-sm font-medium text-zinc-700 hover:bg-zinc-100"
            >
              {item.label}
              {item.href === "/cn/thong-bao" && demChuaDoc.soLuong > 0 && (
                <span className="rounded-full bg-red-500 px-1.5 py-0.5 text-xs font-semibold text-white">
                  {demChuaDoc.soLuong}
                </span>
              )}
              {item.href === "/cn/viec-can-lam" && dsViecCanLam.length > 0 && (
                <span className="rounded-full bg-red-500 px-1.5 py-0.5 text-xs font-semibold text-white">
                  {dsViecCanLam.length}
                </span>
              )}
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
