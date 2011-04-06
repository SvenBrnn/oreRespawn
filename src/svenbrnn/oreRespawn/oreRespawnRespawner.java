/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

/**
 *
 * @author Sven
 */
public class oreRespawnRespawner extends Thread {

    boolean stoprequested;
    private Block BrokenBlock;
    private BlockBreakEvent event;
    private int stdMaxDistance;
    private int maxheight;

    public oreRespawnRespawner(Block BrokenBlock, BlockBreakEvent event, int stdMaxDistance, int maxheight) {
        super();
        stoprequested = false;
    }

    public synchronized void requestStop() {
        stoprequested = true;
    }

    public void run() {
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
                    //System.out.println("[oreRespawn] x:" + i + " y:" + k + " z:" + j + "");
                    if (wo.getBlockAt(new Location(wo, i, k, j)).getTypeId() == 0 && wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() != 0) {
                        BlockList.add(wo.getBlockAt(new Location(wo, i, k, j)));
                        //System.out.println("[oreRespawn] Block in Liste: x:" + i + " y:" + k + " z:" + j + " abgebaut");
                    }
                }
            }
        }

        if (BlockList.isEmpty()) {
            System.out.println("[oreRespawn] Keine Position gefunden!");
            return;
        }

        Random rnd = new Random();
        int tryed = 0;
        int nextInt = 0;
        do {
            nextInt = rnd.nextInt(BlockList.size());

            Block chBl = BlockList.get(nextInt);
            if (chBl.getLightLevel() < 4 && tryed < 20) {
                continue;
            } else if (tryed == 20) {
                break;
            }

            chBl.setTypeId(blockType);
            pl.sendMessage("Block Wurde Erstellt bei: X:" + BlockList.get(nextInt).getX() + " Y:" + BlockList.get(nextInt).getY() + " Z:" + BlockList.get(nextInt).getZ());

            break;
        } while (true);
    }
}
