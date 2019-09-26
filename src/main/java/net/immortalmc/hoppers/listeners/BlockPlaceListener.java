package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.ImmortalHoppers;
import net.immortalmc.hoppers.utils.ItemGenerator;
import net.immortalmc.hoppers.utils.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

public class BlockPlaceListener implements Listener {
    @EventHandler
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        if (e.getItemInHand().getType() != Material.HOPPER) {
            return;
        }
        //Adds placed hoppers to the array
        if (e.getItemInHand().equals(ItemManager.cropHopper) || e.getItemInHand().equals(ItemManager.mobHopper)) {
            ImmortalHoppers.getInstance().getHoppers()
                    .put(e.getBlockPlaced().getLocation(), new Hopper(e.getBlockPlaced().getLocation(), -1, ChatColor.stripColor(e.getItemInHand().getItemMeta().getDisplayName()).split(" ")[0], new ArrayList<>(), null));
        } else {
            //If the title / lore of the hopper
            Hopper hopper = new Hopper(e.getBlockPlaced().getLocation(), 1, null);
            ImmortalHoppers.getInstance().getHoppers()
                    .put(e.getBlockPlaced().getLocation(), hopper);
            try {
                //Check if the item is a custom hopper
                int tier = Integer.parseInt(ChatColor.stripColor(e.getItemInHand().getItemMeta().getDisplayName().substring(5).split(" ")[0].trim()));
                if (ChatColor.stripColor(e.getItemInHand().getItemMeta().getLore().get(0)).startsWith("Speed: ")) {
                    hopper.setLevel(Hopper.Level.getLevel(tier));
                }
                //Using catch isn't the best method but unless performance issues become apparent should be fine
            } catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException ignored) {
            }
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        //Removes saved hoppers from the array
        if (e.getBlock().getType() == Material.HOPPER) {
            Hopper h = ImmortalHoppers.getInstance().getHoppers().get(e.getBlock().getLocation());
            if (h.getLevel() != Hopper.Level.SPECIAL) {
                //If it isn't a special hopper (cannot be upgraded) handle the
                // block break separately and drop the correct tiered hopper
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
                e.getBlock().getWorld().dropItem(h.getLocation(), new ItemGenerator(Material.HOPPER).setName("&6Tier " + h.getLevel() + " Hopper").setLore("Speed: &6" + (Math.round(20.0 / h.getLevel().getSpeed() * 10) / 10) + " items / second", "Blacklist: &6" + h.getLevel().getMaxBlacklist() + " items").build());
            }
            ImmortalHoppers.getInstance().getHoppers().remove(e.getBlock().getLocation());
        }
    }
}
