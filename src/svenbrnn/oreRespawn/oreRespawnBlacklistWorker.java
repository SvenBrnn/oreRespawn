/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author Sven
 */
public class oreRespawnBlacklistWorker {

    private Configuration config;
    private File cfgFile;
    private String file;
    private JavaPlugin plugin;
    public List<Block> blackListedBlock;

    oreRespawnBlacklistWorker(JavaPlugin plugin) {
        this.plugin = plugin;
        file = "plugins/oreRespawn/blacklist.yml";
        cfgFile = new File(file);
        this.config = new Configuration(cfgFile);
        if (!cfgFile.exists()) {
            this.createConfig();
        }
        blackListedBlock = new ArrayList<Block>();
        this.configLoader();
    }

    private void configLoader() {
    }

    private void createConfig() {
        config.setProperty("blacklist", "");
        config.save();
    }

    public void writeConfig() {
        for (int i = 0; i < blackListedBlock.size(); i++) {
            ConfigurationNode node = Configuration.getEmptyNode();
            cfgFile = new File(file);
            cfgFile.delete();
            this.config = new Configuration(cfgFile);
            config.setProperty("blockentry"+i+".x", blackListedBlock.get(i).getX());
            config.setProperty("blockentry"+i+".y", blackListedBlock.get(i).getY());
            config.setProperty("blockentry"+i+".z", blackListedBlock.get(i).getZ());
            config.setProperty("blockentry"+i+".world", blackListedBlock.get(i).getWorld().getName());
        }
        config.save();
    }
}
