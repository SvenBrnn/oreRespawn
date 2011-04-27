package svenbrnn.oreRespawn;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * oreRespawn block listener
 * @author svenbrnn
 */
public class oreRespawnBlockListener extends BlockListener {

    private final oreRespawn plugin;
    private final oreRespawnConfig config;
    private oreRespawnDBAndBlacklistWorker blacklist;
    private oreRespawnRespawner oreRespawn;

    public oreRespawnBlockListener(final oreRespawn plugin, final oreRespawnConfig config, final oreRespawnDBAndBlacklistWorker blacklist, final oreRespawnRespawner oreRespawn) {
        this.plugin = plugin;
        this.config = config;
        this.blacklist = blacklist;
        this.oreRespawn = oreRespawn;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block BrokenBlock = event.getBlock();
        if (config.enabledWorld.contains(BrokenBlock.getWorld())) {
            boolean respawnNeeded = false;
            switch (BrokenBlock.getTypeId()) {
                case 14:
                    respawnNeeded = true;
                    break;
                case 15:
                    respawnNeeded = true;
                    break;
                case 16:
                    respawnNeeded = true;
                    break;
                case 21:
                    respawnNeeded = true;
                    break;
                case 56:
                    respawnNeeded = true;
                    break;
                case 73:
                    respawnNeeded = true;
                    break;
                default:
                    respawnNeeded = false;
            }

            if (respawnNeeded) {
                int num = blacklist.isBlockInBlacklist(BrokenBlock);
                if (num != -1) {
                    blacklist.removeBlockFromBlacklist(num);
                    super.onBlockBreak(event);
                    return;
                }
                //oreRespawn.brokenBlockList.add(BrokenBlock);
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String uhrzeit = sdf.format(dt);

                blacklist.addBlocksToSpawnList(BrokenBlock, BrokenBlock.getTypeId(), uhrzeit);
            }
        }
        super.onBlockBreak(event);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        boolean respawnNeeded = false;
        switch (event.getBlock().getTypeId()) {
            case 14:
                respawnNeeded = true;
                break;
            case 15:
                respawnNeeded = true;
                break;
            case 16:
                respawnNeeded = true;
                break;
            case 21:
                respawnNeeded = true;
                break;
            case 56:
                respawnNeeded = true;
                break;
            case 73:
                respawnNeeded = true;
                break;
            default:
                respawnNeeded = false;
        }
        if (respawnNeeded) {
            blacklist.addBlocksToBlacklist(event.getBlock());
        }
        super.onBlockPlace(event);
    }
}
