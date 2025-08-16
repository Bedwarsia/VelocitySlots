package party.komas19.velocityslots;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigUpdater {

    public static void update(Path configFile, InputStream defaultConfigStream) throws IOException {
        Yaml yaml = new Yaml(new SafeConstructor());

        // Load defaults
        Map<String, Object> defaults = yaml.load(defaultConfigStream);
        if (defaults == null) defaults = new LinkedHashMap<>();

        // Load existing
        Map<String, Object> existing;
        try (InputStream in = Files.newInputStream(configFile)) {
            existing = yaml.load(in);
        }
        if (existing == null) existing = new LinkedHashMap<>();

        // Merge defaults with validation
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

        // Write updated config back
        Files.write(configFile, yaml.dump(newConfig).getBytes());
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
                return value instanceof Number;
            case "config-version":
                return value instanceof Number;
            default:
                return true;
        }
    }
}
