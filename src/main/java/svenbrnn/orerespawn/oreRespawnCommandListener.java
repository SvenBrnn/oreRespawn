/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.orerespawn;

import com.nijiko.permissions.PermissionHandler;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.RegionSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.commands.SelectionCommands;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
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
        if (sender instanceof Player) {
            boolean ret = false;
            String cmd = label;
            Player pl = (Player) sender;
            if (Permissions != null || pl.isOp()) {
                if (cmd.equals("ores")) {
                    if (args.length == 0) {
                        ret = defaultParam(pl);
                    } else if (args[0].equals("spawnnow")) {
                        ret = oreRespawnParam(pl);
                    } else if (args[0].equals("world")) {
                        ret = worldParam(pl, args);
                    } else if (args[0].equals("maxdistance")) {
                        ret = maxdistanceParam(pl, args);
                    } else if (args[0].equals("region")) {
                        ret = region(sender, args);
                    } else if (args[0].equals("log")) {
                        ret = logParam(pl, args);
                    } else {
                        ret = defaultParam(pl);
                    }
                    if (!ret) {
                        pl.sendMessage("[oreRespawn] Permission denied!");
                    }
                }
            }
            return ret;
        }
        return false;
    }

    private boolean region(CommandSender sender, String[] args) {
        if (sender.getClass() == ConsoleCommandSender.class) {
            System.out.println("[oreRespawn] Command not allowed from Console!");
            return false;
        }
        boolean ret = false;
        if (args.length > 1) {
            if (args[1].equals("list")) {
                if ((Permissions != null && Permissions.has((Player) sender, "orerespawn.region.list")) || sender.isOp()) {
                    ((Player) sender).sendMessage("[oreRespawn] Region List:");
                    for (oreRespawnRegion reg : oreRespawn.regions) {
                        ((Player) sender).sendMessage(ChatColor.GREEN + "- " + reg.name);
                    }
                    ret = true;
                }
            } else if (args[1].equals("create")) {
                if ((Permissions != null && Permissions.has((Player) sender, "orerespawn.region.create")) || sender.isOp()) {
                    if (args.length == 3) {
                        if (oreRespawn.worldEdit != null) {
                            Selection selection = oreRespawn.worldEdit.getSelection((Player) sender);
                            if (selection != null && selection.getArea() > 0) {
                                blacklist.addRegion(
                                        selection.getMinimumPoint().getBlockX(),
                                        selection.getMinimumPoint().getBlockY(),
                                        selection.getMinimumPoint().getBlockZ(),
                                        selection.getMaximumPoint().getBlockX(),
                                        selection.getMaximumPoint().getBlockY(),
                                        selection.getMaximumPoint().getBlockZ(),
                                        args[2],
                                        selection.getMinimumPoint().getWorld().getName());

                                oreRespawn.regions = blacklist.getRegions();
                                ((Player) sender).sendMessage("[oreRespawn] Region Added!");
                            } else {
                                ((Player) sender).sendMessage("[oreRespawn] No Area Selected!");
                            }
                        } else {
                            ((Player) sender).sendMessage("[oreRespawn] WorldEdit is Missing!");
                        }
                    } else {
                        ((Player) sender).sendMessage("[oreRespawn] Wrong Syntax!");
                        ((Player) sender).sendMessage("[oreRespawn] Syntax is /ores create <regionsname>");
                    }
                    ret = true;
                }
            } else if (args[1].equals("delete")) {
                if ((Permissions != null && Permissions.has((Player) sender, "orerespawn.region.delete")) || sender.isOp()) {
                    if (args.length == 3) {
                        boolean exist = false;
                        for (int i = 0; i < oreRespawn.regions.size(); i++) {
                            if (oreRespawn.regions.get(i).name.equals(args[2])) {
                                exist = true;
                                break;
                            }
                        }
                        if (exist) {
                            blacklist.deleteRegion(args[2]);
                            oreRespawn.regions = blacklist.getRegions();
                            ((Player) sender).sendMessage("[oreRespawn] Region " + args[2] + " Deleted!");
                        } else {
                            ((Player) sender).sendMessage("[oreRespawn] Region " + args[2] + " not found!");
                        }
                    } else {
                        ((Player) sender).sendMessage("[oreRespawn] Wrong Syntax!");
                        ((Player) sender).sendMessage("[oreRespawn] Syntax is /ores delete <regionsname>");
                    }
                    ret = true;
                }
            }
        } else {
            ((Player) sender).sendMessage("[oreRespawn] Require Arguments!");
            ((Player) sender).sendMessage(ChatColor.GREEN + "[oreRespawn] /ores region list");
            ((Player) sender).sendMessage(ChatColor.GREEN + "[oreRespawn] /ores region create <regionsname>");
            ((Player) sender).sendMessage(ChatColor.GREEN + "[oreRespawn] /ores region delete <regionsname>");
            ret = true;
        }

        return ret;
    }

    private boolean maxdistanceParam(Player pl, String[] args) {
        boolean ret = false;
        if ((Permissions != null && Permissions.has(pl, "orerespawn.maxdistance")) || pl.isOp()) {
            if (args.length == 4) {
                boolean maxIsInt = true;
                int maxRadius = 50;
                String world = "";
                boolean worldExists = true;
                String ore = "";
                boolean isOre = true;
                try {
                    maxRadius = new Integer(args[3]);
                } catch (Exception e) {
                    maxIsInt = false;
                }
                try {
                    world = args[1];
                    if (plugin.getServer().getWorld(world) == null) {
                        worldExists = false;
                    }
                } catch (Exception e) {
                    worldExists = false;
                }
                try {
                    ore = args[2];
                    if (!ore.equals("14") && !ore.equals("15") && !ore.equals("16") && !ore.equals("21") && !ore.equals("56") && !ore.equals("73")
                            && !ore.equals("gold") && !ore.equals("iron") && !ore.equals("coal") && !ore.equals("lapis") && !ore.equals("diamond") && !ore.equals("redstone")) {
                        isOre = false;
                    }
                    if (isOre && (ore.equals("14") || ore.equals("15") || ore.equals("16") || ore.equals("21") || ore.equals("56") || ore.equals("73"))) {
                        if (ore.equals("14")) {
                            ore = "gold";
                        }
                        if (ore.equals("15")) {
                            ore = "iron";
                        }
                        if (ore.equals("16")) {
                            ore = "coal";
                        }
                        if (ore.equals("21")) {
                            ore = "lapis";
                        }
                        if (ore.equals("56")) {
                            ore = "diamond";
                        }
                        if (ore.equals("73")) {
                            ore = "redstone";
                        }
                    }
                } catch (Exception e) {
                    isOre = false;
                }

                if (maxIsInt && worldExists && isOre) {
                    config.changeMaxRadius(maxRadius, world, ore);
                    pl.sendMessage("[oreRespawn] New maxdistance set!");
                } else if (!worldExists) {
                    pl.sendMessage("[oreRespawn] " + world + " is not an existing World!");
                } else if (!isOre) {
                    pl.sendMessage("[oreRespawn] " + ore + " is not a ore.");
                    pl.sendMessage("[oreRespawn] Try: gold, iron, coal, lapis, diamond, redstone or its ID's");
                } else {
                    pl.sendMessage("[oreRespawn] Syntax is: /ores maxdistance <world> <ore> <value>");
                }
            } else {
                pl.sendMessage("[oreRespawn] Syntax is: /ores maxdistance <world> <ore> <value>");
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
            pl.sendMessage(ChatColor.GREEN + "spawnnow: Spawns all Ore thats not spawned yet");
            pl.sendMessage(ChatColor.GREEN + "world: Enable or disable Worlds in config");
            pl.sendMessage(ChatColor.GREEN + "maxdistance: Set the max distance for Ore Respawning in config");
            pl.sendMessage(ChatColor.GREEN + "log: Gets the Ore Logging");
            pl.sendMessage(ChatColor.GREEN + "region: Region Commands");
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
                    if (args[2].equals("enable")) {
                        config.changeWorldEnable(wo.getName(), true);
                    } else if (args[2].equals("disable")) {
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
        if (args.length == 1) {
            if ((Permissions != null && (Permissions.has(pl, "orerespawn.log.show") || Permissions.has(pl, "orerespawn.log.clear"))) || pl.isOp()) {
                pl.sendMessage("[oreRespawn] The parameters for /ore log are show or clear");
                ret = true;
            }
        } else if (args[1].equals("show")) {
            if ((Permissions != null && Permissions.has(pl, "orerespawn.log.show")) || pl.isOp()) {
                pl.sendMessage("[oreRespawn] Num blocks mined per ore:");
                int actOreNum = 0;
                actOreNum = blacklist.getNumOreMined(14);
                pl.sendMessage("Gold: " + actOreNum);
                actOreNum = blacklist.getNumOreMined(15);
                pl.sendMessage("Iron: " + actOreNum);
                actOreNum = blacklist.getNumOreMined(16);
                pl.sendMessage("Coal: " + actOreNum);
                actOreNum = blacklist.getNumOreMined(21);
                pl.sendMessage("Lapis Lazuli: " + actOreNum);
                actOreNum = blacklist.getNumOreMined(56);
                pl.sendMessage("Diamond: " + actOreNum);
                actOreNum = blacklist.getNumOreMined(73);
                pl.sendMessage("Redstone: " + actOreNum);
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
