package net.immortalmc.hoppers.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ItemGenerator {
    //TODO move this class into the API
    //Acts as an itemstack builder and allows for method chaining to create items in one line
    private ItemStack item;
    private ItemMeta meta;

    public ItemGenerator(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemGenerator(ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemGenerator setName(String name) {
        meta.setDisplayName(ChatColor.RESET + ChatColor.translateAlternateColorCodes('&', name));
        return this;
    }

    public ItemGenerator setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemGenerator setLore(String... lore) {
        meta.setLore(Arrays.stream(lore)
                .map(x -> ChatColor.RESET + "" + ChatColor.GRAY + ChatColor.translateAlternateColorCodes('&', x))
                .collect(Collectors
                        .toList()));
        return this;
    }

    public ItemGenerator setDurability(int data) {
        item.setDurability((short) data);
        return this;
    }

    public ItemGenerator setGlisten() {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
