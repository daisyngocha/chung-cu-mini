"use server";

import { revalidatePath } from "next/cache";
import { getSessionToken, requireSession } from "@/lib/auth";
import { goiBackend, BackendError } from "@/lib/backend";

export type KhoiTaoFormState = {
  error?: string;
  ketQua?: { maHopDong: string; maPhong: string; tenDangNhap: string; matKhauTam: string };
};

export async function nhapHopDongDauKy(
  _prevState: KhoiTaoFormState,
  formData: FormData,
): Promise<KhoiTaoFormState> {
  await requireSession("CHU_NHA");
  const token = await getSessionToken();

  const maPhong = String(formData.get("maPhong") ?? "").trim();
  const soDienThoai = String(formData.get("soDienThoai") ?? "").trim();
  const hoTen = String(formData.get("hoTen") ?? "").trim();
  const ngaySinh = String(formData.get("ngaySinh") ?? "").trim();
  const soCCCD = String(formData.get("soCCCD") ?? "").trim();
  const queQuan = String(formData.get("queQuan") ?? "").trim();
  const noiLamViec = String(formData.get("noiLamViec") ?? "").trim();
  const ngayBatDau = String(formData.get("ngayBatDau") ?? "").trim();
  const ngayHetHan = String(formData.get("ngayHetHan") ?? "").trim();
  const giaThue = String(formData.get("giaThue") ?? "").trim();
  const tienCoc = String(formData.get("tienCoc") ?? "").trim();
  const chiSoDien = String(formData.get("chiSoDien") ?? "").trim();
  const chiSoNuoc = String(formData.get("chiSoNuoc") ?? "").trim();
  const noTon = String(formData.get("noTon") ?? "").trim();
  const dsDichVu = formData.getAll("dsDichVu").map((maKhoan) => ({ maKhoan: String(maKhoan), soLuong: 1 }));
  const nguoiOJson = String(formData.get("nguoiOJson") ?? "[]");
  const viTriNguoiOCapTaiKhoan = String(formData.get("viTriNguoiOCapTaiKhoan") ?? "").trim();

  if (!maPhong || !soDienThoai || !hoTen || !ngayBatDau || !ngayHetHan || !giaThue || !tienCoc || !chiSoDien) {
    return { error: "Vui lòng nhập đầy đủ thông tin bắt buộc (phòng, khách, thời hạn, giá thuê, tiền cọc, chỉ số điện)." };
  }

  let dsNguoiO: { hoTen: string; soCCCD: string | null; soDienThoai: string | null; ngayVao: string | null }[];
  try {
    dsNguoiO = JSON.parse(nguoiOJson);
  } catch {
    return { error: "Danh sách người ở không hợp lệ." };
  }

  try {
    const ketQua = await goiBackend<{ maHopDong: string; maPhong: string; tenDangNhap: string; matKhauTam: string }[]>(
      "/api/khoi-tao",
      {
        method: "POST",
        token,
        body: {
          dsHopDong: [
            {
              khach: {
                soDienThoai,
                hoTen,
                ngaySinh: ngaySinh || null,
                soCCCD: soCCCD || null,
                anhCCCD: null,
                queQuan: queQuan || null,
                noiLamViec: noiLamViec || null,
              },
              maPhong,
              ngayBatDau,
              ngayHetHan,
              giaThue: Number(giaThue),
              tienCoc: Number(tienCoc),
              dsDichVu,
              dsNguoiO,
              chiSoDien: { chiSoGanNhat: Number(chiSoDien), anhCongTo: null },
              chiSoNuoc: chiSoNuoc ? { chiSoGanNhat: Number(chiSoNuoc), anhCongTo: null } : null,
              noTon: noTon ? Number(noTon) : null,
              viTriNguoiOCapTaiKhoan: viTriNguoiOCapTaiKhoan ? Number(viTriNguoiOCapTaiKhoan) : null,
            },
          ],
        },
      },
    );
    revalidatePath("/cn/cai-dat/khoi-tao");
    revalidatePath("/cn/phong");
    return { ketQua: ketQua[0] };
  } catch (e) {
    return {
      error: e instanceof BackendError ? e.message : "Không nhập được dữ liệu. Vui lòng thử lại.",
    };
  }
}
