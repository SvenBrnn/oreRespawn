/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.orerespawn;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
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
    private oreRespawnConfig stdMaxDistance;
    private List<oreRespawnBlockToRespawn> brokenBlockList = new ArrayList<oreRespawnBlockToRespawn>();
    private oreRespawnDBAndBlacklistWorker blacklist;
    private boolean instandSpawn;

    public oreRespawnRespawner(oreRespawnConfig stdMaxDistance, oreRespawnDBAndBlacklistWorker blacklist) {
        super();
        stoprequested = false;
        this.stdMaxDistance = stdMaxDistance;
        this.blacklist = blacklist;
        this.setPriority(Thread.MIN_PRIORITY);
        instandSpawn = false;
    }

    public synchronized void requestStop() {
        stoprequested = true;
    }

    public synchronized void spawnNow() {
        instandSpawn = true;
    }

    public void run() {
        while (!stoprequested) {
            if (brokenBlockList.isEmpty()) {
                if (instandSpawn) {
                    instandSpawn = false;
                    brokenBlockList = blacklist.getBlocksFromSpawnListAndDelItAll();
                } else {
                    brokenBlockList = blacklist.getBlocksFromSpawnListAndDelIt();
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
                continue;
            }

            Block BrokenBlock = brokenBlockList.get(0).getBlock();
            //System.out.println("[oreRespawn] Block auf x:" + BrokenBlock.getX() + " y:" + BrokenBlock.getY() + " z:" + BrokenBlock.getZ() + " abgebaut");
            int blockType = brokenBlockList.get(0).getType();
            int x = BrokenBlock.getX();
            int y = BrokenBlock.getY();
            int z = BrokenBlock.getZ();
            World wo = BrokenBlock.getWorld();
            
            oreRespawnConfigWorld conf = null;
            for(int j = 0; j < this.stdMaxDistance.worldConfigs.size(); j++) {
                    if(this.stdMaxDistance.worldConfigs.get(j).worldName.equals(wo.getName()))
                        conf = this.stdMaxDistance.worldConfigs.get(j);
            }
            
            if(conf == null)
                continue;

            int maxheight = 127;
            int maxdis = 0;
            switch (blockType) {
                case 14:
                    maxheight = 35;
                    maxdis = conf.cfgMaxDistance_gold;
                    break;
                case 15:
                    maxheight = 67;
                    maxdis = conf.cfgMaxDistance_iron;
                    break;
                case 16:
                    maxdis = conf.cfgMaxDistance_coal;
                    break;
                case 21:
                    maxdis = conf.cfgMaxDistance_lapis;
                    maxheight = 32;
                    break;
                case 56:
                    maxdis = conf.cfgMaxDistance_diamond;
                    maxheight = 19;
                    break;
                case 73:
                    maxdis = conf.cfgMaxDistance_redstone;
                    maxheight = 19;
                    break;
            }

            List<Block> BlockList = new ArrayList<Block>();

            //Dont Respawn Blocks with maxdistance < 1
            if (maxdis < 0) {
                brokenBlockList.remove(0);
                continue;
            } else if (maxdis == 0) {
                wo.getBlockAt(new Location(wo, x, y, z)).setTypeId(blockType);
                brokenBlockList.remove(0);
                continue;
            }
            int maxDistance = maxdis;

            maxDistance++;
            for (int i = x - maxDistance; i < x + maxDistance; i++) {
                for (int j = z - maxDistance; j < z + maxDistance; j++) {
                    for (int k = 0; k < maxheight - 1; k++) {
                        //System.out.println("[oreRespawn] x:" + i + " y:" + k + " z:" + j + "");
                        if (wo.getBlockAt(new Location(wo, i, k, j)).getTypeId() == 0
                                && wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() != 0
                                && wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() != 18
                                && (wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 14
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 15
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 16
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 21
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 56
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 73
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 73
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 1
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 2
                                || wo.getBlockAt(new Location(wo, i, k + 1, j)).getTypeId() == 3)) {
                            if(!conf.regionRespawnMode || oreRespawn.worldEdit == null) {
                                BlockList.add(wo.getBlockAt(new Location(wo, i, k, j)));
                            } else if(conf.regionRespawnMode && oreRespawn.worldEdit != null) {
                                for(int l = 0; l < oreRespawn.regions.size(); l++){
                                    if(oreRespawn.regions.get(l).region.contains(new BlockWorldVector(new BukkitWorld(wo), i, j, k))){
                                        BlockList.add(wo.getBlockAt(new Location(wo, i, k, j)));
                                        break;
                                    }
                                }
                            }
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
                try {
                    chBl.getWorld().loadChunk(chBl.getChunk());
                } catch (Exception e) {
                }

                if (chBl.getLightLevel() < 4 && tryed < 20) {
                    continue;
                } else if (tryed == 20) {
                    brokenBlockList.remove(0);
                    break;
                }

                chBl.setTypeId(blockType);
                System.out.println("[oreRespawn] " + brokenBlockList.get(0).getType() + " Block auf x:" + chBl.getX() + " y:" + chBl.getY() + " z:" + chBl.getZ() + " gespawnt");
                brokenBlockList.remove(0);
                break;
            } while (true);
        }
    }
}
