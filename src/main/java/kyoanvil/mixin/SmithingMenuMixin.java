package kyoanvil.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public abstract class SmithingMenuMixin {

    @Inject(method = "createResult", at = @At("RETURN"))
    private void kyo$applyNetheriteUpgradeToHybridElytra(CallbackInfo ci) {
        SmithingMenu menu = (SmithingMenu) (Object) this;
        ItemStack templateSlot = menu.getSlot(0).getItem();
        ItemStack baseSlot = menu.getSlot(1).getItem();
        ItemStack ingredientSlot = menu.getSlot(2).getItem();

        // FIX 5: Chỉnh lại lỗi đánh máy cú pháp nguyên liệu Netherite
        if (templateSlot.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                && baseSlot.is(Items.ELYTRA)
                && ingredientSlot.is(Items.NETHERITE_INGOT)) {

            if (baseSlot.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
                net.minecraft.world.item.component.ItemAttributeModifiers modifiers = baseSlot.get(DataComponents.ATTRIBUTE_MODIFIERS);

                boolean isHybridDiamond = false;
                for (net.minecraft.world.item.component.ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
                    if (entry.modifier().id().getPath().equals("hybrid_toughness") && entry.modifier().amount() == 2.0) {
                        isHybridDiamond = true;
                        break;
                    }
                }

                if (isHybridDiamond) {
                    ItemStack netheriteHybrid = new ItemStack(Items.ELYTRA);

                    netheriteHybrid.setDamageValue(baseSlot.getDamageValue());
                    if (baseSlot.has(DataComponents.ENCHANTMENTS)) {
                        netheriteHybrid.set(DataComponents.ENCHANTMENTS, baseSlot.get(DataComponents.ENCHANTMENTS));
                    }

                    net.minecraft.world.item.component.ItemAttributeModifiers.Builder attrBuilder = net.minecraft.world.item.component.ItemAttributeModifiers.builder();
                    attrBuilder.add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR,
                            new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                    net.minecraft.resources.Identifier.parse("kyoanvil:hybrid_armor"), // FIX 6: Dùng Identifier.parse
                                    8.0,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                            ),
                            net.minecraft.world.entity.EquipmentSlotGroup.CHEST
                    );
                    attrBuilder.add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS,
                            new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                    net.minecraft.resources.Identifier.parse("kyoanvil:hybrid_toughness"),
                                    3.0,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                            ),
                            net.minecraft.world.entity.EquipmentSlotGroup.CHEST
                    );
                    attrBuilder.add(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE,
                            new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                    net.minecraft.resources.Identifier.parse("kyoanvil:hybrid_knockback"),
                                    0.1,
                                    net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
                            ),
                            net.minecraft.world.entity.EquipmentSlotGroup.CHEST
                    );
                    netheriteHybrid.set(DataComponents.ATTRIBUTE_MODIFIERS, attrBuilder.build());

                    String currentName = "Netherite Lai Elytra";
                    if (baseSlot.has(DataComponents.CUSTOM_NAME)) {
                        String oldName = baseSlot.get(DataComponents.CUSTOM_NAME).getString();
                        if (oldName.contains("Kim Cương")) {
                            currentName = oldName.replace("Kim Cương", "Netherite");
                        } else {
                            currentName = oldName + " (Netherite)";
                        }
                    }
                    netheriteHybrid.set(DataComponents.CUSTOM_NAME, Component.literal("§6" + currentName).withStyle(Style.EMPTY.withItalic(false)));

                    if (baseSlot.has(DataComponents.LORE)) {
                        netheriteHybrid.set(DataComponents.LORE, baseSlot.get(DataComponents.LORE));
                    }

                    // FIX 7: Đổi setItem thành set
                    menu.getSlot(3).set(netheriteHybrid);
                }
            }
        }
    }
}