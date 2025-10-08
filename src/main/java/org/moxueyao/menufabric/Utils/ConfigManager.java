package org.moxueyao.menufabric.Utils;

import org.moxueyao.menufabric.Menufabric;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

public class ConfigManager {
    public String inCold = "[§a系统§f] §c距离传送冷却还有%s秒！";
    public tpPlayer TP = new tpPlayer();
    public backDeathLoc back =  new backDeathLoc();
    public Home home = new Home();
    private static final Path CONFIG_PATH = Paths.get("config", "menufabric.json");

    public static class tpPlayer {
        public int coldTime = 300;
        public String tpPlayerFailed = "[§a系统§f] §c传送失败！";
        public String tpPlayerSuccess = "[§a系统§f] §6已传送到玩家 §f%s §6的位置。";
    }
    public static class backDeathLoc {
        public int coldTime = 300;
        public String noDeathLoc = "[§a系统§f] §c你没有记录的死亡位置！";
        public String tpDeathFailed = "[§a系统§f] §c目标维度不可用！";
        public String tpDeathSuccess = "[§a系统§f] §6已传送到你死亡的位置。";
    }
    public static class Home{
        public int coldTime = 300;
        public String noHome = "[§a系统§f] §c你还没有设置重生点！";
        public String tpHomeSuccess = "[§a系统§f] §6已传送到你的重生点。";
    }

    public static ConfigManager load() {
        try {
            File configFile = CONFIG_PATH.toFile();
            if (!configFile.exists()) {
                return save(); // 如果文件不存在，创建默认配置
            }
            try (FileReader reader = new FileReader(configFile)) {
                return GSON.fromJson(reader, ConfigManager.class); // 读取配置
            }
        } catch (Exception e) {
            System.err.println();
            Menufabric.LOGGER.error("无法加载配置文件，使用默认配置：{}", e.getMessage());
            return new ConfigManager();
        }
    }

    public static ConfigManager save() {
        try {
            File configFile = CONFIG_PATH.toFile();
            configFile.getParentFile().mkdirs(); // 确保目录存在
            ConfigManager defaultConfig = new ConfigManager();
            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(defaultConfig, writer); // 写入默认配置
            }
            return defaultConfig;
        } catch (Exception e) {
            Menufabric.LOGGER.error("无法创建默认配置文件：{}", e.getMessage());
            return new ConfigManager();
        }
    }
}