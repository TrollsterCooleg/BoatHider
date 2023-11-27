package me.cooleg.boathider;

import me.superneon4ik.noxesiumutils.NoxesiumUtils;
import me.superneon4ik.noxesiumutils.events.NoxesiumPlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NoxesiumUtilsHook implements Listener {

    private final BoatListeners listeners;

    public NoxesiumUtilsHook(BoatListeners listeners) {
        this.listeners = listeners;
    }

    @EventHandler
    public void noxesiumLoginEvent(NoxesiumPlayerJoinEvent event) {
        if (event.getProtocolVersion() < 4) {return;}
        listeners.showAll(event.getPlayer());
    }

    public boolean canShowSafely(Player player) {
        Integer ver = NoxesiumUtils.getManager().getProtocolVersion(player);
        if (ver == null) {return false;}
        return ver >= 4;
    }

}
