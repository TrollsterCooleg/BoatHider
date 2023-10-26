package me.cooleg.boathider.nms;

import org.bukkit.Location;
import org.bukkit.entity.Boat;

public interface INMS {

    Boat spawnBoat(Location location);

    boolean isCollisionless(Boat boat);

}
