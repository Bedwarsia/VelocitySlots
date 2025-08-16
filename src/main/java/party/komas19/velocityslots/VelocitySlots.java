package party.komas19.velocityslots;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;

@Plugin(
        id = "velocityslots",
        name = "VelocitySlots",
        version = BuildConstants.VERSION,
        description = "Fakes max slots in the server MOTD",
        authors = {"Bedwarsia (Komas19)"}
)
public class VelocitySlots {

    private final ProxyServer proxy;
    private final Logger logger;

    @Inject
    public VelocitySlots(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("VelocitySlots has been initialized!");

        // Hardcoded plugin data folder: plugins/velocityslots
        Path dataFolder = Paths.get("plugins", "velocityslots").toAbsolutePath();

        // Initialize config.yml (copies fresh one from JAR and merges defaults)
        Config.init(null, dataFolder, logger); // first argument null since we only need InputStream from classloader

        // Register ProxyPing listener
        proxy.getEventManager().register(this, new ProxyPingListener(proxy, logger));
    }
}
