package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.ImmortalHoppers;
import net.immortalmc.hoppers.utils.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        if (e.getItemInHand().getType() != Material.HOPPER) {
            return;
        }
        //Adds placed hoppers to the array
        if (e.getItemInHand().equals(ItemManager.cropHopper) || e.getItemInHand().equals(ItemManager.mobHopper)) {
            ImmortalHoppers.getInstance().getHoppers()
                    .put(e.getBlockPlaced().getLocation(), new Hopper(e.getBlockPlaced().getLocation(), -1, ChatColor.stripColor(e.getItemInHand().getItemMeta().getDisplayName()).split(" ")[0], null, null));
        } else {
            ImmortalHoppers.getInstance().getHoppers()
                    .put(e.getBlockPlaced().getLocation(), new Hopper(e.getBlockPlaced().getLocation(), 1, null));
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        //Removes saved hoppers from the array
        if (e.getBlock().getType() == Material.HOPPER) {
            ImmortalHoppers.getInstance().getHoppers().remove(e.getBlock().getLocation());
        }
    }
}
