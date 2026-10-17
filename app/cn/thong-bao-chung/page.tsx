import { GuiThongBaoChungForm } from "./gui-thong-bao-chung-form";

export default function ThongBaoChungPage() {
  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-xl font-semibold text-zinc-900">Gửi thông báo chung</h1>
      <p className="text-sm text-zinc-600">
        Thông báo sẽ hiện trong hộp thông báo của toàn bộ khách thuê đang ở trong tòa.
      </p>
      <GuiThongBaoChungForm />
    </div>
  );
}
