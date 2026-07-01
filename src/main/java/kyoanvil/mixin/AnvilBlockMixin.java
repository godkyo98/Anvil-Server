package kyoanvil.mixin;

import kyoanvil.config.KyoAnvilConfig;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(AnvilBlock.class)
public abstract class AnvilBlockMixin {

    /**
     * Bắt thẳng vào hàm tĩnh (static method) tính toán độ sát thương của cái Đe.
     * Hàm này trong Vanilla sẽ Random 12% (0.12f) để quyết định xem đe có bị nứt thêm 1 bậc hay không.
     */
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private static void kyo$applyConfigAnvilDamage(BlockState state, CallbackInfoReturnable<BlockState> cir) {

        // Tạo một Random để giả lập cơ chế tung xí ngầu của Minecraft
        Random random = new Random();

        // Lấy tỉ lệ vỡ đe từ file Config của bạn (Ví dụ: 0.02f = 2%)
        float breakChance = KyoAnvilConfig.INSTANCE.anvilBreakChance;

        // Nếu xí ngầu "may mắn" (không rơi vào tỉ lệ vỡ)
        if (random.nextFloat() >= breakChance) {
            // Trả về chính BlockState hiện tại (Đe giữ nguyên trạng thái, KHÔNG bị nứt thêm)
            cir.setReturnValue(state);
            cir.cancel(); // Dừng ngay hàm của Vanilla lại
        }

        // Ngược lại, nếu xí ngầu rủi ro (< breakChance), ta để hàm Vanilla tự chạy tiếp để đe nứt bình thường!
    }
}