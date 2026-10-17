import { NextResponse, type NextRequest } from "next/server";
import { verifySessionToken } from "./lib/session";

export async function proxy(request: NextRequest) {
  const token = request.cookies.get("session")?.value;
  const session = token ? await verifySessionToken(token) : null;

  if (!session) {
    return NextResponse.redirect(new URL("/dang-nhap", request.url));
  }

  const { pathname } = request.nextUrl;
  if (pathname.startsWith("/cn") && session.vaiTro !== "CHU_NHA") {
    return NextResponse.redirect(new URL("/dang-nhap", request.url));
  }
  if (pathname.startsWith("/kt") && session.vaiTro !== "KHACH_THUE") {
    return NextResponse.redirect(new URL("/dang-nhap", request.url));
  }

  return NextResponse.next();
}

export const config = {
  matcher: ["/cn/:path*", "/kt/:path*", "/doi-mat-khau"],
};
