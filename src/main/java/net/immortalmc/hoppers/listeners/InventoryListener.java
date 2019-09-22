package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.ImmortalHoppers;
import net.immortalmc.hoppers.utils.ItemGenerator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory().getHolder() == null && e.getClickedInventory().getTitle().startsWith("Hopper » ")) {
            ItemStack clickedItem = e.getCurrentItem();
            e.setCancelled(true);
            if (clickedItem.equals(new ItemGenerator(Material.HOPPER).setName("&6Upgrades").setLore("Upgrade the hopper").build())) {
                Inventory upgrades = Bukkit.createInventory(null, 9, "Upgrades");
                String locationStr = ChatColor.stripColor(e.getClickedInventory().getItem(26).getItemMeta().getLore().get(0));
                List<Integer> coords = Pattern.compile("[A-Z]: (-?\\d*),?").matcher(locationStr).results().map(x -> Integer.parseInt(x.group())).collect(Collectors.toList());
                Location location = new Location(e.getWhoClicked().getWorld(), coords.get(0), coords.get(1), coords.get(2));
                Hopper hopper = ImmortalHoppers.getInstance().getHoppers().get(location);
                upgrades.setItem(8, new ItemGenerator(Material.HOPPER).setName("&eHopper").setLore(String.format("X: %s, Y: %s, Z: %s", hopper.getLocation().getBlockX(), hopper.getLocation().getBlockY(), hopper.getLocation().getBlockZ())).build());
                //h w u u u u u w w
                //todo open upgrades
            }
        }
    }
}
