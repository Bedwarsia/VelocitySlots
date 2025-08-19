package party.komas19.velocityslotsvelocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.slf4j.Logger;

public class ProxyPingListener {

    private final ProxyServer proxy;
    private final Logger logger;

    public ProxyPingListener(ProxyServer proxy, Logger logger) {
        this.proxy = proxy;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getPing();

        int realOnline = proxy.getAllPlayers().size();
        int maxSlots;

        switch (Config.getMode()) {
            case "DYNAMIC":
                maxSlots = realOnline + Config.getOffset();
                break;
            case "STATIC":
                maxSlots = Config.getStaticSlots();
                break;
            default:
                maxSlots = realOnline;
                break;
        }

        ServerPing.Players newPlayers = new ServerPing.Players(
                realOnline,
                maxSlots,
                ping.getPlayers().map(ServerPing.Players::getSample).orElse(null)
        );

        ServerPing newPing = new ServerPing(
                ping.getVersion(),
                newPlayers,
                ping.getDescriptionComponent(),
                ping.getFavicon().orElse(null)
        );

        event.setPing(newPing);
    }
}
