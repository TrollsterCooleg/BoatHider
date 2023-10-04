package me.cooleg.boathider;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;

public class NonCollidableBoat extends Boat {
    public NonCollidableBoat(EntityType<? extends Boat> entitytypes, Level world) {
        super(entitytypes, world);
    }

    public NonCollidableBoat(Level world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }
}
