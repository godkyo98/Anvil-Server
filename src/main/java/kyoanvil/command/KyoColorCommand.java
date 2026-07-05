package kyoanvil.command;

import com.mojang.brigadier.CommandDispatcher;
import kyoanvil.config.KyoAnvilConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class KyoColorCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        // 1. Luôn luôn đăng ký lệnh gốc cố định (Để Admin yên tâm dùng dù config bị đổi)
        dispatcher.register(Commands.literal("kyocolor")
            .executes(context -> showColorHelp(context.getSource()))
        );

        // 2. Đăng ký lệnh phụ (Alias) lấy từ file Config
        // Lệnh này có thể tùy biến thành "mausac", "huongdan", "color"
        String alias = KyoAnvilConfig.colorCommandAlias; // CHỈ GỌI TRỰC TIẾP TÊN BIẾN TĨNH

        if (alias != null && !alias.isEmpty() && !alias.equalsIgnoreCase("kyocolor")) {
            dispatcher.register(Commands.literal(alias)
                .executes(context -> showColorHelp(context.getSource()))
            );
        }
    }

    private static int showColorHelp(CommandSourceStack source) {
        // In bảng hướng dẫn màu sắc siêu chuẩn ra chat
        source.sendSystemMessage(Component.literal("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        source.sendSystemMessage(Component.literal("           §6§lBẢNG MÃ MÀU TEA SERVER"));
        source.sendSystemMessage(Component.literal(""));
        source.sendSystemMessage(Component.literal("§0&0 Đen       §8&8 Xám Đậm   §7&7 Xám       §f&f Trắng"));
        source.sendSystemMessage(Component.literal("§c&c Đỏ        §4&4 Đỏ Đậm    §e&e Vàng       §6&6 Vàng Cam"));
        source.sendSystemMessage(Component.literal("§a&a Xanh Lục  §2&2 Lục Đậm   §b&b Xanh Lơ    §3&3 Lam Đậm"));
        source.sendSystemMessage(Component.literal("§9&9 Xanh Biển §1&1 Biển Đậm  §d&d Hồng       §5&5 Tím Đậm"));
        source.sendSystemMessage(Component.literal(""));
        source.sendSystemMessage(Component.literal("           §b§lCÁC ĐỊNH DẠNG CHỮ"));
        source.sendSystemMessage(Component.literal(""));
        source.sendSystemMessage(Component.literal("§l&l In Đậm    §r§o&o In Nghiêng  §r§n&n Gạch Dưới"));
        source.sendSystemMessage(Component.literal("§m&m Gạch Ngang  §k&k Magic      §r&r Reset (Xóa màu)"));
        source.sendSystemMessage(Component.literal("§8§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        return 1;
    }
}