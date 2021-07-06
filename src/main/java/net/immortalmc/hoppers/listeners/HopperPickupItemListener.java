package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.ImmortalHoppers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class HopperPickupItemListener implements Listener {
    @EventHandler
    public void onPickup(InventoryPickupItemEvent e) {
        if (e.getInventory().getType() != InventoryType.HOPPER) {
            return;
        }
        ImmortalHoppers.getInstance().getHoppers().values().stream().filter(x -> x.getHopper().getInventory().getHolder().equals(e.getInventory().getHolder())).findFirst().ifPresent(x -> {
            if (x.getBlacklist().contains(e.getItem().getItemStack().getType()) || e.getItem().getItemStack().getItemMeta().hasDisplayName()) {
                e.setCancelled(true);
            }
        });


    }
}
