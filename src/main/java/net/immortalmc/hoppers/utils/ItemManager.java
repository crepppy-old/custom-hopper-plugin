package net.immortalmc.hoppers.utils;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.ImmortalHoppers;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ItemManager {
    public static ItemStack mobHopper = createItem(Material.HOPPER, "&6Mob Hopper", "Sucks up mob drops in the same chunk");
    public static ItemStack cropHopper = createItem(Material.HOPPER, "&6Crop Hopper", "Sucks up crop drops in the same chunk");

    private ItemManager() {
    }

    public static Hopper getHopperFromItem(ItemStack item) {
        //Get the location of the previous inventories hopper
        // (probably an easier way but I have a habit of over-complicating things)
        String locationStr = ChatColor.stripColor(item.getItemMeta().getLore().get(0));
        List<String> coords = Pattern.compile("([A-Za-z]+: (-?[A-Za-z0-9]*),?)").matcher(locationStr).results().map(x -> x.group(2)).collect(Collectors.toList());
        Location location = new Location(Bukkit.getServer().getWorld(coords.get(0)), Integer.parseInt(coords.get(1)), Integer.parseInt(coords.get(2)), Integer.parseInt(coords.get(3)));
        Hopper hopper = ImmortalHoppers.getInstance().getHoppers().get(location);

        //This shouldn't be called unless a hopper is destroyed when a person is in the inventory
        if (hopper == null) {
            ImmortalHoppers.getInstance().getHoppers().put(location, hopper = new Hopper(location, 1, null));
        }

        return hopper;
    }

    private static ItemStack generateHopperItem(Hopper hopper) {
        return new ItemGenerator(Material.HOPPER).setName("&eHopper").setLore(String.format("World: %s, X: %s, Y: %s, Z: %s", hopper.getLocation().getWorld().getName(), hopper.getLocation().getBlockX(), hopper.getLocation().getBlockY(), hopper.getLocation().getBlockZ())).build();
    }

    public static ItemStack createItem(Material material, String name, String... lore) {
        return new ItemGenerator(material).setName(name).setLore(lore).build();
    }

    public static Inventory getMenu(Hopper hopper) {
        String title = "Hopper » ";
        if (hopper.getLevel() == Hopper.Level.SPECIAL) {
            title += hopper.getSpecial() + " Hopper";
        } else {
            title += "Level " + WordUtils.capitalizeFully(hopper.getLevel().toString());
        }
        Inventory inventory = Bukkit.createInventory(null, 27, title);
        //Change the item name in the menu if it's not unlocked (cosmetic)
        if (hopper.getLevel().getNumber() > 2 || hopper.getLevel() == Hopper.Level.SPECIAL) {
            inventory.setItem(10, new ItemGenerator(Material.CHEST).setName("&eChest Link").setLore("Link the hopper to a remote chest").build());
        } else {
            inventory.setItem(10, new ItemGenerator(Material.CHEST).setName("&e&kChest Link").setLore("&cRequired Level 3").build());
        }
        if (hopper.getLevel() == Hopper.Level.SPECIAL) {
            inventory.setItem(13, new ItemGenerator(Material.HOPPER).setName("&6Upgrades").setLore("&cUnavailable for this hopper!").build());
        } else {
            inventory.setItem(13, new ItemGenerator(Material.HOPPER).setName("&6Upgrades").setLore("Upgrade the hopper").build());
        }
        if (hopper.getLevel().getNumber() > 1 || hopper.getLevel() == Hopper.Level.SPECIAL) {
            inventory.setItem(16, new ItemGenerator(Material.RED_ROSE).setName("&cFilter").setLore("Blacklists items").build());
        } else {
            inventory.setItem(16, new ItemGenerator(Material.RED_ROSE).setName("&c&kFilter").setLore("&cRequired Level 2").build());
        }

        inventory.setItem(26, generateHopperItem(hopper));
        //Fill empty space
        for (int i = 0; i < 27; i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, new ItemGenerator(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).setName(" ").build());
            }
        }
        return inventory;
    }

    public static Inventory getUpgrades(Hopper hopper) {
        Inventory inventory = Bukkit.createInventory(null, 9, "Upgrades");
        inventory.setItem(0, generateHopperItem(hopper));
        for (int i = 1; i <= 5; i++) {
            //GREEN: 5, RED: 14
            Hopper.Level l = Hopper.Level.getLevel(i);
            ItemGenerator item = new ItemGenerator(new ItemStack(Material.STAINED_GLASS_PANE, i)).setName("Upgrade " + i).setLore("Price: &6$" + l.getPrice(), "Speed: &6" + (Math.round(20.0 / l.getSpeed() * 10) / 10) + " items / second", "Blacklist: &6" + l.getMaxBlacklist() + " items").setDurability(14);
            //Set the glass to green if it has already been purchased
            if (hopper.getLevel().getNumber() >= i) {
                item.setDurability(5);
            }
            inventory.setItem(i + 1, item.build());
        }
        inventory.setItem(1, new ItemGenerator(Material.STAINED_GLASS_PANE).setName(" ").build());
        inventory.setItem(7, new ItemGenerator(Material.STAINED_GLASS_PANE).setName(" ").build());
        inventory.setItem(8, new ItemGenerator(Material.STAINED_GLASS_PANE).setName(" ").build());
        return inventory;
    }

    public static Inventory getBlacklist(Hopper hopper) {
        Inventory inventory = Bukkit.createInventory(null, 27, "Blacklist");
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, new ItemGenerator(Material.STAINED_GLASS_PANE).setName(" ").build());
            if (i == 8) {
                i = 17;
            }
        }
        inventory.setItem(26, generateHopperItem(hopper));
        //Add blacklisted items
        if (hopper.getBlacklist() != null) {
            hopper.getBlacklist().stream().map(ItemStack::new).forEach(inventory::addItem);
        }
        //Add barriers to the remaining slots
        for (int i = 9 + hopper.getLevel().getMaxBlacklist(); i < 18; i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, new ItemGenerator(Material.BARRIER).setName("&cUnlock this slot by upgrading the hopper").build());
            }
        }
        return inventory;
    }
}
