package party.komas19.velocityslotsbungee;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class VelocitySlots extends Plugin {

    private Logger logger;

    @Override
    public void onEnable() {
        this.logger = getLogger();
        logger.info("VelocitySlots (BungeeCord) has been enabled!");

        // Hardcoded plugin data folder: plugins/velocityslots
        Path dataFolder = Paths.get("plugins", "velocityslots").toAbsolutePath();

        // Load default config.yml from resources
        try (InputStream defaultConfigStream = getResourceAsStream("config.yml")) {
            if (defaultConfigStream == null) {
                logger.severe("Default config.yml missing from plugin JAR!");
            } else {
                Config.init(this.getClass(), dataFolder, logger);
            }
        } catch (IOException e) {
            logger.severe("Failed to initialize config.yml");
            e.printStackTrace();
            logger.severe("Try to reset the config.yml by deleting it.");
        }

        // Register ping listener
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxyPingListener());
    }

    @Override
    public void onDisable() {
        logger.info("VelocitySlots (BungeeCord) has been disabled!");
    }
}
