package org.moxueyao.menufabric.Utils;

import org.moxueyao.menufabric.Menufabric;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigManager {
    public String inCold = "[§a系统§f] §c距离传送冷却还有%s秒！";
    public tpPlayer TP = new tpPlayer();
    public backDeathLoc back =  new backDeathLoc();
    public Home home = new Home();
    private static final Yaml YAML = new Yaml();
    private static final Path CONFIG_PATH = Paths.get("config", "menufabric.yml");

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
            if (Files.exists(CONFIG_PATH)) {
                String yamlContent = Files.readString(CONFIG_PATH);
                return YAML.loadAs(yamlContent, ConfigManager.class);
            } else {
                ConfigManager config = new ConfigManager();
                save(config);
                return config;
            }
        } catch (IOException e) {
            Menufabric.LOGGER.warn("未找到配置文件,将使用默认配置！");
            return new ConfigManager();
        }
    }

    public static void save(ConfigManager config) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            LoaderOptions loaderOptions = new LoaderOptions();
            Constructor constructor = new Constructor(ConfigManager.class, loaderOptions);
            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Representer representer = new Representer(options);
            representer.addClassTag(ConfigManager.class, Tag.MAP);
            Yaml yaml = new Yaml(constructor, representer, options);
            String yamlContent = yaml.dump(config);
            Files.writeString(CONFIG_PATH, yamlContent);
        } catch (IOException e) {
            Menufabric.LOGGER.error("保存配置文件出错!", e);
        }
    }
}