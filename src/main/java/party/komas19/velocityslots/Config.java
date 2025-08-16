package party.komas19.velocityslots;

import com.velocitypowered.api.plugin.PluginContainer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Config {

    private static Path configFile;
    private static Logger logger;
    private static Map<String, Object> data = new LinkedHashMap<>();

    public static void init(PluginContainer plugin, Path dataDirectory, Logger log) {
        logger = log;
        configFile = dataDirectory.resolve("config.yml");

        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            try (InputStream defaultIn = Objects.requireNonNull(
                    plugin.getInstance().get().getClass().getClassLoader().getResourceAsStream("config.yml"),
                    "Default config.yml missing from jar!"
            )) {
                if (Files.notExists(configFile)) {
                    // Copy fresh config.yml
                    Files.copy(defaultIn, configFile);
                    logger.info("Generated fresh config.yml");
                } else {
                    // Update with defaults (remove old keys + add missing ones)
                    ConfigUpdater.update(configFile, defaultIn);
                    logger.info("Updated config.yml");
                }
            }

            // Load into memory for getters
            load();
        } catch (IOException e) {
            logger.error("Failed to initialize config.yml", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static void load() throws IOException {
        Yaml yaml = new Yaml(new SafeConstructor());
        try (InputStream in = Files.newInputStream(configFile)) {
            Object loaded = yaml.load(in);
            if (loaded instanceof Map) {
                data = (Map<String, Object>) loaded;
            }
        }
    }

    public static String getMode() {
        Object v = data.getOrDefault("max-slots-mode", "DYNAMIC");
        return v instanceof String ? ((String) v).toUpperCase() : "DYNAMIC";
    }

    public static int getOffset() {
        Object v = data.getOrDefault("offset", 10);
        return v instanceof Number ? ((Number) v).intValue() : 10;
    }

    public static int getStaticSlots() {
        Object v = data.getOrDefault("static-slots", 60);
        return v instanceof Number ? ((Number) v).intValue() : 60;
    }
}