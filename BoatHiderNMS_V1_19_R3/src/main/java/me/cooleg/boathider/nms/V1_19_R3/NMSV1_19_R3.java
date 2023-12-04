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

package me.cooleg.boathider.nms.V1_19_R3;

import me.cooleg.boathider.nms.INMS;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.Boat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftBoat;

public class NMSV1_19_R3 implements INMS {

    @Override
    public org.bukkit.entity.Boat spawnBoat(Location location) {
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

    @Override
    public boolean isCollisionless(org.bukkit.entity.Boat boat) {
        return ((CraftBoat) boat).getHandle() instanceof CollisionlessBoat;
    }

}
