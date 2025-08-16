package party.komas19.velocitySlots;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class Config {
    private final File configFile;
    private Map<String, Object> data;

    @Inject
    public Config(@DataDirectory File dataDirectory) throws IOException {
        configFile = new File(dataDirectory, "config.yml");
        if (!configFile.exists()) {
            dataDirectory.mkdirs();
            try (InputStream in = getClass().getClassLoader().getResourceAsStream("config.yml")) {
                if (in != null) {
                    Files.copy(in, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }

        }
        load();
    }

    @SuppressWarnings("unchecked")
    public void load() throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            data = yaml.load(fis);
        }
    }

    public String getMode() {
        return (String) data.getOrDefault("max-slots-mode", "FAKE");
    }

    public int getOffset() {
        return (int) data.getOrDefault("offset", 0);
    }

    public int getStaticSlots() {
        return (int) data.getOrDefault("static-slots", 100);
    }

    public int getUnlimitedSlots() {
        return (int) data.getOrDefault("unlimited-slots", -1);
    }
}
