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
    public int cfgMaxDistance_coal = 0;
    public int respawnDelay_coal = 0;
    public int cfgMaxDistance_iron = 0;
    public int respawnDelay_iron = 0;
    public int cfgMaxDistance_gold = 0;
    public int respawnDelay_gold = 0;
    public int cfgMaxDistance_diamond = 0;
    public int respawnDelay_diamond = 0;
    public int cfgMaxDistance_lapis = 0;
    public int respawnDelay_lapis = 0;
    public int cfgMaxDistance_redstone = 0;
    public int respawnDelay_redstone = 0;
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
        config.load();
        if ((String) config.getProperty("configVersion") == null || !config.getProperty("configVersion").equals("0.5")) {
            cfgFile.delete();
            this.config = new Configuration(cfgFile);
            this.createConfig();
        }
        config.load();
        this.configLoader();
    }

    private void checkConfig() {
    }

    private void configLoader() {
        List<World> worlds = plugin.getServer().getWorlds();
        enabledWorld = new ArrayList<World>();
        config.setProperty("config.configVersion", "0.5");
        for (int i = 0; i < worlds.size(); i++) {
            cfgMaxDistance_coal = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".coal.maxDistance"));
            if ((String) config.getProperty("config.world." + worlds.get(i).getName() + ".coal.respawnDelayInSec") == null) {
                respawnDelay_coal = 0;
                config.setProperty("config.world." + worlds.get(i).getName() + ".coal.respawnDelayInSec", "0");
            } else {
                respawnDelay_coal = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".coal.respawnDelayInSec"));
            }


            cfgMaxDistance_iron = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".iron.maxDistance"));
            if ((String) config.getProperty("config.world." + worlds.get(i).getName() + ".iron.respawnDelayInSec") == null) {
                respawnDelay_iron = 0;
                config.setProperty(worlds.get(i).getName() + ".iron.respawnDelayInSec", "0");
            } else {
                respawnDelay_iron = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".iron.respawnDelayInSec"));
            }


            cfgMaxDistance_gold = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".gold.maxDistance"));
            if ((String) config.getProperty("config.world." + worlds.get(i).getName() + ".gold.respawnDelayInSec") == null) {
                respawnDelay_gold = 0;
                config.setProperty(worlds.get(i).getName() + ".gold.respawnDelayInSec", "0");
            } else {
                respawnDelay_gold = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".gold.respawnDelayInSec"));
            }


            cfgMaxDistance_diamond = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".diamond.maxDistance"));
            if ((String) config.getProperty("config.world." + worlds.get(i).getName() + ".diamond.respawnDelayInSec") == null) {
                respawnDelay_diamond = 0;
                config.setProperty(worlds.get(i).getName() + ".diamond.respawnDelayInSec", "0");
            } else {
                respawnDelay_diamond = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".diamond.respawnDelayInSec"));
            }


            cfgMaxDistance_lapis = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".lapis.maxDistance"));
            if ((String) config.getProperty("config.world." + worlds.get(i).getName() + ".lapis.respawnDelayInSec") == null) {
                respawnDelay_lapis = 0;
                config.setProperty(worlds.get(i).getName() + ".lapis.respawnDelayInSec", "0");
            } else {
                respawnDelay_lapis = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".lapis.respawnDelayInSec"));
            }


            cfgMaxDistance_redstone = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".redstone.maxDistance"));
            if ((String) config.getProperty("config.world." + worlds.get(i).getName() + ".redstone.respawnDelayInSec") == null) {
                respawnDelay_redstone = 0;
                config.setProperty(worlds.get(i).getName() + ".redstone.respawnDelayInSec", "0");
            } else {
                respawnDelay_redstone = new Integer((String) config.getProperty("config.world." + worlds.get(i).getName() + ".redstone.respawnDelayInSec"));
            }

            String var = (String) config.getProperty("config.world." + worlds.get(i).getName() + ".enabled");
            if (var == null || var.equals("")) {
                var = "true";
                config.setProperty("config.world." + worlds.get(i).getName() + ".enabled", "true");
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
            config.setProperty("config.configVersion", "0.5");
            config.setProperty("config.world." + worlds.get(i).getName() + ".enabled", "true");
            config.setProperty("config.world." + worlds.get(i).getName() + ".coal.maxDistance", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".coal.respawnDelayInSec", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".iron.maxDistance", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".iron.respawnDelayInSec", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".gold.maxDistance", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".gold.respawnDelayInSec", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".lapis.maxDistance", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".lapis.respawnDelayInSec", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".diamond.maxDistance", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".diamond.respawnDelayInSec", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".redstone.maxDistance", "50");
            config.setProperty("config.world." + worlds.get(i).getName() + ".redstone.respawnDelayInSec", "50");
        }
        config.save();
    }

    public void changeWorldEnable(String worldname, boolean enable) {
        if (enable) {
            config.setProperty(worldname + ".enabled", "true");
        } else {
            config.setProperty(worldname + ".enabled", "false");
        }
        config.save();
    }

    public void changeMaxRadius(Integer maxRadius, String world, String ore) {
        config.setProperty(world + "." + ore + ".maxDistance", maxRadius.toString());
        config.save();
    }
}
