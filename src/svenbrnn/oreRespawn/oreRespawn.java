package svenbrnn.oreRespawn;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * oreRespawn for Bukkit
 *
 * @author svenbrnn
 */
public class oreRespawn extends JavaPlugin {

    private final oreRespawnPlayerListener playerListener = new oreRespawnPlayerListener(this);
    private oreRespawnConfig configer;
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private oreRespawnBlockListener blockListener;
    private oreRespawnBlacklistWorker blacklist;
    private oreRespawnRespawner oreRespawn;

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events
        // Register our events

        configer = new oreRespawnConfig(this);
        blacklist = new oreRespawnBlacklistWorker(this);
        oreRespawn = new oreRespawnRespawner(configer.cfgMaxDistance);
        blockListener = new oreRespawnBlockListener(this, configer, blacklist, oreRespawn);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Lowest, this);

        oreRespawn.start();
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " disabled!");
    }

    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}
