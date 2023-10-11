package me.cooleg.boathider;

import me.cooleg.boathider.nms.INMS;
import org.bukkit.Chunk;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

public class PersistenceListeners implements Listener {

    private final INMS nms;
    private final JavaPlugin plugin;

    public PersistenceListeners(INMS nms, JavaPlugin plugin) {
        this.nms = nms;
        this.plugin = plugin;
    }

    @EventHandler
    public void worldLoadEvent(WorldLoadEvent event) {
        for (Chunk chunk : event.getWorld().getLoadedChunks()) {
            for (Boat boat : Arrays.stream(chunk.getEntities())
                    .filter(entity -> entity.getType() == EntityType.BOAT)
                    .map((entity -> (Boat) entity))
                    .toList()) {
                replaceBoat(boat);
            }
        }
    }

    @EventHandler
    public void chunkLoadEvent(ChunkLoadEvent event) {
        for (Boat boat : Arrays.stream(event.getChunk().getEntities())
                .filter(entity -> entity.getType() == EntityType.BOAT)
                .map((entity -> (Boat) entity))
                .toList()) {
            replaceBoat(boat);
        }
    }

    @EventHandler
    public void entitySpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof Boat boat)) {return;}
        replaceBoat(boat);
    }

    @EventHandler
    public void vehicleSpawn(PlayerJoinEvent event) {
        if (!event.getPlayer().isInsideVehicle()) {return;}
        if (!(event.getPlayer().getVehicle() instanceof Boat boat)) {return;}
        new BukkitRunnable() {
            @Override
            public void run() {
                if (boat.isDead()) {return;}
                replaceBoat(boat);
            }
        }.runTaskLater(plugin, 1L);
    }

    @EventHandler
    public void boatPlace(EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Boat boat)) {return;}
        replaceBoat(boat);
    }

    private void replaceBoat(Boat boat) {
        Boat newBoat = nms.spawnBoat(boat.getLocation());
        newBoat.setBoatType(boat.getBoatType());

        for (Entity passenger : boat.getPassengers()) {
            newBoat.addPassenger(passenger);
        }

        boat.remove();
    }

}
