package kyoanvil.mixin;

import kyoanvil.config.KyoAnvilConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.ContainerLevelAccess;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {

    public AnvilMenuMixin(int i, ItemCombinerMenuSlotDefinition slotDefinition) {
        super(null, i, null, null, slotDefinition);
    }

    @Shadow @Final private DataSlot cost;
    @Shadow private int repairItemCountCost;

    @Inject(method = "createResult", at = @At("RETURN"))
    private void kyo$overrideAnvilRepairLogic(CallbackInfo ci) {
        AnvilMenu menu = (AnvilMenu) (Object) this;

        ItemStack inputLeft = menu.getSlot(0).getItem();
        ItemStack inputRight = menu.getSlot(1).getItem();
        ItemStack outputSlot = menu.getSlot(2).getItem();

        if (outputSlot.isEmpty()) return;

        boolean isUpgrading = false; // Cờ kiểm tra xem có đang ghép Phù Phép không

        // 1. XỬ LÝ SỬA CHỮA BẰNG VẬT LIỆU (Thỏi Sắt, Kim Cương, Phantoms...)
        if (this.repairItemCountCost > 0 && !inputRight.isEmpty()) {
            int maxDamage = inputLeft.getMaxDamage();
            int currentDamage = inputLeft.getDamageValue();

            if (currentDamage > 0) {
                int repairAmount = Mth.ceil(maxDamage * KyoAnvilConfig.INSTANCE.percentRepairedPerAction);
                int newDamage = Math.max(0, currentDamage - repairAmount);

                outputSlot.setDamageValue(newDamage);
                this.repairItemCountCost = KyoAnvilConfig.INSTANCE.repairCostMaterialAmount;
            }
        }
        // 2. KIỂM TRA XEM CÓ PHẢI ĐANG ÉP PHÙ PHÉP / GỘP ĐỒ KHÔNG
        else if (!inputRight.isEmpty()) {
            // Nếu ô bên phải là Sách Phù Phép (Enchanted Book) hoặc cùng loại với ô bên trái (Ghép 2 cây kiếm)
            if (inputRight.is(Items.ENCHANTED_BOOK) || inputLeft.is(inputRight.getItem())) {
                isUpgrading = true;
            }
        }

        // 3. TÍNH TOÁN GIÁ LEVEL KINH NGHIỆM (XP)
        if (this.cost.get() > 0) {
            if (isUpgrading) {
                // NẾU LÀ NÂNG CẤP/ÉP SÁCH:
                // Để nguyên giá do Vanilla tự tính (Dựa trên độ xịn của Enchantment)
                // Chỉ "Neo" giá tối đa là 39 Level để không bao giờ bị lỗi "Too Expensive" (>= 40)
                if (this.cost.get() >= 40) {
                    this.cost.set(39);
                }
            } else {
                // NẾU CHỈ LÀ ĐỔI TÊN HOẶC SỬA BẰNG THỎI SẮT:
                // Gán mức giá rẻ mạt cố định từ file Config (Mặc định là 1 Level)
                this.cost.set(KyoAnvilConfig.INSTANCE.repairCostLevelAmount);
            }
        }

        // 4. XÓA ÁN PHẠT CỘNG DỒN (Prior Work Penalty)
        // Đây là tính năng then chốt: Đảm bảo lần sửa thứ 10 cũng rẻ y như lần sửa đầu tiên.
        outputSlot.set(DataComponents.REPAIR_COST, 0);

        // 5. ĐỔI MÀU SẮC CHO TÊN (Dùng ký tự &)
        if (KyoAnvilConfig.INSTANCE.allowColorCodes && outputSlot.has(DataComponents.CUSTOM_NAME)) {
            String rawName = outputSlot.get(DataComponents.CUSTOM_NAME).getString();
            if (rawName.contains("&")) {
                String coloredName = rawName.replace('&', '§');
                outputSlot.set(DataComponents.CUSTOM_NAME, Component.literal(coloredName));
            }
        }
    }
}