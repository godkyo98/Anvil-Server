package kyoanvil.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class KyoAnvilConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "kyo_anvil.json");

    // === CẤU HÌNH SỬA ĐỒ TRONG GIAO DIỆN ĐE (Slot GUI) ===
    public int repairCostLevelAmount = 1;       // Giá Level XP cố định mỗi lần sửa/đổi tên
    public int repairCostMaterialAmount = 1;    // Số lượng nguyên liệu tốn cố định cho 1 lần sửa (Mặc định: 1)
    public float percentRepairedPerAction = 0.3333f; // % Độ bền hồi phục cho mỗi lần gõ (0.3333 = 33.33%)

    // === CẤU HÌNH THẾ GIỚI KHỐI BLOCK ===
    public float anvilBreakChance = 0.02f;      // Tỉ lệ đe bị nứt/vỡ khi gõ đồ (0.02 = 2%)
    public boolean allowColorCodes = true;      // Cho phép dùng mã màu '&' khi đổi tên
    public boolean allowAnvilRestoration = true; // Cho phép đập Khối Sắt (Iron Block) ngoài đời để vá Đe bị nứt

    // === CẤU HÌNH LỆNH (COMMANDS) ===
    public String colorCommandAlias = "color"; // Người chơi gõ /color để xem bảng màu (Có thể đổi thành 'color', 'huongdan')

    public static KyoAnvilConfig INSTANCE = new KyoAnvilConfig();

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                INSTANCE = GSON.fromJson(reader, KyoAnvilConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}