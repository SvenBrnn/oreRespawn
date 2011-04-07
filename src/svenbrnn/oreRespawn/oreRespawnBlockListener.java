package svenbrnn.oreRespawn;

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
    private int stdMaxDistance = 5;
    private oreRespawnBlacklistWorker blacklist;
    private oreRespawnRespawner oreRespawn;

    public oreRespawnBlockListener(final oreRespawn plugin, final oreRespawnConfig config, final oreRespawnBlacklistWorker blacklist, final oreRespawnRespawner oreRespawn) {
        this.plugin = plugin;
        this.config = config;
        stdMaxDistance = config.cfgMaxDistance;
        this.blacklist = blacklist;
        this.oreRespawn = oreRespawn;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block BrokenBlock = event.getBlock();
        if (config.enabledWorld.contains(BrokenBlock.getWorld())) {
            if(blacklist.blackListedBlock.contains(BrokenBlock))
            {
                blacklist.blackListedBlock.remove(BrokenBlock);
                blacklist.writeConfig();
                super.onBlockBreak(event);
                return;
            }

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
                oreRespawn.brokenBlockList.add(BrokenBlock);
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
        if(respawnNeeded)
        {
            blacklist.blackListedBlock.add(event.getBlock());
            blacklist.writeConfig();
        }        
        super.onBlockPlace(event);
    }
}
