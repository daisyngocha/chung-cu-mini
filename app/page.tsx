import { redirect } from "next/navigation";
import { getSession } from "@/lib/auth";

export default async function Home() {
  const session = await getSession();

  if (!session) redirect("/dang-nhap");
  if (session.phaiDoiMatKhau) redirect("/doi-mat-khau");
  if (session.vaiTro === "CHU_NHA") redirect("/cn/phong");
  redirect("/kt/trang-chu");
}
