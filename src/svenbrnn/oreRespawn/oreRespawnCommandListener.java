/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Sven
 */
public class oreRespawnCommandListener {

    private Plugin plugin;
    private oreRespawnRespawner respawn;
    private oreRespawnConfig config;
    private PermissionHandler Permissions;

    oreRespawnCommandListener(Plugin plugin, oreRespawnRespawner oreRespawn, oreRespawnConfig configer, PermissionHandler Permissions) {
        this.plugin = plugin;
        this.respawn = oreRespawn;
        this.config = configer;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean ret = false;
        String cmd = command.getName().toLowerCase();
        Player pl = (Player) sender;
        if (cmd.equals("ores")) {
            if (args[0].endsWith("spawnnow")) {
                if (Permissions.has(pl, "orerespawn.respawn")) {
                    respawn.spawnNow();
                    pl.sendMessage("[oreRespawn] Respawning all Ores!");
                    ret = true;
                }
            } else {
                if (Permissions.has(pl, "orerespawn")) {
                    pl.sendMessage("[oreRespawn] Parameter List:");
                    pl.sendMessage("spawnnow: Spawns all Ore thats not spawned yet");
                    ret = true;
                }
            }
        }
        return ret;
    }
}
