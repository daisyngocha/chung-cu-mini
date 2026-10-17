-- CreateEnum
CREATE TYPE "VaiTro" AS ENUM ('CHU_NHA', 'KHACH_THUE');

-- CreateEnum
CREATE TYPE "TrangThaiTK" AS ENUM ('HOAT_DONG', 'KHOA_TAM', 'DA_KHOA');

-- CreateEnum
CREATE TYPE "TrangThaiPhong" AS ENUM ('TRONG', 'DANG_THUE', 'NGUNG_KHAI_THAC');

-- CreateEnum
CREATE TYPE "LoaiKhoan" AS ENUM ('DIEN', 'NUOC', 'NUOC_THEO_NGUOI', 'DICH_VU_THEO_PHONG', 'DICH_VU_THEO_XE');

-- CreateEnum
CREATE TYPE "TrangThaiHD" AS ENUM ('CHO_NHAN_PHONG', 'HIEU_LUC', 'DA_THANH_LY');

-- CreateEnum
CREATE TYPE "LoaiKetThuc" AS ENUM ('KHACH_BAO_TRA', 'HET_HAN', 'CHAM_DUT_VI_PHAM');

-- CreateEnum
CREATE TYPE "LoaiBanGiao" AS ENUM ('NHAN_PHONG', 'TRA_PHONG');

-- CreateEnum
CREATE TYPE "LoaiThayDoi" AS ENUM ('GIA_HAN', 'DIEU_CHINH_GIA', 'DIEU_CHINH_PHI', 'THEM_NGUOI_O', 'BOT_NGUOI_O');

-- CreateEnum
CREATE TYPE "LoaiCongTo" AS ENUM ('DIEN', 'NUOC');

-- CreateEnum
CREATE TYPE "LoaiGhi" AS ENUM ('DAU_KY', 'HANG_THANG', 'CUOI_KY');

-- CreateEnum
CREATE TYPE "LoaiThongBao" AS ENUM ('KY_HOP_DONG', 'HANG_THANG', 'THU_THEM_QUYET_TOAN');

-- CreateEnum
CREATE TYPE "TrangThaiTBT" AS ENUM ('NHAP', 'DA_PHAT_HANH', 'CHO_XAC_NHAN', 'DA_THU_MOT_PHAN', 'DA_THU_DU', 'QUA_HAN', 'DA_HUY');

-- CreateEnum
CREATE TYPE "LoaiKhoanTBT" AS ENUM ('TIEN_PHONG', 'TIEN_COC', 'DIEN', 'NUOC', 'DICH_VU', 'PHI_PHAT_SINH', 'GIAM_TRU', 'DIEU_CHINH');

-- CreateEnum
CREATE TYPE "TrangThaiBaoTT" AS ENUM ('CHO_XAC_NHAN', 'DA_XAC_NHAN', 'TU_CHOI');

-- CreateEnum
CREATE TYPE "HinhThucTT" AS ENUM ('CHUYEN_KHOAN', 'TIEN_MAT');

-- CreateEnum
CREATE TYPE "TrangThaiPT" AS ENUM ('HIEU_LUC', 'DA_HUY');

-- CreateEnum
CREATE TYPE "TrangThaiQT" AS ENUM ('NHAP', 'CHO_PHAN_HOI', 'KHACH_KHONG_DONG_Y', 'DA_CHOT', 'HOAN_TAT');

-- CreateEnum
CREATE TYPE "LoaiKhoanQT" AS ENUM ('TIEN_COC', 'HOAN_TIEN_PHONG', 'DIEN', 'NUOC', 'DICH_VU', 'NO_CHUA_TRA', 'BOI_THUONG_HU_HONG', 'PHAT_TRA_TRUOC_HAN', 'KHAC');

-- CreateEnum
CREATE TYPE "LoaiSuCo" AS ENUM ('DIEN', 'NUOC', 'THIET_BI', 'INTERNET', 'KHAC');

-- CreateEnum
CREATE TYPE "TrangThaiSuCo" AS ENUM ('MOI', 'DANG_XU_LY', 'DA_XU_LY', 'TU_CHOI', 'MO_LAI');

-- CreateEnum
CREATE TYPE "LoaiSuKien" AS ENUM ('TB_TIEN_MOI', 'NHAC_HAN_TT', 'QUA_HAN_TT', 'KHACH_BAO_CK', 'XAC_NHAN_THU', 'TU_CHOI_BAO_CK', 'HD_SAP_HET_HAN', 'SU_CO_MOI', 'SU_CO_DOI_TRANG_THAI', 'SU_CO_MO_LAI', 'KHACH_BAO_TRA', 'QUYET_TOAN_DA_GUI', 'KHACH_PHAN_HOI_QT', 'THONG_BAO_CHUNG');

-- CreateEnum
CREATE TYPE "NguonTB" AS ENUM ('TU_DONG', 'CHU_NHA');

-- CreateEnum
CREATE TYPE "PhamViTB" AS ENUM ('CA_NHAN', 'TOAN_TOA');

-- CreateTable
CREATE TABLE "ToaNha" (
    "maToa" VARCHAR(10) NOT NULL DEFAULT 'TOA01',
    "tenToa" VARCHAR(100) NOT NULL,
    "diaChi" VARCHAR(255) NOT NULL,
    "noiQuy" TEXT,
    "ngayChotChiSo" INTEGER NOT NULL DEFAULT 31,
    "hanThanhToan" INTEGER NOT NULL DEFAULT 5,
    "nganHang" VARCHAR(10) NOT NULL,
    "soTaiKhoan" VARCHAR(20) NOT NULL,
    "chuTaiKhoan" VARCHAR(100) NOT NULL,
    "tenChuNha" VARCHAR(100) NOT NULL,
    "sdtChuNha" VARCHAR(10) NOT NULL,
    "diaChiChuNha" VARCHAR(255),

    CONSTRAINT "ToaNha_pkey" PRIMARY KEY ("maToa")
);

-- CreateTable
CREATE TABLE "TaiKhoan" (
    "maTaiKhoan" VARCHAR(20) NOT NULL,
    "tenDangNhap" VARCHAR(10) NOT NULL,
    "matKhauHash" VARCHAR(255) NOT NULL,
    "vaiTro" "VaiTro" NOT NULL,
    "trangThai" "TrangThaiTK" NOT NULL DEFAULT 'HOAT_DONG',
    "soLanSai" INTEGER NOT NULL DEFAULT 0,
    "phaiDoiMatKhau" BOOLEAN NOT NULL DEFAULT true,
    "ngayTao" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "khoaDenLuc" TIMESTAMP(3),

    CONSTRAINT "TaiKhoan_pkey" PRIMARY KEY ("maTaiKhoan")
);

-- CreateTable
CREATE TABLE "NhatKyThaoTac" (
    "maNhatKy" VARCHAR(20) NOT NULL,
    "maTaiKhoan" VARCHAR(20),
    "thoiDiem" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "doiTuong" VARCHAR(30) NOT NULL,
    "maDoiTuong" VARCHAR(20) NOT NULL,
    "hanhDong" VARCHAR(30) NOT NULL,
    "noiDung" TEXT,

    CONSTRAINT "NhatKyThaoTac_pkey" PRIMARY KEY ("maNhatKy")
);

-- CreateTable
CREATE TABLE "ThongBao" (
    "maThongBao" VARCHAR(20) NOT NULL,
    "loaiSuKien" "LoaiSuKien" NOT NULL,
    "tieuDe" VARCHAR(150) NOT NULL,
    "noiDung" TEXT NOT NULL,
    "nguon" "NguonTB" NOT NULL DEFAULT 'TU_DONG',
    "phamVi" "PhamViTB" NOT NULL DEFAULT 'CA_NHAN',
    "doiTuongLienQuan" VARCHAR(50),
    "ngayTao" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "ThongBao_pkey" PRIMARY KEY ("maThongBao")
);

-- CreateTable
CREATE TABLE "ThongBaoNguoiNhan" (
    "maThongBao" VARCHAR(20) NOT NULL,
    "maTaiKhoan" VARCHAR(20) NOT NULL,
    "daDoc" BOOLEAN NOT NULL DEFAULT false,
    "thoiDiemDoc" TIMESTAMP(3),

    CONSTRAINT "ThongBaoNguoiNhan_pkey" PRIMARY KEY ("maThongBao","maTaiKhoan")
);

-- CreateTable
CREATE TABLE "Phong" (
    "maPhong" VARCHAR(10) NOT NULL,
    "maToa" VARCHAR(10) NOT NULL DEFAULT 'TOA01',
    "tang" INTEGER NOT NULL,
    "dienTich" DECIMAL(6,2) NOT NULL,
    "soNguoiToiDa" INTEGER NOT NULL,
    "giaNiemYet" DECIMAL(15,0) NOT NULL,
    "coCongToNuoc" BOOLEAN NOT NULL DEFAULT true,
    "trangThai" "TrangThaiPhong" NOT NULL DEFAULT 'TRONG',

    CONSTRAINT "Phong_pkey" PRIMARY KEY ("maPhong")
);

-- CreateTable
CREATE TABLE "TaiSan" (
    "maTaiSan" VARCHAR(20) NOT NULL,
    "maPhong" VARCHAR(10) NOT NULL,
    "tenTaiSan" VARCHAR(100) NOT NULL,
    "soLuong" INTEGER NOT NULL DEFAULT 1,
    "tinhTrang" VARCHAR(255) NOT NULL DEFAULT 'Tốt',
    "ghiChu" VARCHAR(500),

    CONSTRAINT "TaiSan_pkey" PRIMARY KEY ("maTaiSan")
);

-- CreateTable
CREATE TABLE "BangGia" (
    "maKhoan" VARCHAR(20) NOT NULL,
    "maToa" VARCHAR(10) NOT NULL DEFAULT 'TOA01',
    "tenKhoan" VARCHAR(100) NOT NULL,
    "loaiKhoan" "LoaiKhoan" NOT NULL,
    "donGia" DECIMAL(15,0) NOT NULL,
    "donViTinh" VARCHAR(20) NOT NULL,
    "ngayApDung" DATE NOT NULL,
    "ngayHetHieuLuc" DATE,

    CONSTRAINT "BangGia_pkey" PRIMARY KEY ("maKhoan")
);

-- CreateTable
CREATE TABLE "KhachThue" (
    "maKhach" VARCHAR(20) NOT NULL,
    "soDienThoai" VARCHAR(10) NOT NULL,
    "hoTen" VARCHAR(100) NOT NULL,
    "ngaySinh" DATE NOT NULL,
    "soCCCD" VARCHAR(12) NOT NULL,
    "anhCCCD" VARCHAR(500),
    "queQuan" VARCHAR(255),
    "noiLamViec" VARCHAR(255),

    CONSTRAINT "KhachThue_pkey" PRIMARY KEY ("maKhach")
);

-- CreateTable
CREATE TABLE "HopDong" (
    "maHopDong" VARCHAR(20) NOT NULL,
    "maPhong" VARCHAR(10) NOT NULL,
    "maKhach" VARCHAR(20) NOT NULL,
    "maTaiKhoan" VARCHAR(20),
    "maNguoiOCapTK" VARCHAR(20),
    "ngayBatDau" DATE NOT NULL,
    "ngayHetHan" DATE NOT NULL,
    "giaThue" DECIMAL(15,0) NOT NULL,
    "tienCoc" DECIMAL(15,0) NOT NULL,
    "mucPhatTraTruoc" DECIMAL(15,0) NOT NULL DEFAULT 0,
    "anhHopDong" VARCHAR(1000),
    "ngayNhanPhong" DATE,
    "ngayBaoTra" DATE,
    "ngayTraDuKien" DATE,
    "ngayTraThucTe" DATE,
    "trangThai" "TrangThaiHD" NOT NULL DEFAULT 'CHO_NHAN_PHONG',
    "lyDoNhanPhongChuaThuDu" VARCHAR(500),
    "loaiKetThuc" "LoaiKetThuc",
    "daThuHoiChiaKhoa" BOOLEAN NOT NULL DEFAULT false,

    CONSTRAINT "HopDong_pkey" PRIMARY KEY ("maHopDong")
);

-- CreateTable
CREATE TABLE "NguoiO" (
    "maNguoiO" VARCHAR(20) NOT NULL,
    "maHopDong" VARCHAR(20) NOT NULL,
    "hoTen" VARCHAR(100) NOT NULL,
    "soCCCD" VARCHAR(12),
    "soDienThoai" VARCHAR(10),
    "ngayVao" DATE NOT NULL,
    "ngayRa" DATE,

    CONSTRAINT "NguoiO_pkey" PRIMARY KEY ("maNguoiO")
);

-- CreateTable
CREATE TABLE "HopDongDichVu" (
    "maHopDong" VARCHAR(20) NOT NULL,
    "maKhoan" VARCHAR(20) NOT NULL,
    "soLuong" INTEGER NOT NULL DEFAULT 1,
    "mucPhi" DECIMAL(15,0) NOT NULL,
    "ngayApDung" DATE NOT NULL,

    CONSTRAINT "HopDongDichVu_pkey" PRIMARY KEY ("maHopDong","maKhoan")
);

-- CreateTable
CREATE TABLE "BanGiaoTaiSan" (
    "maBanGiao" VARCHAR(20) NOT NULL,
    "maHopDong" VARCHAR(20) NOT NULL,
    "maTaiSan" VARCHAR(20) NOT NULL,
    "loaiBanGiao" "LoaiBanGiao" NOT NULL,
    "ngayBanGiao" DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "tinhTrang" VARCHAR(255) NOT NULL,
    "anh" VARCHAR(1000),
    "chiPhiHuHong" DECIMAL(15,0) NOT NULL DEFAULT 0,

    CONSTRAINT "BanGiaoTaiSan_pkey" PRIMARY KEY ("maBanGiao")
);

-- CreateTable
CREATE TABLE "LichSuHopDong" (
    "maLichSu" VARCHAR(20) NOT NULL,
    "maHopDong" VARCHAR(20) NOT NULL,
    "ngayThayDoi" DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "loaiThayDoi" "LoaiThayDoi" NOT NULL,
    "truongThayDoi" VARCHAR(50) NOT NULL,
    "giaTriCu" VARCHAR(500),
    "giaTriMoi" VARCHAR(500),
    "ngayApDung" DATE NOT NULL,
    "anhPhuLuc" VARCHAR(1000),

    CONSTRAINT "LichSuHopDong_pkey" PRIMARY KEY ("maLichSu")
);

-- CreateTable
CREATE TABLE "ChiSo" (
    "maChiSo" VARCHAR(20) NOT NULL,
    "maHopDong" VARCHAR(20) NOT NULL,
    "loaiCongTo" "LoaiCongTo" NOT NULL,
    "kyThang" CHAR(7) NOT NULL,
    "loaiGhi" "LoaiGhi" NOT NULL DEFAULT 'HANG_THANG',
    "ngayGhi" DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "chiSoCu" INTEGER NOT NULL,
    "chiSoMoi" INTEGER NOT NULL,
    "anhCongTo" VARCHAR(500),

    CONSTRAINT "ChiSo_pkey" PRIMARY KEY ("maChiSo")
);

-- CreateTable
CREATE TABLE "ThongBaoTien" (
    "maThongBaoTien" VARCHAR(20) NOT NULL,
    "maHopDong" VARCHAR(20) NOT NULL,
    "maQuyetToan" VARCHAR(20),
    "loaiThongBao" "LoaiThongBao" NOT NULL DEFAULT 'HANG_THANG',
    "kyThang" CHAR(7) NOT NULL,
    "ngayPhatHanh" DATE,
    "hanThanhToan" DATE NOT NULL,
    "noCu" DECIMAL(15,0) NOT NULL DEFAULT 0,
    "tongTien" DECIMAL(15,0) NOT NULL,
    "soDaThu" DECIMAL(15,0) NOT NULL DEFAULT 0,
    "trangThai" "TrangThaiTBT" NOT NULL DEFAULT 'NHAP',
    "lyDoHuy" VARCHAR(500),

    CONSTRAINT "ThongBaoTien_pkey" PRIMARY KEY ("maThongBaoTien")
);

-- CreateTable
CREATE TABLE "DongThongBaoTien" (
    "maDong" VARCHAR(20) NOT NULL,
    "maThongBaoTien" VARCHAR(20) NOT NULL,
    "maChiSo" VARCHAR(20),
    "maSuCo" VARCHAR(20),
    "maThongBaoTienGoc" VARCHAR(20),
    "loaiKhoan" "LoaiKhoanTBT" NOT NULL,
    "dienGiai" VARCHAR(255) NOT NULL,
    "tuNgay" DATE,
    "denNgay" DATE,
    "soLuong" DECIMAL(10,2) NOT NULL DEFAULT 1,
    "donGia" DECIMAL(15,0) NOT NULL,
    "tyLeNgayO" DECIMAL(7,6) NOT NULL DEFAULT 1,
    "thanhTien" DECIMAL(15,2) NOT NULL,

    CONSTRAINT "DongThongBaoTien_pkey" PRIMARY KEY ("maDong")
);

-- CreateTable
CREATE TABLE "BaoThanhToan" (
    "maBaoTT" VARCHAR(20) NOT NULL,
    "maThongBaoTien" VARCHAR(20) NOT NULL,
    "thoiDiemBao" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "anhBienLai" VARCHAR(500),
    "trangThai" "TrangThaiBaoTT" NOT NULL DEFAULT 'CHO_XAC_NHAN',
    "lyDoTuChoi" VARCHAR(500),

    CONSTRAINT "BaoThanhToan_pkey" PRIMARY KEY ("maBaoTT")
);

-- CreateTable
CREATE TABLE "PhieuThu" (
    "soPhieuThu" VARCHAR(20) NOT NULL,
    "maThongBaoTien" VARCHAR(20) NOT NULL,
    "maBaoTT" VARCHAR(20),
    "ngayThu" DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "nguoiNop" VARCHAR(100) NOT NULL,
    "soTien" DECIMAL(15,0) NOT NULL,
    "hinhThuc" "HinhThucTT" NOT NULL,
    "trangThai" "TrangThaiPT" NOT NULL DEFAULT 'HIEU_LUC',
    "lyDoHuy" VARCHAR(500),

    CONSTRAINT "PhieuThu_pkey" PRIMARY KEY ("soPhieuThu")
);

-- CreateTable
CREATE TABLE "QuyetToan" (
    "maQuyetToan" VARCHAR(20) NOT NULL,
    "maHopDong" VARCHAR(20) NOT NULL,
    "ngayTra" DATE NOT NULL,
    "tienCoc" DECIMAL(15,0) NOT NULL,
    "tienPhongHoanLai" DECIMAL(15,0) NOT NULL DEFAULT 0,
    "tongKhauTru" DECIMAL(15,0) NOT NULL DEFAULT 0,
    "soTienQuyetToan" DECIMAL(15,0) NOT NULL,
    "trangThai" "TrangThaiQT" NOT NULL DEFAULT 'NHAP',
    "lyDoDieuChinh" VARCHAR(500),
    "khachDongY" BOOLEAN,
    "yKienKhach" VARCHAR(500),
    "thoiDiemPhanHoi" TIMESTAMP(3),

    CONSTRAINT "QuyetToan_pkey" PRIMARY KEY ("maQuyetToan")
);

-- CreateTable
CREATE TABLE "DongQuyetToan" (
    "maDong" VARCHAR(20) NOT NULL,
    "maQuyetToan" VARCHAR(20) NOT NULL,
    "loaiKhoan" "LoaiKhoanQT" NOT NULL,
    "soTien" DECIMAL(15,2) NOT NULL,
    "ghiChu" VARCHAR(500),
    "anhBangChung" VARCHAR(1000),

    CONSTRAINT "DongQuyetToan_pkey" PRIMARY KEY ("maDong")
);

-- CreateTable
CREATE TABLE "PhieuChi" (
    "soPhieuChi" VARCHAR(20) NOT NULL,
    "maQuyetToan" VARCHAR(20) NOT NULL,
    "ngayChi" DATE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "nguoiNhan" VARCHAR(100) NOT NULL,
    "soTien" DECIMAL(15,0) NOT NULL,
    "hinhThuc" "HinhThucTT" NOT NULL,

    CONSTRAINT "PhieuChi_pkey" PRIMARY KEY ("soPhieuChi")
);

-- CreateTable
CREATE TABLE "SuCo" (
    "maSuCo" VARCHAR(20) NOT NULL,
    "maPhong" VARCHAR(10) NOT NULL,
    "loaiSuCo" "LoaiSuCo" NOT NULL,
    "moTa" VARCHAR(1000) NOT NULL,
    "anh" VARCHAR(1000),
    "ngayBao" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "lichHen" TIMESTAMP(3),
    "ketQua" VARCHAR(1000),
    "chiPhi" DECIMAL(15,0) NOT NULL DEFAULT 0,
    "doKhachGay" BOOLEAN NOT NULL DEFAULT false,
    "trangThai" "TrangThaiSuCo" NOT NULL DEFAULT 'MOI',
    "lyDoTuChoi" VARCHAR(500),

    CONSTRAINT "SuCo_pkey" PRIMARY KEY ("maSuCo")
);

-- CreateTable
CREATE TABLE "LichSuSuCo" (
    "maLichSu" VARCHAR(20) NOT NULL,
    "maSuCo" VARCHAR(20) NOT NULL,
    "thoiDiem" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "trangThaiCu" "TrangThaiSuCo",
    "trangThaiMoi" "TrangThaiSuCo" NOT NULL,
    "vaiTroThucHien" "VaiTro" NOT NULL,
    "ghiChu" VARCHAR(1000),

    CONSTRAINT "LichSuSuCo_pkey" PRIMARY KEY ("maLichSu")
);

-- CreateIndex
CREATE UNIQUE INDEX "TaiKhoan_tenDangNhap_key" ON "TaiKhoan"("tenDangNhap");

-- CreateIndex
CREATE UNIQUE INDEX "KhachThue_soDienThoai_key" ON "KhachThue"("soDienThoai");

-- CreateIndex
CREATE UNIQUE INDEX "ChiSo_maHopDong_loaiCongTo_kyThang_loaiGhi_key" ON "ChiSo"("maHopDong", "loaiCongTo", "kyThang", "loaiGhi");

-- CreateIndex
CREATE UNIQUE INDEX "ThongBaoTien_maQuyetToan_key" ON "ThongBaoTien"("maQuyetToan");

-- CreateIndex
CREATE UNIQUE INDEX "PhieuThu_maBaoTT_key" ON "PhieuThu"("maBaoTT");

-- CreateIndex
CREATE UNIQUE INDEX "QuyetToan_maHopDong_key" ON "QuyetToan"("maHopDong");

-- CreateIndex
CREATE UNIQUE INDEX "PhieuChi_maQuyetToan_key" ON "PhieuChi"("maQuyetToan");

-- AddForeignKey
ALTER TABLE "NhatKyThaoTac" ADD CONSTRAINT "NhatKyThaoTac_maTaiKhoan_fkey" FOREIGN KEY ("maTaiKhoan") REFERENCES "TaiKhoan"("maTaiKhoan") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ThongBaoNguoiNhan" ADD CONSTRAINT "ThongBaoNguoiNhan_maThongBao_fkey" FOREIGN KEY ("maThongBao") REFERENCES "ThongBao"("maThongBao") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ThongBaoNguoiNhan" ADD CONSTRAINT "ThongBaoNguoiNhan_maTaiKhoan_fkey" FOREIGN KEY ("maTaiKhoan") REFERENCES "TaiKhoan"("maTaiKhoan") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Phong" ADD CONSTRAINT "Phong_maToa_fkey" FOREIGN KEY ("maToa") REFERENCES "ToaNha"("maToa") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "TaiSan" ADD CONSTRAINT "TaiSan_maPhong_fkey" FOREIGN KEY ("maPhong") REFERENCES "Phong"("maPhong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BangGia" ADD CONSTRAINT "BangGia_maToa_fkey" FOREIGN KEY ("maToa") REFERENCES "ToaNha"("maToa") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "HopDong" ADD CONSTRAINT "HopDong_maPhong_fkey" FOREIGN KEY ("maPhong") REFERENCES "Phong"("maPhong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "HopDong" ADD CONSTRAINT "HopDong_maKhach_fkey" FOREIGN KEY ("maKhach") REFERENCES "KhachThue"("maKhach") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "HopDong" ADD CONSTRAINT "HopDong_maTaiKhoan_fkey" FOREIGN KEY ("maTaiKhoan") REFERENCES "TaiKhoan"("maTaiKhoan") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "HopDong" ADD CONSTRAINT "HopDong_maNguoiOCapTK_fkey" FOREIGN KEY ("maNguoiOCapTK") REFERENCES "NguoiO"("maNguoiO") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "NguoiO" ADD CONSTRAINT "NguoiO_maHopDong_fkey" FOREIGN KEY ("maHopDong") REFERENCES "HopDong"("maHopDong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "HopDongDichVu" ADD CONSTRAINT "HopDongDichVu_maHopDong_fkey" FOREIGN KEY ("maHopDong") REFERENCES "HopDong"("maHopDong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "HopDongDichVu" ADD CONSTRAINT "HopDongDichVu_maKhoan_fkey" FOREIGN KEY ("maKhoan") REFERENCES "BangGia"("maKhoan") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BanGiaoTaiSan" ADD CONSTRAINT "BanGiaoTaiSan_maHopDong_fkey" FOREIGN KEY ("maHopDong") REFERENCES "HopDong"("maHopDong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BanGiaoTaiSan" ADD CONSTRAINT "BanGiaoTaiSan_maTaiSan_fkey" FOREIGN KEY ("maTaiSan") REFERENCES "TaiSan"("maTaiSan") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LichSuHopDong" ADD CONSTRAINT "LichSuHopDong_maHopDong_fkey" FOREIGN KEY ("maHopDong") REFERENCES "HopDong"("maHopDong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ChiSo" ADD CONSTRAINT "ChiSo_maHopDong_fkey" FOREIGN KEY ("maHopDong") REFERENCES "HopDong"("maHopDong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ThongBaoTien" ADD CONSTRAINT "ThongBaoTien_maHopDong_fkey" FOREIGN KEY ("maHopDong") REFERENCES "HopDong"("maHopDong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "ThongBaoTien" ADD CONSTRAINT "ThongBaoTien_maQuyetToan_fkey" FOREIGN KEY ("maQuyetToan") REFERENCES "QuyetToan"("maQuyetToan") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DongThongBaoTien" ADD CONSTRAINT "DongThongBaoTien_maThongBaoTien_fkey" FOREIGN KEY ("maThongBaoTien") REFERENCES "ThongBaoTien"("maThongBaoTien") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DongThongBaoTien" ADD CONSTRAINT "DongThongBaoTien_maChiSo_fkey" FOREIGN KEY ("maChiSo") REFERENCES "ChiSo"("maChiSo") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DongThongBaoTien" ADD CONSTRAINT "DongThongBaoTien_maSuCo_fkey" FOREIGN KEY ("maSuCo") REFERENCES "SuCo"("maSuCo") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DongThongBaoTien" ADD CONSTRAINT "DongThongBaoTien_maThongBaoTienGoc_fkey" FOREIGN KEY ("maThongBaoTienGoc") REFERENCES "ThongBaoTien"("maThongBaoTien") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "BaoThanhToan" ADD CONSTRAINT "BaoThanhToan_maThongBaoTien_fkey" FOREIGN KEY ("maThongBaoTien") REFERENCES "ThongBaoTien"("maThongBaoTien") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PhieuThu" ADD CONSTRAINT "PhieuThu_maThongBaoTien_fkey" FOREIGN KEY ("maThongBaoTien") REFERENCES "ThongBaoTien"("maThongBaoTien") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PhieuThu" ADD CONSTRAINT "PhieuThu_maBaoTT_fkey" FOREIGN KEY ("maBaoTT") REFERENCES "BaoThanhToan"("maBaoTT") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "QuyetToan" ADD CONSTRAINT "QuyetToan_maHopDong_fkey" FOREIGN KEY ("maHopDong") REFERENCES "HopDong"("maHopDong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "DongQuyetToan" ADD CONSTRAINT "DongQuyetToan_maQuyetToan_fkey" FOREIGN KEY ("maQuyetToan") REFERENCES "QuyetToan"("maQuyetToan") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "PhieuChi" ADD CONSTRAINT "PhieuChi_maQuyetToan_fkey" FOREIGN KEY ("maQuyetToan") REFERENCES "QuyetToan"("maQuyetToan") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "SuCo" ADD CONSTRAINT "SuCo_maPhong_fkey" FOREIGN KEY ("maPhong") REFERENCES "Phong"("maPhong") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "LichSuSuCo" ADD CONSTRAINT "LichSuSuCo_maSuCo_fkey" FOREIGN KEY ("maSuCo") REFERENCES "SuCo"("maSuCo") ON DELETE RESTRICT ON UPDATE CASCADE;
