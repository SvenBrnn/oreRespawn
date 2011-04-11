/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Sven
 */
public class oreRespawnBlacklistWorker {

    private JavaPlugin plugin;
    private oreRespawnDatabase db;

    oreRespawnBlacklistWorker(JavaPlugin plugin, oreRespawnConfig config) {
        this.plugin = plugin;
        db = new oreRespawnDatabase(plugin.getServer(), config);
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

    public List<oreRespawnBlockToRespawn> getBlocksFromSpawnListAndDelIt()
    {
        return db.getBlocksFromSpawnListAndDelIt();
    }

    public List<oreRespawnBlockToRespawn> getBlocksFromSpawnListAndDelItAll()
    {
        return db.getBlocksFromSpawnListAndDelItAll();
    }

    public void addBlocksToSpawnList(Block blk, int typ, String dateTime) {
        db.addBlocksToSpawnList(blk.getX(), blk.getY(), blk.getZ(), typ, blk.getWorld().getName(), dateTime);
    }
}
