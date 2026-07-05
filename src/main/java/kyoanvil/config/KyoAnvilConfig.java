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
    private static final File FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "kyo_anvil.json");

    public static int maxRepairCost = 39;
    public static float anvilDamageChance = 0.02f;
    public static boolean enableColorRename = true;
    public static boolean enableIronIngotRepair = true;

    // Thêm cấu hình alias cho lệnh xem màu sắc
    public static String colorCommandAlias = "mausac";

    public static void load() {
        if (FILE.exists()) {
            try (FileReader reader = new FileReader(FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    maxRepairCost = data.maxRepairCost;
                    anvilDamageChance = data.anvilDamageChance;
                    enableColorRename = data.enableColorRename;
                    enableIronIngotRepair = data.enableIronIngotRepair;
                    if (data.colorCommandAlias != null) colorCommandAlias = data.colorCommandAlias;
                }
            } catch (IOException e) {
                System.out.println("[KyoAnvil] Lỗi đọc file config!");
            }
        } else {
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            ConfigData data = new ConfigData();
            data.maxRepairCost = maxRepairCost;
            data.anvilDamageChance = anvilDamageChance;
            data.enableColorRename = enableColorRename;
            data.enableIronIngotRepair = enableIronIngotRepair;
            data.colorCommandAlias = colorCommandAlias;
            GSON.toJson(data, writer);
        } catch (IOException e) {
            System.out.println("[KyoAnvil] Lỗi lưu file config!");
        }
    }

    private static class ConfigData {
        int maxRepairCost = KyoAnvilConfig.maxRepairCost;
        float anvilDamageChance = KyoAnvilConfig.anvilDamageChance;
        boolean enableColorRename = KyoAnvilConfig.enableColorRename;
        boolean enableIronIngotRepair = KyoAnvilConfig.enableIronIngotRepair;
        String colorCommandAlias = KyoAnvilConfig.colorCommandAlias;
    }
}