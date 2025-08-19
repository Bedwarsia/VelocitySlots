package party.komas19.velocityslotsbungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.event.EventHandler;

public class ProxyPingListener implements Listener {

    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();

        int realOnline = ProxyServer.getInstance().getOnlineCount();
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


        ServerPing.Players oldPlayers = ping.getPlayers();

        // Create new Players object with correct order: online, max, sample
        ServerPing.Players newPlayers = new ServerPing.Players(
                realOnline,               // online players
                maxSlots,                 // max players
                oldPlayers != null ? oldPlayers.getSample() : null // keep sample array
        );

        ServerPing newPing = new ServerPing(
                ping.getVersion(),
                newPlayers,
                ping.getDescription(),
                ping.getFavicon()
        );

        event.setResponse(newPing);
    }
}
