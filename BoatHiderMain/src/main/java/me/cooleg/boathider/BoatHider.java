package me.cooleg.boathider;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.LocationArgument;
import me.cooleg.boathider.nms.INMS;
import me.cooleg.boathider.nms.V1_19_R3.NMSV1_19_R3;
import me.cooleg.boathider.nms.V1_20_R2.NMSV1_20_R2;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoatHider extends JavaPlugin {

    private INMS nms;
    private BoatListeners listeners;

    @Override
    public void onEnable() {
        nms = getNMS();
        saveDefaultConfig();
        registerCommands();

        listeners = new BoatListeners(this);
        Bukkit.getPluginManager().registerEvents(listeners, this);
        Bukkit.getPluginManager().registerEvents(new PersistenceListeners(nms, this), this);
    }

    private INMS getNMS() {
        String versionString = Bukkit.getBukkitVersion().split("-")[0];
        return switch (versionString) {
            case "1.19.4" -> new NMSV1_19_R3();
            case "1.20.2" -> new NMSV1_20_R2();
            default -> throw new IncompatibleVersionException(versionString);
        };
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
