import { requireSession } from "@/lib/auth";
import { DoiMatKhauForm } from "./doi-mat-khau-form";

export default async function DoiMatKhauPage() {
  const session = await requireSession();

  return (
    <div className="flex min-h-full items-center justify-center px-4 py-16">
      <div className="w-full max-w-sm rounded-lg border border-zinc-200 bg-white p-6 shadow-sm">
        <h1 className="mb-1 text-lg font-semibold text-zinc-900">
          Đổi mật khẩu
        </h1>
        <p className="mb-6 text-sm text-zinc-500">
          {session.phaiDoiMatKhau
            ? "Bạn cần đổi mật khẩu trước khi tiếp tục sử dụng."
            : "Cập nhật mật khẩu cho tài khoản của bạn."}
        </p>
        <DoiMatKhauForm />
      </div>
    </div>
  );
}
