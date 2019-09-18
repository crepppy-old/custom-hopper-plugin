package net.immortalmc.hoppers.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemManager {
    public static ItemStack mobHopper = createItem(Material.HOPPER, "&6Mob Hopper", "Sucks up mob drops in the same chunk");
    public static ItemStack cropHopper = createItem(Material.HOPPER, "&6Crop Hopper", "Sucks up crop drops in the same chunk");

    private ItemManager() {
    }

    public static ItemStack createItem(Material material, String name, String... lore) {
        return new ItemGenerator(material).setName(name).setLore(lore).build();
    }
}
