package me.cooleg.boathider;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import me.cooleg.boathider.nms.INMS;
import me.cooleg.boathider.nms.V1_19_3.NMSV1_19_R3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public final class BoatHider extends JavaPlugin {

    private final ArrayList<World> worlds = new ArrayList<>();
    private INMS nms;
    private BoatListeners listeners;

    @Override
    public void onEnable() {
        nms = getNMS();
        saveDefaultConfig();
        registerCommands();
        List<String> worldStrings = getConfig().getStringList("worlds");
        for (String worldString : worldStrings) {
            World world = Bukkit.getWorld(worldString);
            if (world == null) {Bukkit.getLogger().severe("Couldn't find world named " + worldString);}
            worlds.add(world);
        }
        listeners = new BoatListeners(this, worlds);
        Bukkit.getPluginManager().registerEvents(listeners, this);
    }

    private INMS getNMS() {
        String versionString = Bukkit.getBukkitVersion().split("-")[0];
        INMS inms = switch (versionString) {
            case "1.19.4" -> new NMSV1_19_R3();
            default -> throw new IncompatibleVersionException(versionString);
        };
        return inms;
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            listeners.showAll(player);
        }
    }

    public void startRunnable() {
        listeners.setHiding(true);
    }

    public void stopRunnable() {
        listeners.setHiding(false);
    }

    public void registerCommands() {
        new CommandAPICommand("mcm.boathider")
                .withPermission(CommandPermission.OP)
                .withSubcommands(
                        new CommandAPICommand("hidingboats")
                                .withArguments(new BooleanArgument("hide"))
                                .executes((commandSender, commandArguments) -> {
                                    boolean hide = (Boolean) commandArguments.get("hide");
                                    if (hide) {startRunnable(); return;}
                                    stopRunnable();
                                }),
                        new CommandAPICommand("forceinboat")
                                .withArguments(new EntitySelectorArgument.OnePlayer("player"))
                                .executes(((commandSender, commandArguments) -> {
                                    Player player = (Player) commandArguments.get("player");
                                    Location location = player.getLocation();
                                    location.setYaw(player.getEyeLocation().getYaw());
                                    nms.spawnBoat(location).addPassenger(player);
                                })),
                        new CommandAPICommand("spawnboat")
                                .withArguments(new LocationArgument("location"))
                                .executes(((commandSender, commandArguments) -> {
                                    Location location = (Location) commandArguments.get("location");
                                    nms.spawnBoat(location);
                                }))
                ).register();
    }

}
