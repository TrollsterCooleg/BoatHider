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

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BoatListeners implements Listener {

    private NoxesiumUtilsHook hook;
    private final JavaPlugin plugin;
    private boolean hiding = false;

    public BoatListeners(JavaPlugin plugin) {
        this.plugin = plugin;

        try {
            Class.forName("me.superneon4ik.noxesiumutils.NoxesiumUtils");

            hook = new NoxesiumUtilsHook(this);
            Bukkit.getPluginManager().registerEvents(hook, plugin);
        } catch (ClassNotFoundException e) {
            hook = null;
        }
    }

    @EventHandler
    private void worldChangedEvent(PlayerChangedWorldEvent event) {
        showAll(event.getPlayer());
        if (!hiding) {return;}
        hideOtherInWorld(event.getPlayer());
    }

    @EventHandler
    private void entityEnterBoat(VehicleEnterEvent event) {
        if (!hiding) {return;}
        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Boat)) {return;}
        Entity entered = event.getEntered();
        boolean isPlayer = event.getEntered() instanceof Player;
        Player player = isPlayer ? (Player) event.getEntered() : null;

        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (entered == player2) {continue;}
            if (player2.isInsideVehicle() && player2.getVehicle() != event.getVehicle()) {
                hideEntity(player2, vehicle);
                hideEntity(player2, entered);
            }
        }

        if (isPlayer) {hideOtherInWorld(player, vehicle);}
    }

    @EventHandler
    private void entityLeaveBoat(VehicleExitEvent event) {
        if (!hiding) {return;}
        Vehicle vehicle = event.getVehicle();
        if (!(vehicle instanceof Boat)) {return;}
        Entity exited = event.getExited();
        boolean isPlayer = event.getExited() instanceof Player;
        Player player = isPlayer ? (Player) event.getExited() : null;

        for (Player player2 : Bukkit.getOnlinePlayers()) {
            player2.showEntity(plugin, exited);
            if (event.getVehicle().getPassengers().size() != 1) {continue;}
            player2.showEntity(plugin, event.getVehicle());
        }

        if (isPlayer) {showAll(player);}
    }

    @EventHandler
    private void vehicleDestroyEvent(VehicleDestroyEvent event) {
        if (!hiding) {return;}
        if (!(event.getVehicle() instanceof Boat)) {return;}
        for (Entity entity : event.getVehicle().getPassengers()) {
            if (entity instanceof Player target) {showAll(target);}

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showEntity(plugin, entity);
            }
        }
    }

    public void showAll(Player player) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                player.showEntity(plugin, entity);
            }
        }
    }

    public void hideOtherInWorld(Player player) {
        hideOtherInWorld(player, null, 1L);
    }

    public void hideOtherInWorld(Player player, Vehicle boat) {
        hideOtherInWorld(player, boat, 1L);
    }

    public void hideOtherInWorld(Player player, long delay) {
        hideOtherInWorld(player, null, delay);
    }

    public void hideOtherInWorld(Player player, Vehicle boat, long delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isInsideVehicle()) {return;}
                for (Entity entity : player.getWorld().getEntitiesByClass(Boat.class)) {
                    if (entity.getPassengers().size() != 0 && !entity.getPassengers().contains(player) && entity != boat) {
                        hideEntity(player, entity);
                        for (Entity riding : entity.getPassengers()) {
                            hideEntity(player, riding);
                        }
                    }
                }
            }
        }.runTaskLater(plugin, delay);
    }

    public void hideEntity(Player target, Entity hidden) {
        if (hook != null && hook.canShowSafely(target)) {return;}
        target.hideEntity(plugin, hidden);
    }

    public void setHiding(boolean hiding) {
        this.hiding = hiding;
        if (hiding) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                hideOtherInWorld(player);
            }
        } else {
            for (Player player : Bukkit.getOnlinePlayers()) {
                showAll(player);
            }
        }
    }
}
