import 'dotenv/config';
import { PrismaClient } from '@prisma/client';
import * as bcrypt from 'bcrypt';

const prisma = new PrismaClient();

async function main() {
  const matKhauMau = process.env.SEED_ADMIN_PASSWORD;

  if (!matKhauMau) {
    throw new Error(
      'Thiếu SEED_ADMIN_PASSWORD. Hãy khai báo biến này trong file .env trước khi chạy seed.',
    );
  }

  console.log('Bắt đầu khởi tạo dữ liệu mẫu...');

  const toaNha = await prisma.toaNha.upsert({
    where: { maToa: 'TOA01' },
    update: {},
    create: {
      maToa: 'TOA01',
      tenToa: 'Chung cư Mini Mặc định',
      diaChi: '71 Ngô Sỹ Liên, Phường Văn Miếu - Quốc Tử Giám, Hà Nội',

      // Các trường bắt buộc trong bảng ToaNha.
      // Đây là dữ liệu giả để thử nghiệm; hãy thay bằng dữ liệu phù hợp nếu cần.
      nganHang: 'NH-test',
      soTaiKhoan: '0000000000',
      chuTaiKhoan: 'CHU TAI KHOAN MAU',
      tenChuNha: 'Daisy Ngoc Ha',
      sdtChuNha: '0900000000',
    },
  });

  console.log(`✅ Đã khởi tạo tòa nhà: ${toaNha.maToa}`);

  const matKhauHash = await bcrypt.hash(matKhauMau, 10);

  const taiKhoan = await prisma.taiKhoan.upsert({
    where: { tenDangNhap: 'admin-NHa' },
    update: {
      // Chạy lại seed sẽ cập nhật hash theo mật khẩu mẫu trong .env,
      // đồng thời mở khóa và buộc đổi lại mật khẩu ở lần đăng nhập tới.
      matKhauHash,
      phaiDoiMatKhau: true,
      trangThai: 'HOAT_DONG',
      soLanSai: 0,
      khoaDenLuc: null,
    },
    create: {
      maTaiKhoan: 'TK_ADMIN_01',
      tenDangNhap: 'admin-NHa',
      matKhauHash,
      vaiTro: 'CHU_NHA',
    },
  });

  console.log(`✅ Đã khởi tạo tài khoản: ${taiKhoan.tenDangNhap}`);
}

main()
  .catch((error) => {
    console.error('❌ Lỗi khi khởi tạo dữ liệu:', error);
    process.exitCode = 1;
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
