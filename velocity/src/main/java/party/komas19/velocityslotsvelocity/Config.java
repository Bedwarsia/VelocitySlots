package party.komas19.velocityslotsvelocity;

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

    private static final Map<String, Object> DEFAULTS = new LinkedHashMap<>();
    static {
        DEFAULTS.put("max-slots-mode", "DYNAMIC");
        DEFAULTS.put("offset", 0);
        DEFAULTS.put("static-slots", 100);
    }

    private static Path configFile;
    private static Logger logger;
    private static Map<String, Object> data = new LinkedHashMap<>();

    /**
     * Initializes the config file.
     * Copies missing keys, replaces invalid values, and preserves comments.
     */
    public static void init(Class<?> pluginClass, Path dataDirectory, Logger log) {
        logger = log;
        configFile = dataDirectory.resolve("config.yml");

        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            // Ensure config exists
            if (Files.notExists(configFile)) {
                try (InputStream in = Objects.requireNonNull(
                        pluginClass.getClassLoader().getResourceAsStream("config.yml"),
                        "Default config.yml missing from jar!"
                )) {
                    Files.copy(in, configFile);
                    logger.info("Generated fresh config.yml");
                }
            }

            // Update config safely
            ConfigUpdater.update(configFile, DEFAULTS);

            // Load into memory
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
            } else {
                data = new LinkedHashMap<>();
            }
        }

        // Validate loaded values
        for (Map.Entry<String, Object> entry : DEFAULTS.entrySet()) {
            String key = entry.getKey();
            Object value = data.get(key);
            if (!isValid(key, value)) {
                data.put(key, entry.getValue());
            }
        }
    }

    private static boolean isValid(String key, Object value) {
        if (value == null) return false;
        switch (key) {
            case "max-slots-mode":
                if (!(value instanceof String)) return false;
                String mode = ((String) value).toUpperCase();
                return mode.equals("DYNAMIC") || mode.equals("STATIC");
            case "offset":
            case "static-slots":
                return value instanceof Number;
            default:
                return true;
        }
    }

    public static String getMode() {
        Object v = data.getOrDefault("max-slots-mode", DEFAULTS.get("max-slots-mode"));
        return v instanceof String ? ((String) v).toUpperCase() : (String) DEFAULTS.get("max-slots-mode");
    }

    public static int getOffset() {
        Object v = data.getOrDefault("offset", DEFAULTS.get("offset"));
        return v instanceof Number ? ((Number) v).intValue() : (int) DEFAULTS.get("offset");
    }

    public static int getStaticSlots() {
        Object v = data.getOrDefault("static-slots", DEFAULTS.get("static-slots"));
        return v instanceof Number ? ((Number) v).intValue() : (int) DEFAULTS.get("static-slots");
    }
}
