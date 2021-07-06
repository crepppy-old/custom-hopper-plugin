package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.IHopperCommand;
import net.immortalmc.hoppers.ImmortalHoppers;
import net.immortalmc.hoppers.utils.ItemGenerator;
import net.immortalmc.hoppers.utils.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        try {
            if (e.getClickedInventory().getHolder() == null && e.getClickedInventory().getTitle().startsWith("Hopper » ")) {
                Hopper hopper = ItemManager.getHopperFromItem(e.getClickedInventory().getItem(26));
                e.setCancelled(true);
                if (clickedItem.equals(new ItemGenerator(Material.HOPPER).setName("&6Upgrades").setLore("Upgrade the hopper").build())) {
                    e.getWhoClicked().openInventory(ItemManager.getUpgrades(hopper));
                }
                if (clickedItem.equals(new ItemGenerator(Material.CHEST).setName("&eChest Link").setLore("Link the hopper to a remote chest").build())) {
                    //Add players to a hashmap to register the next block they interact with
                    ImmortalHoppers.getInstance().getCustomInventoryMode().put((Player) e.getWhoClicked(), hopper);
                    e.getWhoClicked().closeInventory();
                    IHopperCommand.prefixedMessage((Player) e.getWhoClicked(), ChatColor.GRAY + "Please click the block you wish to connect to the hopper");
                }
                if (clickedItem.equals(new ItemGenerator(Material.RED_ROSE).setName("&cFilter").setLore("Blacklists items").build())) {
                    e.getWhoClicked().openInventory(ItemManager.getBlacklist(hopper));
                }
            } else if (e.getClickedInventory().getHolder() == null && e.getClickedInventory().getTitle().equalsIgnoreCase("Upgrades")) {
                Hopper hopper = ItemManager.getHopperFromItem(e.getClickedInventory().getItem(0));
                e.setCancelled(true);
                //Purchase the upgrade
                Hopper.Level upgrade = Hopper.Level.getLevel(Integer.parseInt(clickedItem.getItemMeta().getDisplayName().substring(10)));
                if (upgrade.getNumber() == hopper.getLevel().getNumber() + 1) {
                    if (ImmortalHoppers.getInstance().getEconomy().withdrawPlayer((OfflinePlayer) e.getWhoClicked(), upgrade.getPrice()).transactionSuccess()) {
                        IHopperCommand.prefixedMessage((Player) e.getWhoClicked(), String.format("Successfully upgraded hopper to &6%s &8for &6%s", upgrade.getNumber(), ImmortalHoppers.getInstance().getEconomy().format(upgrade.getPrice())));
                        hopper.setLevel(upgrade);
                        e.getWhoClicked().openInventory(ItemManager.getUpgrades(hopper));
                    } else {
                        IHopperCommand.prefixedMessage((Player) e.getWhoClicked(), ChatColor.RED + "You cannot afford this");
                    }
                } else {
                    IHopperCommand.prefixedMessage((Player) e.getWhoClicked(), ChatColor.RED + "Please buy the hopper upgrades in order");
                }
            } else if (e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("Blacklist")) {
                Hopper hopper = ItemManager.getHopperFromItem(e.getWhoClicked().getOpenInventory().getTopInventory().getItem(26));
                e.setCancelled(true);
                if (e.getClickedInventory().equals(e.getWhoClicked().getOpenInventory().getBottomInventory())) { //Player's inventory
                    //Alert the player if the item should not be whitelisted
                    if (ImmortalHoppers.getInstance().getConfig().getStringList("blacklist").contains(clickedItem.getType().toString().toUpperCase())) {
                        IHopperCommand.prefixedMessage((Player) e.getWhoClicked(), ChatColor.RED + "You cannot blacklist this item");
                        return;
                    }
                    //Add the item to the blacklist if possible
                    if (e.getWhoClicked().getOpenInventory().getTopInventory().addItem(new ItemStack(clickedItem.getType())).isEmpty() && !hopper.getBlacklist().contains(clickedItem.getType())) {
                        hopper.getBlacklist().add(clickedItem.getType());
                    }
                } else {
                    //Remove the item from the blacklist
                    if (!(clickedItem.equals(new ItemGenerator(Material.BARRIER).setName("&cUnlock this slot by upgrading the hopper").build()) && clickedItem.equals(new ItemGenerator(Material.STAINED_GLASS_PANE).setName(" ").build()))) {
                        hopper.getBlacklist().remove(clickedItem.getType());
                    }
                }
                //Update the view
                e.getWhoClicked().openInventory(ItemManager.getBlacklist(hopper));
            }
        } catch (NullPointerException ignored) {
            //Player has renamed a chest to a reserved name
            ignored.printStackTrace();
        }
    }
}
