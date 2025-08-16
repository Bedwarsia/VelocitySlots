package party.komas19.velocitySlots;

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

        // Load default config (from JAR)
        Map<String, Object> defaultConfig = yaml.load(defaultConfigStream);
        if (defaultConfig == null) defaultConfig = new LinkedHashMap<>();

        // Load existing config (on disk)
        Map<String, Object> existingConfig;
        try (InputStream existingStream = Files.newInputStream(configFile)) {
            existingConfig = yaml.load(existingStream);
        }
        if (existingConfig == null) existingConfig = new LinkedHashMap<>();

        // Merge missing keys from default
        boolean changed = mergeDefaults(existingConfig, defaultConfig);

        if (changed) {
            // Save updated config
            Files.write(configFile, yaml.dump(existingConfig).getBytes());
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean mergeDefaults(Map<String, Object> target, Map<String, Object> defaults) {
        boolean changed = false;
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            if (!target.containsKey(entry.getKey())) {
                target.put(entry.getKey(), entry.getValue());
                changed = true;
            } else if (entry.getValue() instanceof Map && target.get(entry.getKey()) instanceof Map) {
                // Recursive merge for nested sections
                changed |= mergeDefaults(
                        (Map<String, Object>) target.get(entry.getKey()),
                        (Map<String, Object>) entry.getValue()
                );
            }
        }
        return changed;
    }
}

