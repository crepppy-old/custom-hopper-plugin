package net.immortalmc.hoppers;

import net.immortalmc.hoppers.listeners.BlockPlaceListener;
import net.immortalmc.hoppers.listeners.HopperMoveItemEvent;
import net.immortalmc.hoppers.listeners.ItemDropEvent;
import net.immortalmc.hoppers.utils.SQLUtil;
import net.immortalmc.hoppers.listeners.HopperInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImmortalHoppers extends JavaPlugin {
    private static ImmortalHoppers instance;
    private List<Hopper> hoppers;

    public static ImmortalHoppers getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        //Register events are assign variables
        //TODO vault integration for upgrades - crepppy 18/09
        instance = this;
        hoppers = new ArrayList<>();
        getConfig().options().copyDefaults(true);
        saveConfig();
        Bukkit.getPluginCommand("ihoppers").setExecutor(new IHopperCommand());
        Bukkit.getPluginManager().registerEvents(new BlockPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new HopperInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new HopperMoveItemEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ItemDropEvent(), this);
        //Loads saved hoppers from SQL database
        SQLUtil.load();
        //Ticks each hopper at the specified interval for their level
        for (Hopper.Level h : Hopper.Level.values()) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                Iterator<Hopper> hopperIterator = hoppers.iterator();
                while (hopperIterator.hasNext()) {
                    Hopper hopper = hopperIterator.next();
                    if (hopper.getLocation().getBlock().getType() != Material.HOPPER) hopperIterator.remove();
                    else if (hopper.getLevel().equals(h))
                        hopper.tick();
                }
            }, h.getSpeed(), h.getSpeed());
        }
    }

    @Override
    public void onDisable() {
        SQLUtil.save();
    }

    public List<Hopper> getHoppers() {
        return hoppers;
    }
}
