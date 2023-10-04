package me.cooleg.boathider;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.Boat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class BoatHider extends JavaPlugin {

    private final ArrayList<World> worlds = new ArrayList<>();
    private BoatListeners listeners;

    @Override
    public void onEnable() {
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
                                    ServerLevel level = ((CraftWorld) player.getWorld()).getHandle();
                                    NonCollidableBoat boat = new NonCollidableBoat(level, location.getX(), location.getY(), location.getZ());
                                    float yaw = Location.normalizeYaw(player.getEyeLocation().getYaw());
                                    boat.setYRot(yaw);
                                    boat.yRotO = yaw;
                                    boat.setYHeadRot(yaw);
                                    level.addFreshEntity(boat);
                                    boat.setVariant(Boat.Type.ACACIA);
                                    ((CraftPlayer) player).getHandle().startRiding(boat);
                                }))
                ).register();
    }

}
