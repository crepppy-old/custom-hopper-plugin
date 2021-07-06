package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.ImmortalHoppers;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class HopperMoveItemEvent implements Listener {
    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent e) {
        if (e.getSource().getType() != InventoryType.HOPPER) {
            return;
        }
        //Disable the vanilla hopper functionality
        e.setCancelled(true);
        Hopper block = (Hopper) e.getSource().getHolder();
        net.immortalmc.hoppers.Hopper hopper = ImmortalHoppers.getInstance().getHoppers().get(block.getLocation());
        //If the target inventory for a hopper is unknown, add the block the vanilla hopper tried to access
        if (hopper == null) {
            ImmortalHoppers.getInstance().getHoppers()
                    .put(block.getLocation(), new net.immortalmc.hoppers.Hopper(block.getLocation(), 1, e.getDestination().getHolder()));
        } else if (hopper.getInventory() == null) {
            hopper.setInventory(e.getDestination().getHolder());
        }
    }
}
