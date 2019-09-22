package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.ImmortalHoppers;
import net.immortalmc.hoppers.utils.ItemGenerator;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HopperInteractEvent implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        /*If a player shift clicks on a hopper, don't open the inventory*/
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() != Material.HOPPER || e.getPlayer().isSneaking()) {
            return;
        }
        Hopper h = ImmortalHoppers.getInstance().getHoppers().stream().filter(x -> x.getLocation().equals(e.getClickedBlock().getLocation())).findFirst().orElse(null);
        if (h == null) {
            ImmortalHoppers.getInstance().getHoppers().add(h = new Hopper(e.getClickedBlock().getLocation(), 1, null));
        }
        e.setCancelled(true);
        String title = "Hopper » ";
        if (h.getLevel() == Hopper.Level.SPECIAL) {
            title += h.getSpecial() + " Hopper";
        } else {
            title += "Level " + WordUtils.capitalizeFully(h.getLevel().toString());
        }
        Inventory inventory = Bukkit.createInventory(null, 27, title);
        if (h.getLevel().getNumber() > 2) {
            inventory.setItem(10, new ItemGenerator(Material.CHEST).setName("&eChest Link").setLore("Link the hopper to a remote chest").build());
        } else {
            inventory.setItem(10, new ItemGenerator(Material.CHEST).setName("&e&kChest Link").setLore("&cRequired Level 3").build());
        }
        inventory.setItem(13, new ItemGenerator(Material.HOPPER).setName("&6Upgrades").setLore("Upgrade the hopper").build());
        if (h.getLevel().getNumber() > 1) {
            inventory.setItem(16, new ItemGenerator(Material.RED_ROSE).setName("&cFilter").setLore("Blacklists items").build());
        } else {
            inventory.setItem(16, new ItemGenerator(Material.RED_ROSE).setName("&c&kFilter").setLore("&cRequired Level 2").build());/*todo open inventory*/
        }
        for (int i = 0; i < 27; i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, new ItemGenerator(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName("").build());
            }
        }
        e.getPlayer().openInventory(inventory);
    }


}