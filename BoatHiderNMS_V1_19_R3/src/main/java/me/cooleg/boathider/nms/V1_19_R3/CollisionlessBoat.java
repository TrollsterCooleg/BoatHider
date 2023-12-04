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

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

public class CollisionlessBoat extends Boat {

    public CollisionlessBoat(EntityType<? extends Boat> entitytypes, Level world) {
        super(entitytypes, world);
    }

    public CollisionlessBoat(Level world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

}
