package kyoanvil;

import com.mojang.brigadier.CommandDispatcher;
import kyoanvil.command.KyoColorCommand;
import kyoanvil.config.KyoAnvilConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KyoAnvil implements ModInitializer {

	// Khai báo ID và Bộ ghi log (Logger) chuẩn của Fabric
	public static final String MOD_ID = "kyoanvil";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[Kyo Anvil] Đang khởi tạo hệ thống...");

		KyoAnvilConfig.load();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			registerCommands(dispatcher);

			// THÊM DÒNG NÀY ĐỂ KÍCH HOẠT LỆNH MÀU SẮC
			KyoColorCommand.register(dispatcher);
		});

		registerAnvilRestoration();

		LOGGER.info("[Kyo Anvil] Khởi tạo thành công! Sẵn sàng phục vụ.");
	}

	/**
	 * Hàm đăng ký lệnh /kyoanvil reload
	 */
	private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal(MOD_ID)
				.requires(source -> source.getPlayer() == null || source.getServer().getPlayerList().isOp(source.getPlayer().nameAndId())) // Chỉ Admin/Console mới được dùng // Chỉ Admin/Console mới được dùng
				.then(Commands.literal("reload")
						.executes(context -> {
							KyoAnvilConfig.load();
							context.getSource().sendSuccess(
									() -> Component.literal("§a[Kyo Anvil] Đã tải lại file cấu hình (Config) thành công!"),
									true
							);
							return 1;
						})
				)
		);
	}

	/**
	 * Hàm xử lý logic Phục hồi độ bền của Đe bằng Khối Sắt
	 */
	private void registerAnvilRestoration() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			// Bỏ qua nếu là Client hoặc config không cho phép tính năng này
			if (world.isClientSide() || !KyoAnvilConfig.INSTANCE.allowAnvilRestoration) {
				return InteractionResult.PASS;
			}

			var pos = hitResult.getBlockPos();
			BlockState state = world.getBlockState(pos);
			var stack = player.getItemInHand(hand);

			// Kiểm tra nếu người chơi cầm Khối Sắt (Iron Block) trên tay
			if (stack.is(Items.IRON_BLOCK)) {
				var currentBlock = state.getBlock();
				BlockState newState = null;

				// Xác định trạng thái mới của đe
				if (currentBlock == Blocks.DAMAGED_ANVIL) {
					newState = Blocks.CHIPPED_ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, state.getValue(AnvilBlock.FACING));
				} else if (currentBlock == Blocks.CHIPPED_ANVIL) {
					newState = Blocks.ANVIL.defaultBlockState().setValue(AnvilBlock.FACING, state.getValue(AnvilBlock.FACING));
				}

				// Nếu đe được sửa thành công
				if (newState != null) {
					if (!player.getAbilities().instabuild) {
						stack.shrink(1); // Trừ 1 khối sắt (trừ khi đang ở chế độ Sáng Tạo)
					}
					// Phát tiếng búa tạ gõ đe chân thực
					world.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1.0f, 1.0f);
					// Cập nhật khối block ngoài thế giới
					world.setBlockAndUpdate(pos, newState);

					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.PASS;
		});
	}
}