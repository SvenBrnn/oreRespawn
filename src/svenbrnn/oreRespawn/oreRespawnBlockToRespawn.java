/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import org.bukkit.block.Block;

/**
 *
 * @author Sven
 */
public class oreRespawnBlockToRespawn {

    private Block block;
    private int type;

    oreRespawnBlockToRespawn(Block block, int type) {
        this.block = block;
        this.type = type;
    }

    public Block getBlock() {
        return this.block;
    }

    public int getType() {
        return this.type;
    }
}
