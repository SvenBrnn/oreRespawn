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

    public oreRespawnBlockListener(final oreRespawn plugin, final oreRespawnConfig config, final oreRespawnBlacklistWorker blacklist) {
        this.plugin = plugin;
        this.config = config;
        stdMaxDistance = config.cfgMaxDistance;
        this.blacklist = blacklist;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block BrokenBlock = event.getBlock();
        if (config.enabledWorld.contains(BrokenBlock.getWorld())) {
            if(blacklist.blackListedBlock.contains(BrokenBlock))
            {
                blacklist.blackListedBlock.remove(BrokenBlock);
                super.onBlockBreak(event);
                return;
            }

            int maxheight = 127;
            boolean respawnNeeded = false;
            switch (BrokenBlock.getTypeId()) {
                case 14:
                    maxheight = 35;
                    respawnNeeded = true;
                    break;
                case 15:
                    maxheight = 67;
                    respawnNeeded = true;
                    break;
                case 16:
                    respawnNeeded = true;
                    break;
                case 21:
                    maxheight = 32;
                    respawnNeeded = true;
                    break;
                case 56:
                    maxheight = 19;
                    respawnNeeded = true;
                    break;
                case 73:
                    maxheight = 19;
                    respawnNeeded = true;
                    break;
                default:
                    respawnNeeded = false;
            }

            if (respawnNeeded) {
                oreRespawnRespawner sp = new oreRespawnRespawner(BrokenBlock, event, stdMaxDistance, maxheight);
                sp.run();
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
        }        
        super.onBlockPlace(event);
    }
}
