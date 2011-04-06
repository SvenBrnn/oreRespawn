package svenbrnn.oreRespawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

/**
 * oreRespawn block listener
 * @author svenbrnn
 */
public class oreRespawnBlockListener extends BlockListener {

    private final oreRespawn plugin;
    private final oreRespawnConfig config;
    private int stdMaxDistance = 5;

    public oreRespawnBlockListener(final oreRespawn plugin, final oreRespawnConfig config) {
        this.plugin = plugin;
        this.config = config;
        stdMaxDistance = config.cfgMaxDistance;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Block BrokenBlock = event.getBlock();
        if (config.enabledWorld.contains(BrokenBlock.getWorld())) {
            int maxheight = 127;
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
                oreRespawnRespawner sp = new oreRespawnRespawner(BrokenBlock, event, stdMaxDistance, maxheight);
                sp.setPriority(sp.MIN_PRIORITY);
                sp.run();
            }
        }
        super.onBlockBreak(event);
    }
    //put all Block related code here
}
