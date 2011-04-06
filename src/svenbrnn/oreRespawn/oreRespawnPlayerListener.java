package svenbrnn.oreRespawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * @author svenbrnn
 */
public class oreRespawnPlayerListener extends PlayerListener {
    private final oreRespawn plugin;

    public oreRespawnPlayerListener(oreRespawn instance) {
        plugin = instance;
    }

    //Insert Player related code here
}

