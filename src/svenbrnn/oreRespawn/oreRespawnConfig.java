/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author Sven
 */
public class oreRespawnConfig {

    private Configuration config;
    private File cfgFile;
    private String file;
    private JavaPlugin plugin;
    public int cfgMaxDistance = 0;
    public int respawnDelay = 0;
    public List<World> enabledWorld;

    public oreRespawnConfig() {
        checkConfig();
    }

    oreRespawnConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        file = "plugins/oreRespawn/config.yml";
        cfgFile = new File(file);
        this.config = new Configuration(cfgFile);
        if (!cfgFile.exists()) {
            this.createConfig();
        }
        this.configLoader();
    }

    private void checkConfig() {
    }

    private void configLoader() {
        List<World> worlds = plugin.getServer().getWorlds();
        enabledWorld = new ArrayList<World>();
        config.load();
        cfgMaxDistance = new Integer((String) config.getProperty("maxDistance"));
        if ((String) config.getProperty("respawnDelayInSec") == null) {
            respawnDelay = 0;
            config.setProperty("respawnDelayInSec", "0");
        } else {
            respawnDelay = new Integer((String) config.getProperty("respawnDelayInSec"));
        }

        for (int i = 0; i < worlds.size(); i++) {
            String var = (String) config.getProperty(worlds.get(i).getName() + "enabled");
            if (var == null || var.equals("")) {
                var = "true";
                config.setProperty(worlds.get(i).getName() + "enabled", "true");
            }

            if (var == null ? "true" == null : var.equals("true")) {
                enabledWorld.add(worlds.get(i));
                System.out.println("[oreRespawn] " + worlds.get(i).getName() + " enabled!");
            } else if (var == null ? "false" == null : var.equals("false")) {
                System.out.println("[oreRespawn] " + worlds.get(i).getName() + " disabled!");
            } else {
                System.out.println("[oreRespawn] Error in Config!");
                return;
            }
        }
        config.save();
    }

    private void createConfig() {
        List<World> worlds = plugin.getServer().getWorlds();
        for (int i = 0; i < worlds.size(); i++) {
            config.setProperty(worlds.get(i).getName() + "enabled", "true");
        }
        config.setProperty("maxDistance", "50");
        config.setProperty("respawnDelayInSec", "50");
        config.save();
    }

    public void changeWorldEnable(String worldname, boolean enable) {
        if (enable) {
            config.setProperty(worldname + "enabled", "true");
        } else {
            config.setProperty(worldname + "enabled", "false");
        }
        config.save();
    }

    public void changeMaxRadius(int maxRadius) {
        config.setProperty("maxDistance", maxRadius);
        config.save();
    }
}
