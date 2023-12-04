/**
 *     BoatHider - Minecraft plugin for collision-less ice boat racing
 *     Copyright (C) 2023 Cooleg
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.cooleg.boathider;

import me.cooleg.boathider.nms.INMS;
import org.bukkit.Chunk;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleCreateEvent;
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
    public void boatSpawn(VehicleCreateEvent event) {
        if (!(event.getVehicle() instanceof Boat boat) || nms.isCollisionless(boat)) {return;}
        new BukkitRunnable() {
            @Override
            public void run() {
                if (boat.isDead()) {return;}
                replaceBoat(boat);
            }
        }.runTaskLater(plugin, 1L);
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
