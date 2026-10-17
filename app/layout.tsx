import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Chung cư mini",
  description: "Hệ thống quản lý chung cư mini",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="vi" className="h-full">
      <body className="min-h-full bg-zinc-50 text-zinc-900 antialiased">
        {children}
      </body>
    </html>
  );
}
