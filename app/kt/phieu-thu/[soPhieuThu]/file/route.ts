import { NextRequest, NextResponse } from "next/server";
import { getSessionToken, requireSession } from "@/lib/auth";

export async function GET(
  _request: NextRequest,
  { params }: { params: Promise<{ soPhieuThu: string }> },
) {
  await requireSession("KHACH_THUE");
  const token = await getSessionToken();
  const { soPhieuThu } = await params;

  const res = await fetch(`${process.env.BACKEND_URL}/api/khach/phieu-thu/${soPhieuThu}/pdf`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    cache: "no-store",
  });

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
      "Content-Type": "application/pdf",
      "Content-Disposition": `attachment; filename="${soPhieuThu}.pdf"`,
    },
  });
}
