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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BoatListeners implements Listener {

    private final JavaPlugin plugin;
    private boolean hiding = false;

    public BoatListeners(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    private void playerJoinedServer(PlayerJoinEvent event) {
        showAll(event.getPlayer());
        if (!hiding) {return;}
        hideOtherInWorld(event.getPlayer());
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
                player2.hideEntity(plugin, vehicle);
                player2.hideEntity(plugin, entered);
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
        hideOtherInWorld(player, null);
    }

    public void hideOtherInWorld(Player player, Vehicle boat) {
        if (!player.isInsideVehicle()) {return;}
        for (Entity entity : player.getWorld().getEntitiesByClass(Boat.class)) {
            if (entity.getPassengers().size() != 0 && !entity.getPassengers().contains(player) && entity != boat) {
                player.hideEntity(plugin, entity);
                for (Entity riding : entity.getPassengers()) {
                    player.hideEntity(plugin, riding);
                }
            }
        }
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
