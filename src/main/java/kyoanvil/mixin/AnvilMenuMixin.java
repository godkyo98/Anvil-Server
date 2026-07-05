package kyoanvil.mixin;

import kyoanvil.config.KyoAnvilConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {

    @Shadow public DataSlot cost;
    @Shadow private String itemName;

    @Inject(method = "createResult", at = @At("RETURN"))
    private void kyo$advancedAnvilColorFix(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        ItemStack resultStack = menu.getSlot(2).getItem();
        ItemStack inputStack = menu.getSlot(0).getItem();

        if (!resultStack.isEmpty()) {

            // XỬ LÝ ĐỔI TÊN MÀU, BẢO LƯU VÀ KẾ THỪA MÀU SẮC KHI THÊM CHỮ
            if (this.itemName != null && !this.itemName.isEmpty()) {
                // Hướng 1: Người chơi chủ động gõ thêm mã màu mới (& hoặc §)
                if (this.itemName.contains("&") || this.itemName.contains("§")) {
                    resultStack.set(DataComponents.CUSTOM_NAME, kyo$parseColorString(this.itemName));
                }
                // Hướng 2: Người chơi chỉ gõ thêm chữ thường (Không gõ lại mã màu)
                else if (inputStack.has(DataComponents.CUSTOM_NAME)) {
                    Component inputCustomName = inputStack.get(DataComponents.CUSTOM_NAME);
                    String inputPlainName = inputCustomName.getString();

                    // Trường hợp A: Người chơi giữ nguyên chữ (chỉ sửa đồ hoặc ép sách)
                    if (this.itemName.equals(inputPlainName)) {
                        resultStack.set(DataComponents.CUSTOM_NAME, inputCustomName);
                    }
                    // Trường hợp B: Người chơi THÊM hoặc SỬA chữ khác vô (Ví dụ: từ "Kiếm" thành "Kiếm VIP")
                    else {
                        // Gọi hàm ma thuật trích xuất Style/Màu sắc từ sâu trong Component cũ
                        Style baseStyle = kyo$extractBaseStyle(inputCustomName);

                        // Đảm bảo tắt in nghiêng của đe trừ khi món đồ cũ cố tình bật nghiêng bằng &o
                        // CHỈ CẦN KIỂM TRA NHƯ THẾ NÀY LÀ ĐỦ:
                        if (!baseStyle.isItalic()) {
                            baseStyle = baseStyle.withItalic(false);
                        }

                        // Nhuộm lại màu cũ cho chuỗi văn bản mới nhập
                        resultStack.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName).withStyle(baseStyle));
                    }
                }
            }

            // Xóa án phạt sửa chữa & Giới hạn level tối đa gửi về Client
            resultStack.set(DataComponents.REPAIR_COST, 0);
            if (this.cost.get() >= 40) {
                this.cost.set(KyoAnvilConfig.maxRepairCost);
            }
        }
    }

    /**
     * HÀM MA THUẬT MỚI: Quét sâu vào cấu trúc dữ liệu để tìm ra màu sắc gốc của vũ khí
     */
    private Style kyo$extractBaseStyle(Component component) {
        if (component == null) return Style.EMPTY;

        // Nếu bản thân Component rễ có màu, lấy luôn rễ
        if (component.getStyle().getColor() != null) {
            return component.getStyle();
        }

        // Nếu rễ rỗng (do mod parse ra), lội vào danh sách các thằng con (siblings) bên trong
        for (Component sibling : component.getSiblings()) {
            if (sibling.getStyle().getColor() != null) {
                // Tìm thấy thằng con đầu tiên có màu (Ví dụ: màu đỏ cũ), lấy ngay Style của nó!
                return sibling.getStyle();
            }
        }

        // Không tìm thấy gì thì trả về style mặc định của rễ
        return component.getStyle();
    }

    // Hàm ma thuật biên dịch chuỗi mã màu & sang §
    private Component kyo$parseColorString(String text) {
        if (text == null) return Component.empty();
        String processed = text.replace('&', '§');

        MutableComponent root = Component.empty().withStyle(Style.EMPTY.withItalic(false));
        StringBuilder currentText = new StringBuilder();
        Style style = Style.EMPTY.withItalic(false);

        int i = 0;
        while (i < processed.length()) {
            char c = processed.charAt(i);
            if (c == '§' && i + 1 < processed.length()) {
                char code = Character.toLowerCase(processed.charAt(i + 1));

                if (currentText.length() > 0) {
                    root.append(Component.literal(currentText.toString()).withStyle(style));
                    currentText.setLength(0);
                }

                i += 2;
                switch (code) {
                    case '0' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.BLACK);
                    case '1' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.DARK_BLUE);
                    case '2' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.DARK_GREEN);
                    case '3' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.DARK_AQUA);
                    case '4' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.DARK_RED);
                    case '5' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.DARK_PURPLE);
                    case '6' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.GOLD);
                    case '7' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.GRAY);
                    case '8' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.DARK_GRAY);
                    case '9' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.BLUE);
                    case 'a' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.GREEN);
                    case 'b' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.AQUA);
                    case 'c' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.RED);
                    case 'd' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.LIGHT_PURPLE);
                    case 'e' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.YELLOW);
                    case 'f' -> style = Style.EMPTY.withItalic(false).withColor(ChatFormatting.WHITE);
                    case 'k' -> style = style.withObfuscated(true);
                    case 'l' -> style = style.withBold(true);
                    case 'm' -> style = style.withStrikethrough(true);
                    case 'n' -> style = style.withUnderlined(true);
                    case 'o' -> style = style.withItalic(true);
                    case 'r' -> style = Style.EMPTY.withItalic(false);
                    default -> currentText.append('§').append(processed.charAt(i - 1));
                }
            } else {
                currentText.append(c);
                i++;
            }
        }

        if (currentText.length() > 0) {
            root.append(Component.literal(currentText.toString()).withStyle(style));
        }

        return root;
    }
}