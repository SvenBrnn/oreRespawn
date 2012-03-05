package svenbrnn.orerespawn;

import java.util.HashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.util.List;
import org.bukkit.plugin.Plugin;

/**
 * oreRespawn for Bukkit
 *
 * @author svenbrnn
 */
public class oreRespawn extends JavaPlugin {

    private oreRespawnConfig configer;
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    public oreRespawnCommandListener commandListener;
    private oreRespawnBlockListener blockListener;
    private oreRespawnDBAndBlacklistWorker blacklist;
    private oreRespawnRespawner oreRespawn;
    private PermissionHandler Permissions;
    public static List<oreRespawnRegion> regions;
    public static WorldEditPlugin worldEdit = null;

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        setupPermissions();
        if(!setupWorldEdit()) {
            pm.disablePlugin(this);
            System.out.println("[OreRespawn] WorldEdit not found! Plugin disabled!");
            return;
        }

        configer = new oreRespawnConfig(this);
        blacklist = new oreRespawnDBAndBlacklistWorker(this, configer);
        
        if(worldEdit != null)
            regions = blacklist.getRegions();
        oreRespawn = new oreRespawnRespawner(configer, blacklist);
        blockListener = new oreRespawnBlockListener(this, configer, blacklist, oreRespawn);
        commandListener = new oreRespawnCommandListener(this,oreRespawn, configer, Permissions, blacklist);


        //pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Lowest, this);
        //pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Lowest, this);
        pm.registerEvents(blockListener, this);

        oreRespawn.start();
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    private void setupPermissions() {
      Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

      if (this.Permissions == null) {
          if (test != null) {
              this.Permissions = ((Permissions)test).getHandler();
              System.out.println("[OreRespawn] Permission system detected!");
          } else {
              System.out.println("[OreRespawn] Permission system not detected, defaulting to OP");
          }
      }
  }
    
  private boolean setupWorldEdit() {
      Plugin test = this.getServer().getPluginManager().getPlugin("WorldEdit");

      if (worldEdit == null) {
          if (test != null) {
              worldEdit = (WorldEditPlugin)test;
              System.out.println("[OreRespawn] WorldEdit detected!");
              return true;
          } 
      }
      return false;
  }

    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " disabled!");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandListener.onCommand(sender, command, label, args);
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
