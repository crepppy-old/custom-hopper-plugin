package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.utils.ItemGenerator;
import net.immortalmc.hoppers.utils.ItemManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        if (e.getClickedInventory().getHolder() == null && e.getClickedInventory().getTitle().startsWith("Hopper » ")) {
            if (clickedItem.equals(new ItemGenerator(Material.HOPPER).setName("&6Upgrades").setLore("Upgrade the hopper").build())) {
                Hopper hopper = ItemManager.getHopperFromItem(e.getClickedInventory().getItem(26));
                e.getWhoClicked().openInventory(ItemManager.getUpgrades(hopper));
            }
        } else if (e.getClickedInventory().getHolder() == null && e.getClickedInventory().getTitle().equalsIgnoreCase("Upgrades")) {
            e.setCancelled(true);
        }
    }
}
