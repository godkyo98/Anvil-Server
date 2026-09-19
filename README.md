# 🛠️ Kyo Anvil - Giải Pháp Tối Ưu Hóa Chiếc Đe Server-Side Toàn Diện

![Minecraft Version](https://img.shields.io/badge/Minecraft-26.3-2ea44f?style=for-the-badge&logo=minecraft)
![Fabric Loader](https://img.shields.io/badge/Fabric%20Loader-0.19.5-dbd087?style=for-the-badge)
![Mod Version](https://img.shields.io/badge/Version-1.0.2-blue?style=for-the-badge)
![Platform](https://img.shields.io/badge/Fabric-Server--Side-E3C95A?style=for-the-badge)

Một module Server-Side cao cấp thuộc chuỗi dự án **Kyo Series**, được thiết kế riêng biệt cho máy chủ **TEA SERVER** vận hành trên nền tảng **Fabric**. 

Mod hoạt động hoàn toàn ở phía Server, mang lại những cải tiến mang tính cách mạng cho chiếc Đe Vanilla mà không yêu cầu người chơi phải cài đặt bất kỳ bản mod nào ở phía Client.

---

## ✨ Tính Năng Nổi Bật

### 1. 🪽 Đột Phá: Giáp Cánh Lai (Hybrid Elytra) & Nâng Cấp Netherite
* **Hợp nhất Giáp & Cánh trên Đe:** Cho phép người chơi kết hợp trực tiếp áo giáp (Chestplate) với Cánh cứng (Elytra) trên Đe để tạo thành **Giáp cánh lai**.
* **Bảo lưu toàn diện thuộc tính:** Giữ nguyên 100% các dòng phù phép (Enchantments) và chỉ số thuộc tính (Armor, Armor Toughness,...) từ trang bị gốc khi ghép nối.
* **Định danh & Chi phí chuẩn:** Tự động thiết lập tên, dòng mô tả (Lore) chuẩn hóa và tính toán chi phí sửa chữa hợp lý cho Giáp cánh lai.
* **Hỗ trợ Bàn Rèn (Smithing Table):** Tích hợp sâu vào Bàn rèn, cho phép mang Giáp cánh lai đi nâng cấp lên phiên bản **Netherite** bằng Phôi nâng cấp tương tự áo giáp thông thường.

### 2. 🛑 Xóa Bỏ Vĩnh Viễn Án Phạt "Too Expensive" (Quá Đắt)
* **Cơ chế:** Can thiệp sâu thông qua Mixin vào giai đoạn xuất xưởng kết quả (`createResult`), liên tục đặt lại thuộc tính `DataComponents.REPAIR_COST` về mốc `0`.
* **Lợi ích:** Vật phẩm có thể được gia cố, sửa chữa hoặc đập sách phù phép vô hạn lần mà không bao giờ bị tăng cấp độ yêu cầu ở những lần sau.
* **Đánh lừa Client:** Tự động phát hiện nếu cấp độ yêu cầu đạt mốc khóa click của Client ($\ge 40$ cấp), hệ thống sẽ ép hiển thị về mốc an toàn cấu hình trong Config (mặc định là `39`), giữ cho ô kết quả luôn mở để người chơi nhấp lấy đồ bình thường.

### 3. 🎨 Hệ Thống Đổi Tên Màu Sắc Thông Minh (`&` To Structured Components)
* **Dịch mã màu động:** Cho phép người dùng sử dụng ký tự quen thuộc `&` để tạo màu sắc rực rỡ, in đậm, gạch ngang, chữ nghiêng hoặc hiệu ứng ma thuật nhấp nháy chuẩn mã màu Minecraft.
* **Lột sạch rác hiển thị:** Khắc phục triệt để lỗi lộ ký tự mã thô (`§c`) trên thanh nhập chữ (Text Box) khi bỏ vật phẩm vào Đe lần thứ hai. Thanh text box luôn hiển thị chuỗi văn bản trần sạch sẽ.
* **Bảo lưu màu sắc tuyệt đối:** Khi đập thêm sách hoặc sửa độ bền cho một vũ khí đã có màu sắc từ trước, hệ thống tự động đối chiếu văn bản thanh Text Box. Nếu người dùng không sửa đổi chữ, toàn bộ cấu trúc định dạng màu sắc gốc sẽ được giữ nguyên 100%, không bị biến thành màu trắng Vanilla.
* **Kế thừa định dạng:** Nếu người chơi chỉnh sửa một phần chữ (Ví dụ: Thêm tiền tố/hậu tố), phần chữ mới sẽ tự động kế thừa màu sắc và style của vũ khí gốc mà không bắt họ phải gõ lại mã màu ban đầu.

### 4. 🪙 Tính Năng "Anvil Restoration" - Sửa Đe Tiết Kiệm
* **Kinh tế hóa:** Thay vì ép buộc người chơi lãng phí tài nguyên chế tạo một chiếc đe hoàn toàn mới bằng 3 Khối Sắt, tính năng này cho phép họ dùng **Phôi Sắt (Iron Ingot)** để phục hồi đe.
* **Cách dùng:** Cầm Phôi Sắt trên tay và click chuột phải trực tiếp vào chiếc đe bị hỏng nặng hoặc nứt.
* **Giai đoạn phục hồi:** `Đe Bị Hỏng Nặng (Damaged)` ➔ `Đe Bị Nứt (Chipped)` ➔ `Đe Hoàn Hảo (Normal)`. Mỗi lần sửa tốn đúng 1 Phôi Sắt kèm hiệu ứng âm thanh rèn đe Vanilla chân thực.

### 5. 🎲 Giảm Tỷ Lệ Nứt/Vỡ Đe Qua Cấu Hình Động
* Can thiệp trực tiếp vào hàm tính toán sát thương khối của `AnvilBlock` bằng Mixin.
* Cho phép hạ thấp tỷ lệ vỡ đe từ mốc `12%` mặc định của Vanilla xuống mốc an toàn hơn (mặc định là `2%`), giúp gia tăng vòng đời sử dụng đe trên các cụm Survival đông người chơi.

---

## ⚙️ Cấu Hình Hệ Thống (`config/kyo_anvil.json`)

Tệp cấu hình được tự động khởi tạo ngay trong lần chạy server đầu tiên dưới dạng tệp dữ liệu JSON trực quan, hỗ trợ thay đổi trực tiếp và nạp cấu hình thông minh:

```json
{
  "maxRepairCost": 39,
  "anvilDamageChance": 0.02,
  "enableColorRename": true,
  "enableIronIngotRepair": true,
  "colorCommandAlias": "mausac"
}
```
