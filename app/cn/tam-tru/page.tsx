export default function TamTruPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Danh sách tạm trú</h1>
      <p className="text-sm text-zinc-600">
        Xuất danh sách người ở mới (người đứng tên hợp đồng + người ở cùng) trong một khoảng ngày,
        dùng để khai báo tạm trú.
      </p>

      <form
        method="GET"
        action="/cn/tam-tru/file"
        className="flex flex-wrap items-end gap-3 rounded-lg border border-zinc-200 bg-white p-4"
      >
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Từ ngày</label>
          <input
            name="tuNgay"
            type="date"
            required
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs font-medium text-zinc-600">Đến ngày</label>
          <input
            name="denNgay"
            type="date"
            required
            className="rounded-md border border-zinc-300 px-2 py-1.5 text-sm"
          />
        </div>
        <button
          type="submit"
          className="rounded-md bg-zinc-900 px-3 py-1.5 text-sm font-medium text-white hover:bg-zinc-800"
        >
          Tải file Excel
        </button>
      </form>
    </div>
  );
}
