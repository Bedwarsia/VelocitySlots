package party.komas19.velocityslotsbungee;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ConfigUpdater {

    // Comments for each key, injected before the key
    private static final Map<String, List<String>> keyComments = new LinkedHashMap<>();
    static {
        keyComments.put("max-slots-mode", Arrays.asList(
                "# ----------------------------------------------------------",
                "# VelocitySlots Configuration File (BungeeCord)",
                "# ----------------------------------------------------------",
                "# Controls the 'max slots' shown in your server list (MOTD)",
                "# without affecting the real online player count.",
                "# Options:",
                "#   STATIC   -> always show a fixed number",
                "#   DYNAMIC  -> show online + offset",
                "# Note: This only fakes the max slots. Real limits still apply.",
                "# ----------------------------------------------------------",
                "# ------------------------",
                "# MAX SLOTS MODE",
                "# ------------------------",
                "# How max slots appear:",
                "#   DYNAMIC -> online + offset",
                "#   STATIC  -> fixed static-slots value"
        ));
        keyComments.put("offset", Arrays.asList(
                "# ------------------------",
                "# DYNAMIC MODE SETTINGS",
                "# ------------------------",
                "# Used only if max-slots-mode = DYNAMIC",
                "# Shows: online + offset",
                "# Example: 50 online + 10 offset => 50/60 shown"
        ));
        keyComments.put("static-slots", Arrays.asList(
                "# ------------------------",
                "# STATIC MODE SETTINGS",
                "# ------------------------",
                "# Used only if max-slots-mode = STATIC",
                "# Always shows this number as max slots",
                "# Example: 20 online with static-slots: 1 => 20/1 shown"
        ));
    }

    public static void update(Path configFile, Map<String, Object> defaults) throws IOException {
        Yaml yaml = new Yaml();

        // Load existing config
        Map<String, Object> existing = new LinkedHashMap<>();
        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                Object loaded = yaml.load(in);
                if (loaded instanceof Map) {
                    existing = (Map<String, Object>) loaded;
                }
            }
        }

        // Merge defaults with validation
        Map<String, Object> merged = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            String key = entry.getKey();
            Object defaultValue = entry.getValue();
            Object existingValue = existing.get(key);
            if (isValid(key, existingValue)) {
                merged.put(key, existingValue);
            } else {
                merged.put(key, defaultValue);
            }
        }

        // Write the config with comments
        try (BufferedWriter writer = Files.newBufferedWriter(configFile)) {
            for (Map.Entry<String, Object> entry : merged.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // Write comments first if any
                List<String> comments = keyComments.getOrDefault(key, Collections.emptyList());
                for (String comment : comments) {
                    writer.write(comment);
                    writer.newLine();
                }

                // Write key-value
                writer.write(key + ": " + value);
                writer.newLine();
                writer.newLine();
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
            case "config-version":
                return value instanceof Number;
            default:
                return true;
        }
    }
}
