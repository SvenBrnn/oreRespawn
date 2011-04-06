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
                System.out.println("[oreRespawn] Block auf x:" + BrokenBlock.getX() + " y:" + BrokenBlock.getY() + " z:" + BrokenBlock.getZ() + " abgebaut");

                int blockType = BrokenBlock.getTypeId();
                int x = BrokenBlock.getX();
                int y = BrokenBlock.getY();
                int z = BrokenBlock.getZ();
                Player pl = event.getPlayer();
                World wo = BrokenBlock.getWorld();

                List<Block> BlockList = new ArrayList<Block>();

                int maxDistance = stdMaxDistance - 1;

                maxDistance++;
                for (int i = x - maxDistance; i < x + maxDistance; i++) {
                    for (int j = z - maxDistance; j < z + maxDistance; j++) {
                        for (int k = 0; k < maxheight - 1; k++) {
                            if (wo.getBlockAt(new Location(wo, x, y, z)).getTypeId() == 0 && wo.getBlockAt(new Location(wo, x, y + 1, z)).getTypeId() != 0) {
                                BlockList.add(wo.getBlockAt(new Location(wo, x, y, z)));
                                System.out.println("[oreRespawn] Block in Liste: x:" + x + " y:" + y + " z:" + z + " abgebaut");
                                break;
                            }
                        }
                    }
                }

                if(BlockList.isEmpty())
                {
                    super.onBlockBreak(event);
                    System.out.println("[oreRespawn] Keine Position gefunden!");
                    return;
                }

                Random rnd = new Random();
                int tryed = 0;
                int nextInt = 0;
                do {
                    nextInt = rnd.nextInt(BlockList.size());

                    Block chBl = BlockList.get(nextInt);
                    if (chBl.getLightLevel() > 8 && tryed < 20) {
                        continue;
                    } else if (tryed == 20) {
                        break;
                    }

                    chBl.setTypeId(blockType);
                    break;
                } while (true);

                if (tryed != 20) {
                    pl.sendMessage("Block Wurde Erstellt bei: X:" + BlockList.get(nextInt).getX() + " Y:" + BlockList.get(nextInt).getY() + " Z:" + BlockList.get(nextInt).getZ());
                }
            }
        }
        super.onBlockBreak(event);
    }
    //put all Block related code here
}
