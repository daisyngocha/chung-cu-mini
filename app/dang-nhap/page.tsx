import { redirect } from "next/navigation";
import { getSession } from "@/lib/auth";
import { LoginForm } from "./login-form";

export default async function DangNhapPage() {
  const session = await getSession();
  if (session) redirect("/");

  return (
    <div className="flex min-h-full items-center justify-center px-4 py-16">
      <div className="w-full max-w-sm rounded-lg border border-zinc-200 bg-white p-6 shadow-sm">
        <h1 className="mb-1 text-lg font-semibold text-zinc-900">
          Chung cư mini
        </h1>
        <p className="mb-6 text-sm text-zinc-500">Đăng nhập để tiếp tục</p>
        <LoginForm />
      </div>
    </div>
  );
}
