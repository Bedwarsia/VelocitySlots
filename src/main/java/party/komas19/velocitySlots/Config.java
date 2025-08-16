package party.komas19.velocitySlots;

import com.velocitypowered.api.plugin.PluginContainer;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Config {

    private static Path configFile;
    private static Logger logger;

    public static void init(PluginContainer plugin, Path dataDirectory, Logger log) {
        logger = log;
        configFile = dataDirectory.resolve("config.yml");

        try {
            if (Files.notExists(dataDirectory)) {
                Files.createDirectories(dataDirectory);
            }

            if (Files.notExists(configFile)) {
                // Copy fresh config.yml from JAR
                try (InputStream in = Objects.requireNonNull(
                        plugin.getInstance().get().getClass().getClassLoader().getResourceAsStream("config.yml")
                )) {
                    Files.copy(in, configFile);
                    logger.info("Generated fresh config.yml");
                }
            } else {
                // Update existing config.yml with defaults
                try (InputStream in = plugin.getInstance().get().getClass().getClassLoader().getResourceAsStream("config.yml")) {
                    if (in != null) {
                        ConfigUpdater.update(configFile, in);
                        logger.info("Updated config.yml with missing keys (if any).");
                    } else {
                        logger.warn("Could not find default config.yml in plugin JAR!");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to initialize config.yml", e);
        }
    }

    public static Path getConfigFile() {
        return configFile;
    }
}
