/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svenbrnn.oreRespawn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
            Date dt = new Date();
            dt.setTime(dt.getTime() - (1000 * config.respawnDelay));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String uhrzeit = sdf.format(dt);

            Statement sql = conn.createStatement();
            ResultSet res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist WHERE time < DATETIME('" + uhrzeit + "')");
            List<Integer> idList = new ArrayList<Integer>();

            while (res.next()) {
                blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))),res.getInt("typ"))) ;
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

    public List<oreRespawnBlockToRespawn> getBlocksFromSpawnListAndDelItAll() {
        List<oreRespawnBlockToRespawn> blList = new ArrayList<oreRespawnBlockToRespawn>();
        try {
            Statement sql = conn.createStatement();
            ResultSet res = sql.executeQuery("SELECT x, y, z, typ, world, id FROM ore_spawnlist");
            List<Integer> idList = new ArrayList<Integer>();

            while (res.next()) {
                blList.add(new oreRespawnBlockToRespawn(server.getWorld(res.getString("world")).getBlockAt(new Location(server.getWorld(res.getString("world")), res.getInt("x"), res.getInt("y"), res.getInt("z"))),res.getInt("typ"))) ;
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

    public int getNumOreMined(int oreTyp)
    {
        int numOre = 0;
        try {
            Statement sql = conn.createStatement();
            ResultSet res = sql.executeQuery("SELECT typ FROM ore_minelog WHERE typ='"+oreTyp+"'");
            while(res.next())
            {
                numOre++;
            }
        } catch (SQLException ex) {
            //Logger.getLogger(oreRespawnDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
        return numOre;
    }

    public void clearAllOreMined()
    {
        try {
            Statement sql = conn.createStatement();
            sql.execute("DELETE FROM ore_minelog");
        } catch (SQLException ex) {
            //Logger.getLogger(oreRespawnDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
