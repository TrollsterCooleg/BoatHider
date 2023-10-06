package me.cooleg.boathider.nms.V1_19_3;

import me.cooleg.boathider.nms.INMS;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.Boat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftBoat;
import org.bukkit.entity.Entity;

public class NMSV1_19_R3 implements INMS {

    @Override
    public Entity spawnBoat(Location location) {
        ServerLevel level = ((CraftWorld) location.getWorld()).getHandle();
        CollisionlessBoat boat = new CollisionlessBoat(level, location.getX(), location.getY(), location.getZ());
        float yaw = Location.normalizeYaw(location.getYaw());
        boat.setYRot(yaw);
        boat.yRotO = yaw;
        boat.setYHeadRot(yaw);
        level.addFreshEntity(boat);
        boat.setVariant(Boat.Type.MANGROVE);
        return new CraftBoat((CraftServer) Bukkit.getServer(), boat);
    }

}
