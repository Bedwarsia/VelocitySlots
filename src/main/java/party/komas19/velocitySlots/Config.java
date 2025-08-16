package party.komas19.velocitySlots;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Config {
    private final File configFile;
    private Map<String, Object> data;
    private final int CURRENT_VERSION = 2; // increment this when changing defaults
    private final Set<String> allowedKeys = Set.of(
            "config-version",
            "max-slots-mode",
            "offset",
            "static-slots"
    );

    @Inject
    public Config(@DataDirectory File dataDirectory) throws IOException {
        configFile = new File(dataDirectory, "config.yml");
        if (!configFile.exists()) {
            dataDirectory.mkdirs();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (in != null) {
                    Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    // create a default config if no embedded resource
                    saveDefaultConfig();
                }
            }
        }
        load();
        updateConfig();
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            data = yaml.load(fis);
            if (data == null) {
                data = new HashMap<>();
            }
        }
    }

    private void saveDefaultConfig() throws IOException {
        data = new HashMap<>();
        data.put("config-version", CURRENT_VERSION);
        data.put("max-slots-mode", "FAKE");
        data.put("offset", 10);
        data.put("static-slots", 60);
        save();
    }

    private void updateConfig() throws IOException {
        int version = (int) data.getOrDefault("config-version", 0);
        boolean updated = false;

        // Add missing keys with defaults
        Map<String, Object> defaults = Map.of(
            "max-slots-mode", "DYNAMIC",
            "offset", 10,
            "static-slots", 60
        );

        for (String key : defaults.keySet()) {
            if (!data.containsKey(key)) {
                data.put(key, defaults.get(key));
                updated = true;
            }
        }

        // Validate max-slots-mode
        String mode = (String) data.getOrDefault("max-slots-mode", "DYNAMIC");
        if (!Set.of("DYNAMIC", "STATIC").contains(mode)) {
            data.put("max-slots-mode", "DYNAMIC");
            updated = true;
        }

        // Remove deprecated keys
        Iterator<String> it = data.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (!allowedKeys.contains(key)) {
                it.remove();
                updated = true;
            }
        }

        // Update version
        if (version < CURRENT_VERSION) {
            data.put("config-version", CURRENT_VERSION);
            updated = true;
        }

        if (updated) save();
    }

    public String getMode() {
        String mode = (String) data.getOrDefault("max-slots-mode", "DYNAMIC");
        if (!Set.of("DYNAMIC", "STATIC").contains(mode)) return "DYNAMIC";
        return mode;
    }

    public int getOffset() {
        Object value = data.getOrDefault("offset", 10);
        if (!(value instanceof Number)) return 10;
        return ((Number) value).intValue();
    }

    public int getStaticSlots() {
        Object value = data.getOrDefault("static-slots", 60);
        if (!(value instanceof Number)) return 60;
        return ((Number) value).intValue();
    }

}
