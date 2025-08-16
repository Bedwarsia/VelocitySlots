package party.komas19.velocitySlots;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.slf4j.Logger;

import java.nio.file.Path;

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
    private final Config config;

    @Inject
    public VelocitySlots(ProxyServer proxy, Logger logger, @com.velocitypowered.api.plugin.annotation.DataDirectory Path folder) throws Exception {
        this.proxy = proxy;
        this.logger = logger;
        this.config = new Config(folder.toFile());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("VelocitySlots has been initialized!");
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getPing();

        int realOnline = proxy.getAllPlayers().size();
        int maxSlots;

        switch (config.getMode().toUpperCase()) {
            case "FAKE":
                maxSlots = realOnline + config.getOffset();
                break;
            case "STATIC":
                maxSlots = config.getStaticSlots();
                break;
            default:
                maxSlots = realOnline;
                break;
        }

        // Construct a new Players object directly
        ServerPing.Players newPlayers = new ServerPing.Players(realOnline, maxSlots, ping.getPlayers().map(ServerPing.Players::getSample).orElse(null));

        // Rebuild the ServerPing with all original fields except the updated players
        ServerPing newPing = new ServerPing(
                ping.getVersion(),
                newPlayers,
                ping.getDescriptionComponent(),
                ping.getFavicon().orElse(null)
        );


        event.setPing(newPing);
    }
}
