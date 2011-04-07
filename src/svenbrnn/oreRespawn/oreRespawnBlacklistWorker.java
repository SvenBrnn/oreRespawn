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

    public void writeConfig()
    {

    }
    
}
