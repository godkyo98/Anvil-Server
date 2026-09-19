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
    @Shadow public int repairItemCountCost;
    @Shadow private String itemName;

    @Inject(method = "createResult", at = @At("RETURN"))
    private void kyo$advancedAnvilColorFix(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        ItemStack leftInput = menu.getSlot(0).getItem();
        ItemStack rightInput = menu.getSlot(1).getItem();
        ItemStack resultStack = menu.getSlot(2).getItem();

        // ════════════ TÍNH NĂNG MỚI: GHÉP ÁO GIÁP LAI ELYTRA ════════════
        if (resultStack.isEmpty() && kyo$isChestplate(leftInput) && rightInput.is(net.minecraft.world.item.Items.ELYTRA)) {
            ItemStack hybridElytra = new ItemStack(net.minecraft.world.item.Items.ELYTRA);

            kyo$applyHybridLogic(leftInput, rightInput, hybridElytra);

            menu.getSlot(2).set(hybridElytra); // FIX 1: Đổi setItem thành set
            this.cost.set(15);
            this.repairItemCountCost = 1;
            return;
        }

        // ════════════ GIỮ NGUYÊN CÁC TÍNH NĂNG SỬA LỖI MÀU SẮC CŨ ════════════
        if (!resultStack.isEmpty()) {
            if (this.itemName != null && !this.itemName.isEmpty()) {
                if (this.itemName.contains("&") || this.itemName.contains("§")) {
                    resultStack.set(DataComponents.CUSTOM_NAME, kyo$parseColorString(this.itemName));
                } else if (leftInput.has(DataComponents.CUSTOM_NAME)) {
                    Component inputCustomName = leftInput.get(DataComponents.CUSTOM_NAME);
                    String inputPlainName = inputCustomName.getString();

                    if (this.itemName.equals(inputPlainName)) {
                        resultStack.set(DataComponents.CUSTOM_NAME, inputCustomName);
                    } else {
                        Style baseStyle = kyo$extractBaseStyle(inputCustomName);
                        if (!baseStyle.isItalic()) {
                            baseStyle = baseStyle.withItalic(false);
                        }
                        resultStack.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName).withStyle(baseStyle));
                    }
                }
            }

            resultStack.set(DataComponents.REPAIR_COST, 0);
            if (this.cost.get() >= 40) {
                this.cost.set(KyoAnvilConfig.maxRepairCost);
            }
        }
    }

    private boolean kyo$isChestplate(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(net.minecraft.world.item.Items.NETHERITE_CHESTPLATE) ||
                stack.is(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE) ||
                stack.is(net.minecraft.world.item.Items.IRON_CHESTPLATE) ||
                stack.is(net.minecraft.world.item.Items.GOLDEN_CHESTPLATE) ||
                stack.is(net.minecraft.world.item.Items.CHAINMAIL_CHESTPLATE) ||
                stack.is(net.minecraft.world.item.Items.LEATHER_CHESTPLATE);
    }

    private void kyo$applyHybridLogic(ItemStack armor, ItemStack elytra, ItemStack result) {
        double armorValue = 0;
        double toughnessValue = 0;
        double knockbackRes = 0;
        String armorName = "";

        if (armor.is(net.minecraft.world.item.Items.NETHERITE_CHESTPLATE)) {
            armorValue = 8; toughnessValue = 3; knockbackRes = 0.1; armorName = "Netherite";
        } else if (armor.is(net.minecraft.world.item.Items.DIAMOND_CHESTPLATE)) {
            armorValue = 8; toughnessValue = 2; armorName = "Kim Cương";
        } else if (armor.is(net.minecraft.world.item.Items.IRON_CHESTPLATE)) {
            armorValue = 6; armorName = "Sắt";
        } else if (armor.is(net.minecraft.world.item.Items.CHAINMAIL_CHESTPLATE)) {
            armorValue = 5; armorName = "Xích";
        } else if (armor.is(net.minecraft.world.item.Items.GOLDEN_CHESTPLATE)) {
            armorValue = 5; armorName = "Vàng";
        } else if (armor.is(net.minecraft.world.item.Items.LEATHER_CHESTPLATE)) {
            armorValue = 3; armorName = "Da";
        }

        result.setDamageValue(elytra.getDamageValue());

        // FIX 2: Sửa lại package của ItemEnchantments cho chuẩn 26.2
        net.minecraft.world.item.enchantment.ItemEnchantments armorEnchants = armor.getOrDefault(DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        net.minecraft.world.item.enchantment.ItemEnchantments elytraEnchants = elytra.getOrDefault(DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
        net.minecraft.world.item.enchantment.ItemEnchantments.Mutable combinedEnchants = new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(armorEnchants);

        for (net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> enchant : elytraEnchants.keySet()) {
            int levelFromArmor = armorEnchants.getLevel(enchant);
            int levelFromElytra = elytraEnchants.getLevel(enchant);
            int finalLevel = (levelFromArmor == levelFromElytra && levelFromArmor > 0) ? levelFromArmor + 1 : Math.max(levelFromArmor, levelFromElytra);
            combinedEnchants.set(enchant, finalLevel);
        }
        result.set(DataComponents.ENCHANTMENTS, combinedEnchants.toImmutable());

        net.minecraft.world.item.component.ItemAttributeModifiers.Builder attrBuilder = net.minecraft.world.item.component.ItemAttributeModifiers.builder();
        if (armorValue > 0) {
            attrBuilder.add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR,
                    new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                            net.minecraft.resources.Identifier.parse("kyoanvil:hybrid_armor"), // FIX 3: Dùng Identifier.parse thay vì .of()
                            armorValue,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                    ),
                    net.minecraft.world.entity.EquipmentSlotGroup.CHEST
            );
        }
        if (toughnessValue > 0) {
            attrBuilder.add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS,
                    new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                            net.minecraft.resources.Identifier.parse("kyoanvil:hybrid_toughness"),
                            toughnessValue,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                    ),
                    net.minecraft.world.entity.EquipmentSlotGroup.CHEST
            );
        }
        if (knockbackRes > 0) {
            attrBuilder.add(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE,
                    new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                            net.minecraft.resources.Identifier.parse("kyoanvil:hybrid_knockback"),
                            knockbackRes,
                            net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                    ),
                    net.minecraft.world.entity.EquipmentSlotGroup.CHEST
            );
        }
        result.set(DataComponents.ATTRIBUTE_MODIFIERS, attrBuilder.build());

        String baseDisplayName = armorName + " Lai Elytra";
        if (armor.has(DataComponents.CUSTOM_NAME)) {
            baseDisplayName = armor.get(DataComponents.CUSTOM_NAME).getString() + " Lai Elytra";
        }
        result.set(DataComponents.CUSTOM_NAME, Component.literal("§6" + baseDisplayName).withStyle(Style.EMPTY.withItalic(false)));

        java.util.List<Component> loreLines = new java.util.ArrayList<>();
        if (armor.has(DataComponents.LORE)) {
            loreLines.addAll(armor.get(DataComponents.LORE).lines());
        }
        loreLines.add(Component.literal("§7- Món đồ đã được ghép cánh Elytra").withStyle(Style.EMPTY.withItalic(false)));

        // FIX 4: Đổi class Lore thành ItemLore theo chuẩn 26.2
        result.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(loreLines));
    }

    private Style kyo$extractBaseStyle(Component component) {
        if (component == null) return Style.EMPTY;
        if (component.getStyle().getColor() != null) return component.getStyle();
        for (Component sibling : component.getSiblings()) {
            if (sibling.getStyle().getColor() != null) return sibling.getStyle();
        }
        return component.getStyle();
    }

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