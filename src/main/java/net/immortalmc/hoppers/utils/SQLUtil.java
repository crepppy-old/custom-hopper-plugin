package net.immortalmc.hoppers.utils;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.ImmortalHoppers;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
                    .prepareStatement("CREATE TABLE IF NOT EXISTS `immortalhoppers` (`location` VARCHAR(50) NOT NULL, `level` TINYINT(5) NOT NULL, `special` VARCHAR(50) NOT NULL);")
                    .executeUpdate();
            ResultSet rs = connection.prepareStatement("SELECT * FROM immortalhoppers").executeQuery();
            while (rs.next()) {
                String world = rs.getString("location").split(":")[0].trim();
                int x = Integer.parseInt(rs.getString("location").split(":")[1].trim());
                int y = Integer.parseInt(rs.getString("location").split(":")[2].trim());
                int z = Integer.parseInt(rs.getString("location").split(":")[3].trim());
                ImmortalHoppers.getInstance().getHoppers()
                        .add(new Hopper(new Location(Bukkit.getWorld(world), x, y, z), rs.getInt("level"), null, rs.getString("special")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            connection
                    .prepareStatement("DELETE FROM immortalhoppers;")
                    .executeUpdate();
            for (Hopper h : ImmortalHoppers.getInstance().getHoppers()) {
                String world = h.getLocation().getWorld().getName();
                int x = h.getLocation().getBlockX();
                int y = h.getLocation().getBlockY();
                int z = h.getLocation().getBlockZ();
                String location = world + ":" + x + ":" + y + ":" + z;
                //Insert the hopper values into the table
                connection
                        .prepareStatement("INSERT INTO immortalhoppers(location, level, special) VALUES('" + location + "', " + h.getLevel().getNumber() + ", '" + h.getSpecial() + "');")
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
