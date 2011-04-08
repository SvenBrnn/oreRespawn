/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Sven
 */
public class oreRespawnBlacklistWorker {

    private JavaPlugin plugin;
    private oreRespawnDatabase db;

    oreRespawnBlacklistWorker(JavaPlugin plugin) {
        this.plugin = plugin;
        db = new oreRespawnDatabase();
    }

    public void addBlocksToBlacklist(Block blackListedBlock) {
        db.addBlockToBlacklist(blackListedBlock.getX(), blackListedBlock.getY(), blackListedBlock.getZ(), blackListedBlock.getWorld().getName());
    }

    public int isBlockInBlacklist(Block bl) {
        return db.isBlockInBacklist(bl.getX(), bl.getY(), bl.getZ(), bl.getWorld().getName());
    }

    public void removeBlockFromBlacklist(int id) {
        db.deleteBlockFromBlacklist(id);
    }
}
