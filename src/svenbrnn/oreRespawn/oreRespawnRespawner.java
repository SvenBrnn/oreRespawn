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

/**
 *
 * @author Sven
 */
public class oreRespawnRespawner extends Thread {

    boolean stoprequested;
    private int stdMaxDistance;
    public List<Block> brokenBlockList = new ArrayList<Block>();

    public oreRespawnRespawner(int stdMaxDistance) {
        super();
        stoprequested = false;
        this.stdMaxDistance = stdMaxDistance;
        this.setPriority(Thread.MIN_PRIORITY);
    }

    public synchronized void requestStop() {
        stoprequested = true;
    }

    public void run() {
        while (!stoprequested) {
            if(brokenBlockList.isEmpty())
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                   
                }
                continue;
            }

            Block BrokenBlock = brokenBlockList.get(0);
            //System.out.println("[oreRespawn] Block auf x:" + BrokenBlock.getX() + " y:" + BrokenBlock.getY() + " z:" + BrokenBlock.getZ() + " abgebaut");
            int blockType = BrokenBlock.getTypeId();
            int x = BrokenBlock.getX();
            int y = BrokenBlock.getY();
            int z = BrokenBlock.getZ();
            World wo = BrokenBlock.getWorld();

            int maxheight = 127;
            switch (BrokenBlock.getTypeId()) {
                case 14:
                    maxheight = 35;
                    break;
                case 15:
                    maxheight = 67;
                    break;
                case 16:
                    break;
                case 21:
                    maxheight = 32;
                    break;
                case 56:
                    maxheight = 19;
                    break;
                case 73:
                    maxheight = 19;
                    break;
            }

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
                brokenBlockList.remove(0);
                System.out.println("[oreRespawn] Block auf x:" + chBl.getX() + " y:" + chBl.getY() + " z:" + chBl.getZ() + " gespawnt");
                break;
            } while (true);
        }
    }
}
