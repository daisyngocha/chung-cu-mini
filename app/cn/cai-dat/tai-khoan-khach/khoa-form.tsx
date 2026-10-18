"use client";

import { khoaTaiKhoan, moKhoaTaiKhoan } from "./actions";

export function KhoaMoKhoaButton({ maTaiKhoan, dangKhoa }: { maTaiKhoan: string; dangKhoa: boolean }) {
  return (
    <form action={(dangKhoa ? moKhoaTaiKhoan : khoaTaiKhoan).bind(null, maTaiKhoan)}>
      <button
        type="submit"
        className={
          dangKhoa
            ? "text-xs font-medium text-green-700 hover:underline"
            : "text-xs font-medium text-red-700 hover:underline"
        }
      >
        {dangKhoa ? "Mở khóa" : "Khóa"}
      </button>
    </form>
  );
}
