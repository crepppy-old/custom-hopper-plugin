package net.immortalmc.hoppers;

import net.immortalmc.hoppers.listeners.*;
import net.immortalmc.hoppers.utils.SQLUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;

public class ImmortalHoppers extends JavaPlugin {
    private static ImmortalHoppers instance;
    private HashMap<Location, Hopper> hoppers;
    private Economy econ;
    private HashMap<Player, Hopper> customInventoryMode;

    public static ImmortalHoppers getInstance() {
        return instance;
    }

    public HashMap<Player, Hopper> getCustomInventoryMode() {
        return customInventoryMode;
    }

    @Override
    public void onEnable() {
        //Register events are assign variables
        instance = this;
        hoppers = new HashMap<>();
        customInventoryMode = new HashMap<>();
        getConfig().options().copyDefaults(true);
        saveConfig();
        //Register commands and events
        Bukkit.getPluginCommand("ihoppers").setExecutor(new IHopperCommand());
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new HopperInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new HopperPickupItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new HopperMoveItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        //Vault economy hook
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        econ = rsp.getProvider();
        //Loads saved hoppers from SQL database
        SQLUtil.load();
        //Ticks each hopper at the specified interval for their level;
        for (Hopper.Level h : Hopper.Level.values()) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Iterator<Hopper> hopperIterator = hoppers.values().iterator();
                while (hopperIterator.hasNext()) {
                    Hopper hopper = hopperIterator.next();
                    if (hopper.getLocation().getBlock().getType() != Material.HOPPER) {
                        hopperIterator.remove();
                    } else if (hopper.getLevel().equals(h)) {
                        hopper.tick();
                    }
                }
            }, h.getSpeed(), h.getSpeed());
        }
    }

    @Override
    public void onDisable() {
        SQLUtil.save();
    }

    public HashMap<Location, Hopper> getHoppers() {
        return hoppers;
    }

    public Economy getEconomy() {
        return econ;
    }
}
