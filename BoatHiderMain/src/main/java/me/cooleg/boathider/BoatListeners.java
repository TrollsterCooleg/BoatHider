package me.cooleg.boathider;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
    private void playerEnterBoat(VehicleEnterEvent event) {
        if (!hiding) {return;}
        if (!(event.getVehicle() instanceof Boat)) {return;}
        if (!(event.getEntered() instanceof Player player)) {return;}

        for (Player player2 : Bukkit.getOnlinePlayers()) {
            if (player == player2) {continue;}
            if (player2.isInsideVehicle() && player2.getVehicle() != event.getVehicle()) {
                player2.hidePlayer(plugin, player);
                player2.hideEntity(plugin, event.getVehicle());
            }
        }

        hideOtherInWorld(player);
    }

    @EventHandler
    private void playerLeaveBoat(VehicleExitEvent event) {
        if (!hiding) {return;}
        if (!(event.getVehicle() instanceof Boat)) {return;}
        if (!(event.getExited() instanceof Player player)) {return;}
        showAll(player);
        for (Player player2 : Bukkit.getOnlinePlayers()) {
            player2.showPlayer(plugin, player);
            if (event.getVehicle().getPassengers().size() != 1) {continue;}
            player2.showEntity(plugin, event.getVehicle());
        }
    }

    @EventHandler
    private void vehicleDestroyEvent(VehicleDestroyEvent event) {
        if (!hiding) {return;}
        if (!(event.getVehicle() instanceof Boat)) {return;}
        for (Entity entity : event.getVehicle().getPassengers()) {
            if (!(entity instanceof Player target)) {continue;}
            showAll(target);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.showPlayer(plugin, target);
            }
        }
    }

    /*
    @EventHandler
    private void chunkLoadEvent(ChunkLoadEvent event) {
        if (!hiding) {return;}
        ArrayList<Boat> boats = new ArrayList<>();
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof Boat boat)) {continue;}
            boats.add(boat);
        }

        for (Player player : event.getWorld().getPlayers()) {
            if (player.isInsideVehicle()) {
                for (Boat boat : boats) {
                    if (boat.getPassengers().size() == 0) {continue;}
                    player.hideEntity(plugin, boat);
                    for (Entity entity : boat.getPassengers()) {
                        if (!(entity instanceof Player target)) {continue;}
                        player.hidePlayer(plugin, target);
                    }
                }
            } else {
                for (Boat boat : boats) {
                    player.showEntity(plugin, boat);
                    for (Entity entity : boat.getPassengers()) {
                        if (!(entity instanceof Player target)) {continue;}
                        player.showPlayer(plugin, target);
                    }
                }
            }

        }
    }

     */

    public void showAll(Player player) {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(Boat.class)) {
                player.showEntity(plugin, entity);
            }
        }
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player.showPlayer(plugin, player1);
        }
    }

    public void hideOtherInWorld(Player player) {
        if (!player.isInsideVehicle()) {return;}
        for (Entity entity : player.getWorld().getEntitiesByClass(Boat.class)) {
            if (entity.getPassengers().size() != 0 && !entity.getPassengers().contains(player)) {
                player.hideEntity(plugin, entity);
                for (Entity riding : entity.getPassengers()) {
                    if (!(riding instanceof Player ridingPlayer)) {continue;}
                    player.hidePlayer(plugin, ridingPlayer);
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
