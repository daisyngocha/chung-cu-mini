import { NextRequest, NextResponse } from "next/server";
import { getSessionToken, requireSession } from "@/lib/auth";

export async function GET(request: NextRequest) {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const { searchParams } = new URL(request.url);
  const tuNgay = searchParams.get("tuNgay");
  const denNgay = searchParams.get("denNgay");
  if (!tuNgay || !denNgay) {
    return NextResponse.json({ message: "Thiếu tuNgay hoặc denNgay." }, { status: 400 });
  }

  const res = await fetch(
    `${process.env.BACKEND_URL}/api/hop-dong/tam-tru?tuNgay=${tuNgay}&denNgay=${denNgay}`,
    {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
      cache: "no-store",
    },
  );

  if (!res.ok) {
    const data = await res.json().catch(() => null);
    return NextResponse.json(
      { message: data?.message ?? `Backend trả lỗi ${res.status}` },
      { status: res.status },
    );
  }

  const file = await res.arrayBuffer();
  return new NextResponse(file, {
    headers: {
      "Content-Type":
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "Content-Disposition": 'attachment; filename="danh-sach-tam-tru.xlsx"',
    },
  });
}
