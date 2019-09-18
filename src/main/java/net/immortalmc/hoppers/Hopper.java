package net.immortalmc.hoppers;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Hopper {
    private Location location;
    private org.bukkit.block.Hopper hopper;
    private Level level;
    private String special;
    private InventoryHolder inventory;

    public Hopper(Location location, Level level, InventoryHolder inventory) {
        this.location = location;
        this.level = level;
        this.special = "";
        this.hopper = (org.bukkit.block.Hopper) location.getBlock().getState();
        this.inventory = inventory;
    }

    public Hopper(Location location, int level, InventoryHolder inventory) {
        this.location = location;
        this.level = Level.getLevel(level);
        this.special = "";
        this.hopper = (org.bukkit.block.Hopper) location.getBlock().getState();
        this.inventory = inventory;
    }

    public Hopper(Location location, int level, InventoryHolder inventory, String special) {
        this.location = location;
        this.level = Level.getLevel(level);
        this.special = special;
        this.hopper = (org.bukkit.block.Hopper) location.getBlock().getState();
        this.inventory = inventory;
    }

    public String getSpecial() {
        return special;
    }

    public void tick() {
        try {
            if (inventory != null && hopper.getInventory().getContents() != null) {
                //Get the contents of the hopper, removing null values
                List<ItemStack> contents = Arrays.stream(hopper.getInventory().getContents()).filter(Objects::nonNull).collect(Collectors.toList());
                if(contents.size() == 0) return;
                //Get the 1 of the first item in the hopper and places it into the target inventory
                ItemStack item = contents.get(0).clone();
                contents.get(0).setAmount(item.getAmount() - 1);
                item.setAmount(1);
                inventory.getInventory().addItem(item);
                //Replace the current inventory of the hopper, with the new one that doesn't have null values
                //  and has 1 less item
                hopper.getInventory().clear();
                hopper.getInventory().addItem(contents.toArray(ItemStack[]::new));
            }
        } catch (IllegalStateException e) {
            this.inventory = null;
        }
    }

    public Location getLocation() {
        return location;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public InventoryHolder getInventory() {
        return inventory;
    }

    public void setInventory(InventoryHolder inventory) {
        this.inventory = inventory;
    }

    public enum Level {
        ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SPECIAL(-1);

        private int number;

        Level(int number) {
            this.number = number;
        }

        public static Level getLevel(int i) {
            switch (i) {
                case 2:
                    return TWO;
                case 3:
                    return THREE;
                case 4:
                    return FOUR;
                case 5:
                    return FIVE;
                case -1:
                    return SPECIAL;
                default:
                    return ONE;
            }
        }

        public int getNumber() {
            return number;
        }

        public int getSpeed() {
            //Get speed value from config
            return ImmortalHoppers.getInstance().getConfig()
                    .getInt("upgrades." + this.toString().toUpperCase() + ".speed");
        }

        public double getPrice() {
            //Gets price of upgrade from config
            return ImmortalHoppers.getInstance().getConfig().getDouble("upgrades." + number + ".price");
        }
    }
}
