package kyoanvil;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import kyoanvil.config.KyoAnvilConfig;

public class KyoAnvil implements ModInitializer {
	@Override
	public void onInitialize() {
		// 1. Tự động sinh hoặc nạp Config
		KyoAnvilConfig.load();
		System.out.println("[KyoAnvil] Đã nạp thành công hệ thống Đe Server-side!");

		// 2. Tính năng: Cầm Khối Sắt gõ vào đe để phục hồi độ bền
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (!KyoAnvilConfig.enableIronIngotRepair) return InteractionResult.PASS;
			if (world.isClientSide()) return InteractionResult.PASS;

			BlockPos pos = hitResult.getBlockPos();
			BlockState state = world.getBlockState(pos);
			ItemStack stack = player.getItemInHand(hand);

			// Kiểm tra nếu cầm Khối Sắt và click vào Đe bị nứt hoặc hỏng
			if (stack.is(Items.IRON_INGOT)) {
				BlockState newState = null;

				if (state.is(Blocks.DAMAGED_ANVIL)) {
					newState = Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, state.getValue(AnvilBlock.FACING));
				} else if (state.is(Blocks.CHIPPED_ANVIL)) {
					newState = Blocks.ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, state.getValue(AnvilBlock.FACING));
				}

				if (newState != null) {
					// Cập nhật trạng thái đe mới
					world.setBlockAndUpdate(pos, newState);
					// Phát âm thanh gõ đe để người chơi nhận biết
					world.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0F, 1.0F);

					// Trừ 1 Khối sắt (nếu người chơi ở mode sinh tồn)
					if (!player.isCreative()) {
						stack.shrink(1);
					}
					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.PASS;
		});
	}
}