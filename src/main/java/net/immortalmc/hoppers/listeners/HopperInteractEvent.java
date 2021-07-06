package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.IHopperCommand;
import net.immortalmc.hoppers.ImmortalHoppers;
import net.immortalmc.hoppers.utils.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class HopperInteractEvent implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (ImmortalHoppers.getInstance().getCustomInventoryMode().containsKey(e.getPlayer())) {
            try {
                ImmortalHoppers.getInstance().getCustomInventoryMode().get(e.getPlayer()).setCustomInventory(e.getClickedBlock().getLocation());
                ImmortalHoppers.getInstance().getCustomInventoryMode().remove(e.getPlayer());
                IHopperCommand.prefixedMessage(e.getPlayer(), "Set location as custom inventory");
            } catch (Exception ex) {
                ex.printStackTrace();
                IHopperCommand.prefixedMessage(e.getPlayer(), ChatColor.RED + "Please click on a valid hopper");
            }
        } else {
            //If a player shift clicks on a hopper, don't open the inventory
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() != Material.HOPPER || e.getPlayer().isSneaking()) {
                return;
            }
            Hopper h = ImmortalHoppers.getInstance().getHoppers().get(e.getClickedBlock().getLocation());
            if (h == null) {
                ImmortalHoppers.getInstance().getHoppers().put(e.getClickedBlock().getLocation(), h = new Hopper(e.getClickedBlock().getLocation(), 1, null));
            }
            e.setCancelled(true);
            e.getPlayer().openInventory(ItemManager.getMenu(h));
        }
    }


}