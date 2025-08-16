package party.komas19.velocityslots;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigUpdater {

    private static final LinkedHashMap<String, String[]> defaultsWithComments = new LinkedHashMap<>();

    static {
        defaultsWithComments.put("max-slots-mode", new String[]{
                "# ----------------------------------------------------------",
                "# VelocitySlots Configuration File",
                "# ----------------------------------------------------------",
                "# Controls the 'max slots' shown in your server list (MOTD)",
                "# without affecting the real online player count.",
                "#",
                "# Options:",
                "#   STATIC   -> always show a fixed number",
                "#   DYNAMIC  -> show online + offset",
                "#",
                "# Note: This only fakes the max slots. Real limits still apply.",
                "# ----------------------------------------------------------",
                "#",
                "# ------------------------",
                "# MAX SLOTS MODE",
                "# ------------------------",
                "# How max slots appear:",
                "#   DYNAMIC -> online + offset",
                "#   STATIC  -> fixed static-slots value"
        });
        defaultsWithComments.put("offset", new String[]{
                "# ------------------------",
                "# DYNAMIC MODE SETTINGS",
                "# ------------------------",
                "# Used only if max-slots-mode = DYNAMIC",
                "# Shows: online + offset",
                "# Example: 50 online + 10 offset => 50/60 shown"
        });
        defaultsWithComments.put("static-slots", new String[]{
                "# ------------------------",
                "# STATIC MODE SETTINGS",
                "# ------------------------",
                "# Used only if max-slots-mode = STATIC",
                "# Always shows this number as max slots",
                "# Example: 20 online with static-slots: 1 => 20/1 shown"
        });
        defaultsWithComments.put("config-version", new String[]{
                "# ------------------------",
                "# CONFIG VERSION",
                "# ------------------------",
                "# Used internally to track configuration version"
        });
    }

    public static void update(Path configFile, InputStream defaultConfigStream) throws IOException {
        Yaml yaml = new Yaml(new SafeConstructor());

        // Load defaults from JAR
        Map<String, Object> defaults = yaml.load(defaultConfigStream);
        if (defaults == null) defaults = new LinkedHashMap<>();

        // Load existing config
        Map<String, Object> existing;
        if (Files.exists(configFile)) {
            try (InputStream in = Files.newInputStream(configFile)) {
                existing = yaml.load(in);
            }
            if (existing == null) existing = new LinkedHashMap<>();
        } else {
            existing = new LinkedHashMap<>();
        }

        // Merge: replace invalid values with defaults
        Map<String, Object> newConfig = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            String key = entry.getKey();
            Object defaultValue = entry.getValue();
            Object existingValue = existing.get(key);

            if (isValid(key, existingValue)) {
                newConfig.put(key, existingValue);
            } else {
                newConfig.put(key, defaultValue);
            }
        }

        // Write back valid/merged values
        Files.write(configFile, yaml.dump(newConfig).getBytes());

        // Append missing keys with comments (preserves manual comments)
        List<String> lines = Files.exists(configFile)
                ? Files.readAllLines(configFile)
                : new ArrayList<>();

        Set<String> existingKeys = lines.stream()
                .map(line -> line.split(":", 2)[0].trim())
                .collect(Collectors.toSet());

        try (BufferedWriter writer = Files.newBufferedWriter(configFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for (String key : defaults.keySet()) {
                if (!existingKeys.contains(key)) {
                    String[] comments = defaultsWithComments.getOrDefault(key, new String[0]);
                    for (String commentLine : comments) {
                        writer.write(commentLine);
                        writer.newLine();
                    }
                    writer.write(key + ": " + defaults.get(key));
                    writer.newLine();
                }
            }
        }
    }

    private static boolean isValid(String key, Object value) {
        if (value == null) return false;

        switch (key) {
            case "max-slots-mode":
                if (!(value instanceof String)) return false;
                String mode = ((String) value).toUpperCase();
                return mode.equals("DYNAMIC") || mode.equals("STATIC"); // adapt if default changes
            case "offset":
            case "static-slots":
            case "config-version":
                return value instanceof Number;
            default:
                return true;
        }
    }
}
