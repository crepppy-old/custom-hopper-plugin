package net.immortalmc.hoppers.utils;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.ImmortalHoppers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SQLUtil {
    private static SQLUtil instance = new SQLUtil();
    private static Connection connection;

    private SQLUtil() {
        try {
            String host = ImmortalHoppers.getInstance().getConfig().getString("sql.host");
            String port = ImmortalHoppers.getInstance().getConfig().getString("sql.port");
            String database = ImmortalHoppers.getInstance().getConfig().getString("sql.database");
            String username = ImmortalHoppers.getInstance().getConfig().getString("sql.username");
            String password = ImmortalHoppers.getInstance().getConfig().getString("sql.password");
            connection = DriverManager
                    .getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static SQLUtil getSQLUtil() {
        return instance;
    }

    public static void load() {
        try {
            connection
                    .prepareStatement("CREATE TABLE IF NOT EXISTS `immortalhoppers` (`location` VARCHAR(50) NOT NULL PRIMARY KEY, `level` INT NOT NULL, `special` VARCHAR(50) NOT NULL, `inventory` VARCHAR(50) NULL, `blacklist` VARCHAR(100) NULL);")
                    .executeUpdate();
            ResultSet rs = connection.prepareStatement("SELECT * FROM immortalhoppers").executeQuery();
            while (rs.next()) {
                String world = rs.getString("location").split(":")[0].trim();
                int x = Integer.parseInt(rs.getString("location").split(":")[1].trim());
                int y = Integer.parseInt(rs.getString("location").split(":")[2].trim());
                int z = Integer.parseInt(rs.getString("location").split(":")[3].trim());
                Location location = new Location(Bukkit.getWorld(world), x, y, z);
                Location inventory = null;
                if (rs.getObject("inventory") != null) {
                    String inventoryWorld = rs.getString("inventory").split(":")[0].trim();
                    int inventoryX = Integer.parseInt(rs.getString("inventory").split(":")[1].trim());
                    int inventoryY = Integer.parseInt(rs.getString("inventory").split(":")[2].trim());
                    int inventoryZ = Integer.parseInt(rs.getString("inventory").split(":")[3].trim());
                    inventory = new Location(Bukkit.getWorld(inventoryWorld), inventoryX, inventoryY, inventoryZ);
                }

                List<Material> blacklist = rs.getObject("blacklist") == null ? new ArrayList<>() : Arrays.stream(rs.getString("blacklist").split(",")).map(Material::matchMaterial).collect(Collectors.toList());
                ImmortalHoppers.getInstance().getHoppers()
                        .put(location, new Hopper(location, rs.getInt("level"), rs.getString("special"), blacklist, inventory));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            for (Hopper h : ImmortalHoppers.getInstance().getHoppers().values()) {
                String world = h.getLocation().getWorld().getName();
                int x = h.getLocation().getBlockX();
                int y = h.getLocation().getBlockY();
                int z = h.getLocation().getBlockZ();
                String location = world + ":" + x + ":" + y + ":" + z;

                String iLocation = "null";
                if (h.getCustomInventory() != null) {
                    String iWorld = h.getCustomInventory().getWorld().getName();
                    int iX = h.getCustomInventory().getBlockX();
                    int iY = h.getCustomInventory().getBlockY();
                    int iZ = h.getCustomInventory().getBlockZ();
                    iLocation = "'" + iWorld + ":" + iX + ":" + iY + ":" + iZ + "'";
                }
                String blacklist = "null";
                if (h.getBlacklist() != null) {
                    blacklist = "'" + h.getBlacklist().stream().map(b -> b.toString() + ", ").collect(Collectors.joining("")) + "'";
                }

                //Insert the hopper values into the table
                connection
                        .prepareStatement("REPLACE INTO immortalhoppers(location, level, special, inventory, blacklist) VALUES('" + location + "', " + h.getLevel().getNumber() + ", '" + h.getSpecial() + "', " + iLocation + ", " + blacklist + ");")
                        .executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
