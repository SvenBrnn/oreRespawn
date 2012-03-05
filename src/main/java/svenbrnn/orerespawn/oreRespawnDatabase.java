/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.orerespawn;

import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Server;

/**
 *
 * @author Sven
 */
public class oreRespawnDatabase {

    public String file = "./plugins/oreRespawn/data.db";
    private Connection conn = null;
    private Server server;
    private oreRespawnConfig config;

    public oreRespawnDatabase(Server server, oreRespawnConfig config) {
        System.out.println("Database class loaded");
        initDB();
        this.server = server;
        this.config = config;
    }
    private boolean try_once = false;

    private void initDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + file);
            Statement sql = conn.createStatement();
            sql.execute("CREATE TABLE IF NOT EXISTS ore_blacklist "
                    + "(`id` INTEGER NOT NULL PRIMARY KEY , "
                    + "`x` INT( 6 ) NOT NULL ,"
                    + "`y` INT( 6 ) NOT NULL ,"
                    + "`z` INT( 6 ) NOT NULL ,"
                    + "`world` INT( 6 ) NOT NULL)");
            sql = conn.createStatement();
            sql.execute("CREATE TABLE IF NOT EXISTS ore_spawnlist "
                    + "(`id` INTEGER NOT NULL PRIMARY KEY , "
                    + "`x` INT( 6 ) NOT NULL ,"
                    + "`y` INT( 6 ) NOT NULL ,"
                    + "`z` INT( 6 ) NOT NULL ,"
                    + "`typ` INT( 6 ) NOT NULL ,"
                    + "`world` INT( 6 ) NOT NULL,"
                    + "`time` DATETIME NOT NULL)");
            sql.execute("CREATE TABLE IF NOT EXISTS ore_minelog "
                    + "(`id` INTEGER NOT NULL PRIMARY KEY , "
                    + "`x` INT( 6 ) NOT NULL ,"
                    + "`y` INT( 6 ) NOT NULL ,"
                    + "`z` INT( 6 ) NOT NULL ,"
                    + "`typ` INT( 6 ) NOT NULL ,"
                    + "`world` INT( 6 ) NOT NULL)");
            sql.execute("CREATE TABLE IF NOT EXISTS ore_region "
                    + "(`id` INTEGER NOT NULL PRIMARY KEY , "
                    + "`x1` INT( 6 ) NOT NULL ,"
                    + "`y1` INT( 6 ) NOT NULL ,"
                    + "`z1` INT( 6 ) NOT NULL ,"
                    + "`x2` INT( 6 ) NOT NULL ,"
                    + "`y2` INT( 6 ) NOT NULL ,"
                    + "`z2` INT( 6 ) NOT NULL ,"
                    + "`regionname` CHAR( 64 ) NOT NULL ,"
                    + "`world` INT( 6 ) NOT NULL)");
        } catch (Exception e) {
            System.out.println("Could not connect " + e.getMessage());
            System.out.println(e.getCause());
        }
    }

    public void addBlockToBlacklist(int x, int y, int z, String world) {
        try {
            Statement sql = conn.createStatement();
            sql.execute("INSERT INTO ore_blacklist(x, y, z, world) VALUES('" + x + "','" + y + "','" + z + "','" + world + "')");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void addRegion(int x1, int y1, int z1, int x2, int y2, int z2, String name, String world) {
        try {
            Statement sql = conn.createStatement();
            sql.execute("INSERT INTO ore_region(x1, y1, z1, x2, y2, z2, regionname, world) VALUES('" + x1 + "','" + y1 + "','" + z1 + "','" + x2 + "','" + y2 + "','" + z2 + "','" + name + "','" + world + "')");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<oreRespawnRegion> getRegions() {
        List<oreRespawnRegion> list = new ArrayList<oreRespawnRegion>();
        try {
            Statement sql = conn.createStatement();
            ResultSet res = sql.executeQuery("SELECT x1, y1, z1, x2, y2, z2, regionname, world FROM ore_region");
            while (res.next()) {
                oreRespawnRegion regio = new oreRespawnRegion();
                int x1 = res.getInt("x1");
                int x2 = res.getInt("x2");
                int y1 = res.getInt("y1");
                int y2 = res.getInt("y2");
                int z1 = res.getInt("z1");
                int z2 = res.getInt("z2");
                regio.name = res.getString("regionname");
                String world = res.getString("world");
                regio.region = new CuboidRegion(new BlockWorldVector(new BukkitWorld(server.getWorld(world)), x1, y1, z1), new BlockWorldVector(new BukkitWorld(server.getWorld(world)), x2, y2, z2));
                list.add(regio);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    public void deleteRegion(String regionName) {
        try {
            Statement sql = conn.createStatement();
            sql.execute("DELETE FROM ore_region WHERE name='" + regionName + "'");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int isBlockInBacklist(int x, int y, int z, String world) {
        try {
            Statement sql = conn.createStatement();
            ResultSet res = sql.executeQuery("SELECT id FROM ore_blacklist WHERE x='" + x + "' AND y='" + y + "' AND z='" + z + "' AND world='" + world + "'");
            if (res.next()) {
                return res.getInt("id");
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return -1;
    }

    public void deleteBlockFromBlacklist(int id) {
        try {
            Statement sql = conn.createStatement();
            sql.execute("DELETE FROM ore_blacklist WHERE id='" + id + "'");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void addBlocksToSpawnList(int x, int y, int z, int typ, String world, String dateTime) {
        try {
            Statement sql = conn.createStatement();
            sql.execute("INSERT INTO ore_spawnlist(x, y, z, typ, world, time) VALUES('" + x + "','" + y + "','" + z + "','" + typ + "','" + world + "','" + dateTime + "')");
            sql.execute("INSERT INTO ore_minelog(x, y, z, typ, world) VALUES('" + x + "','" + y + "','" + z + "','" + typ + "','" + world + "')");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public List<oreRespawnBlockToRespawn> getBlocksFromSpawnListAndDelIt() {
        List<oreRespawnBlockToRespawn> blList = new ArrayList<oreRespawnBlockToRespawn>();
        try {

            Date dt;
            int maxdel = 0;

            for (int i = 0; i < server.getWorlds().size(); i++) {
                oreRespawnConfigWorld conf = null;
                for (int j = 0; j < this.config.worldConfigs.size(); j++) {
                    if (this.config.worldConfigs.get(j).worldName.equals(server.getWorlds().get(i).getName())) {
                        conf = this.config.worldConfigs.get(j);
                    }
                }

                if (conf == null) {
                    continue;
                }

                //Get Gold
                maxdel = conf.respawnDelay_gold;
                dt = new Date();
                dt.setTime(dt.getTime() - (1000 * maxdel));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String uhrzeit = sdf.format(dt);

                Statement sql = conn.createStatement();
                ResultSet res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist WHERE time < DATETIME('" + uhrzeit + "') AND typ='14'");
                List<Integer> idList = new ArrayList<Integer>();

                while (res.next()) {
                    blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))), res.getInt("typ")));
                    idList.add(res.getInt("id"));
                }

                //Get Iron
                maxdel = conf.respawnDelay_iron;
                dt = new Date();
                dt.setTime(dt.getTime() - (1000 * maxdel));
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                uhrzeit = sdf.format(dt);

                sql = conn.createStatement();
                res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist WHERE time < DATETIME('" + uhrzeit + "') AND typ='15'");

                while (res.next()) {
                    if (server.getWorld(res.getString("world")) != null) {
                        blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))), res.getInt("typ")));
                    }
                    idList.add(res.getInt("id"));
                }

                //Get Cloal
                maxdel = conf.respawnDelay_coal;
                dt = new Date();
                dt.setTime(dt.getTime() - (1000 * maxdel));
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                uhrzeit = sdf.format(dt);

                sql = conn.createStatement();
                res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist WHERE time < DATETIME('" + uhrzeit + "') AND typ='16'");

                while (res.next()) {
                    if (server.getWorld(res.getString("world")) != null) {
                        blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))), res.getInt("typ")));
                    }
                    idList.add(res.getInt("id"));
                }

                //Get Lapis
                maxdel = conf.respawnDelay_lapis;
                dt = new Date();
                dt.setTime(dt.getTime() - (1000 * maxdel));
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                uhrzeit = sdf.format(dt);

                sql = conn.createStatement();
                res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist WHERE time < DATETIME('" + uhrzeit + "') AND typ='21'");

                while (res.next()) {
                    if (server.getWorld(res.getString("world")) != null) {
                        blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))), res.getInt("typ")));
                    }
                    idList.add(res.getInt("id"));
                }

                //Get Diamond
                maxdel = conf.respawnDelay_diamond;
                dt = new Date();
                dt.setTime(dt.getTime() - (1000 * maxdel));
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                uhrzeit = sdf.format(dt);

                sql = conn.createStatement();
                res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist WHERE time < DATETIME('" + uhrzeit + "') AND typ='56'");

                while (res.next()) {
                    if (server.getWorld(res.getString("world")) != null) {
                        blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))), res.getInt("typ")));
                    }
                    idList.add(res.getInt("id"));
                }

                //Get Redstone
                maxdel = conf.respawnDelay_redstone;
                dt = new Date();
                dt.setTime(dt.getTime() - (1000 * maxdel));
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                uhrzeit = sdf.format(dt);

                sql = conn.createStatement();
                res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist WHERE time < DATETIME('" + uhrzeit + "') AND typ='73'");

                while (res.next()) {
                    if (server.getWorld(res.getString("world")) != null) {
                        blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))), res.getInt("typ")));
                    }
                    idList.add(res.getInt("id"));
                }

                //Delete all Ores from DB
                for (int j = 0; j < idList.size(); j++) {
                    sql = conn.createStatement();
                    sql.execute("DELETE FROM ore_spawnlist WHERE id=" + idList.get(j));
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        return blList;
    }

    public List<oreRespawnBlockToRespawn> getBlocksFromSpawnListAndDelItAll() {
        List<oreRespawnBlockToRespawn> blList = new ArrayList<oreRespawnBlockToRespawn>();
        try {
            Statement sql = conn.createStatement();
            ResultSet res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist");
            List<Integer> idList = new ArrayList<Integer>();

            while (res.next()) {
                blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))), res.getInt("typ")));
                idList.add(res.getInt("id"));
            }

            for (int i = 0; i < idList.size(); i++) {
                sql = conn.createStatement();
                sql.execute("DELETE FROM ore_spawnlist WHERE id=" + idList.get(i));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return blList;
    }

    public int getNumOreMined(int oreTyp) {
        int numOre = 0;
        try {
            Statement sql = conn.createStatement();
            ResultSet res = sql.executeQuery("SELECT typ FROM ore_minelog WHERE typ='" + oreTyp + "'");
            while (res.next()) {
                numOre++;
            }
        } catch (SQLException ex) {
            //Logger.getLogger(oreRespawnDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numOre;
    }

    public void clearAllOreMined() {
        try {
            Statement sql = conn.createStatement();
            sql.execute("DELETE FROM ore_minelog");
        } catch (SQLException ex) {
            //Logger.getLogger(oreRespawnDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
