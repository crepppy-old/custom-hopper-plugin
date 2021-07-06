package net.immortalmc.hoppers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Hopper {
    private Location location;
    private org.bukkit.block.Hopper hopper;
    private Level level;
    private String special;
    private List<Material> blacklist;
    private Location customInventory;
    private InventoryHolder inventory;

    public Hopper(Location location, int level, InventoryHolder inventory) {
        this.location = location;
        this.level = Level.getLevel(level);
        this.special = "";
        this.hopper = (org.bukkit.block.Hopper) location.getBlock().getState();
        this.blacklist = new ArrayList<>();
        this.inventory = inventory;
    }

    public Hopper(Location location, int level, String special, List<Material> blacklist, Location customInventory) {
        this.location = location;
        this.level = Level.getLevel(level);
        this.special = special;
        this.hopper = (org.bukkit.block.Hopper) location.getBlock().getState();
        this.inventory = null;
        this.blacklist = blacklist;
        this.customInventory = customInventory;
    }

    public org.bukkit.block.Hopper getHopper() {
        return hopper;
    }

    public String getSpecial() {
        return special;
    }

    public void tick() {
        InventoryHolder inventory = this.customInventory == null ? this.inventory : (InventoryHolder) this.customInventory.getBlock().getState();
        try {
            //If the hopper is a special hopper loop through dropped items in the same chunk
            //If an item is compatible with the hopper
            //Add the item to the hopper and remove from the ground
            if (level == Level.SPECIAL) {
                Arrays.stream(hopper.getChunk().getEntities()).filter(e -> e.getType().equals(EntityType.DROPPED_ITEM)).forEach(e -> {
                    Item item = (Item) e;
                    if (ImmortalHoppers.getInstance().getConfig().getStringList(special.toLowerCase()).contains(item.getItemStack().getType().toString().toUpperCase())) {
                        if (!hopper.getInventory().addItem(item.getItemStack()).containsValue(item.getItemStack())) {
                            e.remove();
                        }
                    }
                });
                Arrays.stream(hopper.getChunk().getEntities()).filter(x -> x.getType() == EntityType.DROPPED_ITEM).filter(x -> ImmortalHoppers.getInstance().getConfig().getStringList(special.toLowerCase()).contains(((Item) x).getItemStack().getType().toString().toUpperCase())).forEach(x -> {
                    Item droppedItem = (Item) x;
                    if (!hopper.getInventory().addItem(droppedItem.getItemStack()).containsValue(droppedItem.getItemStack())) {
                        x.remove();
                    }
                });
            }
            if (inventory != null) {
                if (hopper.getInventory().getContents() != null) {
                    //Get the contents of the hopper, removing null values
                    List<ItemStack> contents = Arrays.stream(hopper.getInventory().getContents()).filter(Objects::nonNull).collect(Collectors.toList());
                    if (contents.size() == 0) {
                        return;
                    }
                    //Get the 1 of the first item in the hopper and places it into the target inventory
                    ItemStack item = contents.get(0).clone();
                    contents.get(0).setAmount(item.getAmount() - 1);
                    item.setAmount(1);

                    //Remove blacklisted items
                    if (blacklist != null && blacklist.contains(item.getType())) {
                        hopper.getLocation().getWorld().dropItem(hopper.getLocation().add(0, 1, 0), item);
                        return;
                    } else {
                        if (!inventory.getInventory().addItem(item).isEmpty()) {
                            return;
                        }
                    }
                    //Replace the current inventory of the hopper, with the new one that doesn't have null values
                    //  and has 1 less item
                    hopper.getInventory().clear();
                    hopper.getInventory().addItem(contents.toArray(ItemStack[]::new));
                }
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

    public Location getCustomInventory() {
        return customInventory;
    }

    public void setCustomInventory(Location customInventory) {
        this.customInventory = customInventory;
    }

    public List<Material> getBlacklist() {
        return blacklist;
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
            return ImmortalHoppers.getInstance().getConfig().getDouble("upgrades." + this.toString().toUpperCase() + ".price");
        }

        public int getMaxBlacklist() {
            //Gets the maximum number of blacklisted items the hopper can have
            return ImmortalHoppers.getInstance().getConfig().getInt("upgrades." + this.toString().toUpperCase() + ".blacklist");
        }
    }
}
