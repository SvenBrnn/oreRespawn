/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import com.nijiko.permissions.PermissionHandler;
import org.bukkit.World;
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
    private oreRespawnDBAndBlacklistWorker blacklist;

    oreRespawnCommandListener(Plugin plugin, oreRespawnRespawner oreRespawn, oreRespawnConfig configer, PermissionHandler Permissions, oreRespawnDBAndBlacklistWorker blacklist) {
        this.plugin = plugin;
        this.respawn = oreRespawn;
        this.config = configer;
        this.Permissions = Permissions;
        this.blacklist = blacklist;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean ret = false;
        String cmd = command.getName().toLowerCase();
        Player pl = (Player) sender;
        if (Permissions != null || pl.isOp()) {
            if (cmd.equals("ores")) {
                if (args[0].equals("spawnnow")) {
                    ret = oreRespawnParam(pl);
                } else if (args[0].equals("world")) {
                    ret = worldParam(pl, args);
                } else if (args[0].equals("maxdistance")) {
                    ret = maxdistanceParam(pl, args);
                } else if (args[0].equals("log")) {
                    ret = logParam(pl, args);
                } else {
                    ret = defaultParam(pl);
                }
            }
            if (!ret) {
                pl.sendMessage("[oreRespawn] Permission denied!");
            }
        }
        return ret;
    }

    private boolean maxdistanceParam(Player pl, String[] args) {
        boolean ret = false;
        if ((Permissions != null && Permissions.has(pl, "orerespawn.maxdistance")) || pl.isOp()) {
            if (args.length == 2) {
                boolean maxIsInt = true;
                int maxRadius = 50;
                try {
                    maxRadius = new Integer(args[1]);
                } catch (Exception e) {
                    maxIsInt = false;
                }
                if (maxIsInt) {
                    config.changeMaxRadius(maxRadius);
                    pl.sendMessage("[oreRespawn] New maxdistance set!");
                } else {
                    pl.sendMessage("[oreRespawn] Syntax is: /ores maxdistance <value>");
                }
            }
            ret = true;
        }
        return ret;
    }

    private boolean oreRespawnParam(Player pl) {
        boolean ret = false;
        if ((Permissions != null && Permissions.has(pl, "orerespawn.respawn")) || pl.isOp()) {
            respawn.spawnNow();
            pl.sendMessage("[oreRespawn] Respawning all Ores!");
            ret = true;
        }
        return ret;
    }

    private boolean defaultParam(Player pl) {
        boolean ret = false;
        if ((Permissions != null && Permissions.has(pl, "orerespawn")) || pl.isOp()) {
            pl.sendMessage("[oreRespawn] Parameter List:");
            pl.sendMessage("spawnnow: Spawns all Ore thats not spawned yet");
            pl.sendMessage("world: Enable or disable Worlds in config");
            pl.sendMessage("maxdistance: Set the max distance for Ore Respawning in config");
            ret = true;
        }
        return ret;
    }

    private boolean worldParam(Player pl, String[] args) {
        boolean ret = false;
        if ((Permissions != null && Permissions.has(pl, "orerespawn.world")) || pl.isOp()) {
            if (args.length == 3) {
                World wo = plugin.getServer().getWorld(args[1]);
                if (wo != null) {
                    if (args[0].equals("enable")) {
                        config.changeWorldEnable(wo.getName(), true);
                    } else if (args[0].equals("disable")) {
                        config.changeWorldEnable(wo.getName(), false);
                    } else {
                        pl.sendMessage("[oreRespawn] Syntax is: /ores world <worldname> <enable/disable>");
                    }
                } else {
                    pl.sendMessage("[oreRespawn] World " + args[1] + " not found!");
                }
            } else {
                pl.sendMessage("[oreRespawn] Syntax is: /ores world <worldname> <enable/disable>");
            }
            ret = true;
        }
        return ret;
    }

    private boolean logParam(Player pl, String[] args) {
        boolean ret = false;
        if (args[1].equals("show")) {
            if ((Permissions != null && Permissions.has(pl, "orerespawn.log.show")) || pl.isOp()) {
                pl.sendMessage("[oreRespawn] Num blocks mined per ore:");
                int actOreNum = 0;
                actOreNum = blacklist.getNumOreMined(14);
                pl.sendMessage("Gold:" + actOreNum);
                actOreNum = blacklist.getNumOreMined(15);
                pl.sendMessage("Iron:" + actOreNum);
                actOreNum = blacklist.getNumOreMined(16);
                pl.sendMessage("Coal" + actOreNum);
                actOreNum = blacklist.getNumOreMined(21);
                pl.sendMessage("Lapis Lazuli:" + actOreNum);
                actOreNum = blacklist.getNumOreMined(56);
                pl.sendMessage("Diamond:" + actOreNum);
                actOreNum = blacklist.getNumOreMined(73);
                pl.sendMessage("Redstone:" + actOreNum);
                ret = true;
            }
        } else if (args[1].equals("clear")) {
            if ((Permissions != null && Permissions.has(pl, "orerespawn.log.clear")) || pl.isOp()) {
                blacklist.clearAllOreMined();
                ret = true;
            }
        } else {
            if ((Permissions != null && (Permissions.has(pl, "orerespawn.log.show") || Permissions.has(pl, "orerespawn.log.clear"))) || pl.isOp()) {
                pl.sendMessage("[oreRespawn] The parameters for /ore log are show or clear");
                ret = true;
            }
        }
        return ret;
    }
}
