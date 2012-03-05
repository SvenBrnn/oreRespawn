/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.orerespawn;

import com.sk89q.worldedit.WorldEdit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Sven
 */
public class oreRespawnConfig {

    private YamlConfiguration config;
    private File cfgFile;
    private String file;
    private JavaPlugin plugin;
    public List<oreRespawnConfigWorld> worldConfigs;
    public List<World> enabledWorld;

    public oreRespawnConfig() {
        checkConfig();
    }

    oreRespawnConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        file = "plugins/oreRespawn/config.yml";
        cfgFile = new File(file);
        this.config = new YamlConfiguration();
        if (!cfgFile.exists()) {
            this.createConfig();
        }
        try {
            config.load(cfgFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        String cfgVers = (String) config.get("config.configVersion");
        if (cfgVers == null || !cfgVers.equals("0.6")) {
            System.out.println("[OreRespawn] Recreate Config!");
            cfgFile.delete();
            this.config = new YamlConfiguration();
            this.createConfig();
        }
        try {
            config.load(cfgFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.configLoader();
    }

    private void checkConfig() {
    }

    private void configLoader() {
        List<World> worlds = plugin.getServer().getWorlds();
        enabledWorld = new ArrayList<World>();
        worldConfigs = new ArrayList<oreRespawnConfigWorld>();
        config.set("config.configVersion", "0.6");
        for (int i = 0; i < worlds.size(); i++) {
            oreRespawnConfigWorld conf = new oreRespawnConfigWorld();
            conf.worldName = worlds.get(i).getName();
            conf.cfgMaxDistance_coal = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".coal.maxDistance"));
            if ((String) config.get("config.world." + worlds.get(i).getName() + ".coal.respawnDelayInSec") == null) {
                conf.respawnDelay_coal = 0;
                config.set("config.world." + worlds.get(i).getName() + ".coal.respawnDelayInSec", "0");
            } else {
                conf.respawnDelay_coal = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".coal.respawnDelayInSec"));
            }


            conf.cfgMaxDistance_iron = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".iron.maxDistance"));
            if ((String) config.get("config.world." + worlds.get(i).getName() + ".iron.respawnDelayInSec") == null) {
                conf.respawnDelay_iron = 0;
                config.set(worlds.get(i).getName() + ".iron.respawnDelayInSec", "0");
            } else {
                conf.respawnDelay_iron = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".iron.respawnDelayInSec"));
            }


            conf.cfgMaxDistance_gold = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".gold.maxDistance"));
            if ((String) config.get("config.world." + worlds.get(i).getName() + ".gold.respawnDelayInSec") == null) {
                conf.respawnDelay_gold = 0;
                config.set(worlds.get(i).getName() + ".gold.respawnDelayInSec", "0");
            } else {
                conf.respawnDelay_gold = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".gold.respawnDelayInSec"));
            }


            conf.cfgMaxDistance_diamond = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".diamond.maxDistance"));
            if ((String) config.get("config.world." + worlds.get(i).getName() + ".diamond.respawnDelayInSec") == null) {
                conf.respawnDelay_diamond = 0;
                config.set(worlds.get(i).getName() + ".diamond.respawnDelayInSec", "0");
            } else {
                conf.respawnDelay_diamond = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".diamond.respawnDelayInSec"));
            }


            conf.cfgMaxDistance_lapis = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".lapis.maxDistance"));
            if ((String) config.get("config.world." + worlds.get(i).getName() + ".lapis.respawnDelayInSec") == null) {
                conf.respawnDelay_lapis = 0;
                config.set(worlds.get(i).getName() + ".lapis.respawnDelayInSec", "0");
            } else {
                conf.respawnDelay_lapis = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".lapis.respawnDelayInSec"));
            }


            conf.cfgMaxDistance_redstone = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".redstone.maxDistance"));
            if ((String) config.get("config.world." + worlds.get(i).getName() + ".redstone.respawnDelayInSec") == null) {
                conf.respawnDelay_redstone = 0;
                config.set(worlds.get(i).getName() + ".redstone.respawnDelayInSec", "0");
            } else {
                conf.respawnDelay_redstone = new Integer((String) config.get("config.world." + worlds.get(i).getName() + ".redstone.respawnDelayInSec"));
            }

            String var = (String) config.get("config.world." + worlds.get(i).getName() + ".enabled");
            if (var == null || var.equals("")) {
                var = "true";
                config.set("config.world." + worlds.get(i).getName() + ".enabled", "true");
            }
            
            String regionMode = (String) config.get("config.world." + worlds.get(i).getName() + ".regionMode");
            if (regionMode == null || regionMode.equals("")) {
                regionMode = "true";
                config.set("config.world." + worlds.get(i).getName() + ".regionMode", "true");
            }

            if (regionMode == null ? "true" == null : regionMode.equals("true")) {
                try {
                    if(!WorldEdit.getVersion().equals("")){
                        conf.regionRespawnMode = true;
                        System.out.println("[oreRespawn] " + worlds.get(i).getName() + " is in RegionsMode!");
                    } else {
                        System.out.println("[oreRespawn] WorldEdit is needed for RegionsMode!");
                    }
                } catch (Exception e) {
                    System.out.println("[oreRespawn] WorldEdit is needed for RegionsMode!");
                }
            }
            
            worldConfigs.add(conf);
        }
        try {
            config.save(cfgFile);
        } catch (IOException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createConfig() {        

        List<World> worlds = plugin.getServer().getWorlds();
        for (int i = 0; i < worlds.size(); i++) {
            config.set("config.configVersion", "0.6");
            config.set("config.world." + worlds.get(i).getName() + ".enabled", "true");
            
            if(oreRespawn.worldEdit != null)
                config.set("config.world." + worlds.get(i).getName() + ".regionMode", "true");
            else
                config.set("config.world." + worlds.get(i).getName() + ".regionMode", "false");
            
            config.set("config.world." + worlds.get(i).getName() + ".coal.maxDistance", "50");
            config.set("config.world." + worlds.get(i).getName() + ".coal.respawnDelayInSec", "50");
            config.set("config.world." + worlds.get(i).getName() + ".iron.maxDistance", "50");
            config.set("config.world." + worlds.get(i).getName() + ".iron.respawnDelayInSec", "50");
            config.set("config.world." + worlds.get(i).getName() + ".gold.maxDistance", "50");
            config.set("config.world." + worlds.get(i).getName() + ".gold.respawnDelayInSec", "50");
            config.set("config.world." + worlds.get(i).getName() + ".lapis.maxDistance", "50");
            config.set("config.world." + worlds.get(i).getName() + ".lapis.respawnDelayInSec", "50");
            config.set("config.world." + worlds.get(i).getName() + ".diamond.maxDistance", "50");
            config.set("config.world." + worlds.get(i).getName() + ".diamond.respawnDelayInSec", "50");
            config.set("config.world." + worlds.get(i).getName() + ".redstone.maxDistance", "50");
            config.set("config.world." + worlds.get(i).getName() + ".redstone.respawnDelayInSec", "50");
        }
        try {
            config.save(cfgFile);
        } catch (IOException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeWorldEnable(String worldname, boolean enable) {
        if (enable) {
            config.set(worldname + ".enabled", "true");
        } else {
            config.set(worldname + ".enabled", "false");
        }
        try {
            config.save(cfgFile);
        } catch (IOException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changeMaxRadius(Integer maxRadius, String world, String ore) {
        config.set(world + "." + ore + ".maxDistance", maxRadius.toString());
        try {
            config.save(cfgFile);
        } catch (IOException ex) {
            Logger.getLogger(oreRespawnConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
