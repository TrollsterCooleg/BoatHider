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

package me.cooleg.boathider;

import com.noxcrew.noxesium.api.protocol.rule.ServerRuleIndices;
import me.superneon4ik.noxesiumutils.NoxesiumUtils;
import me.superneon4ik.noxesiumutils.events.NoxesiumPlayerJoinEvent;
import me.superneon4ik.noxesiumutils.network.clientbound.ClientboundChangeServerRulesPacket;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class NoxesiumUtilsHook implements Listener {

    private final BoatListeners listeners;

    public NoxesiumUtilsHook(BoatListeners listeners) {
        this.listeners = listeners;
    }

    @EventHandler
    public void noxesiumLoginEvent(NoxesiumPlayerJoinEvent event) {
        if (event.getProtocolVersion() < 4) {return;}
        listeners.showAll(event.getPlayer());

        var rule = NoxesiumUtils.getManager().getServerRule(event.getPlayer(), ServerRuleIndices.DISABLE_BOAT_COLLISIONS);
        if (rule != null) {
            rule.setValue(true);
            new ClientboundChangeServerRulesPacket(List.of(rule)).send(event.getPlayer());
        }
    }

    public boolean canShowSafely(Player player) {
        Integer ver = NoxesiumUtils.getManager().getProtocolVersion(player);
        if (ver == null) {return false;}
        return ver >= 4;
    }

}
