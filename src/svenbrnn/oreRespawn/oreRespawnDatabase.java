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

/**
 *
 * @author Sven
 */
public class oreRespawnDatabase {

    public String file = "./plugins/oreRespawn/data.db";
    private Connection conn = null;

    public oreRespawnDatabase() {
        System.out.println("Database class loaded");
        initDB();
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

    public void deleteBlockFromBlacklist(int id)
    {
        try {
            Statement sql = conn.createStatement();
            sql.execute("DELETE FROM ore_blacklist WHERE id='"+id+"'");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
