package net.immortalmc.hoppers;

import net.immortalmc.hoppers.utils.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IHopperCommand implements CommandExecutor {
    public static void prefixedMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lImmortalMC &8» " + message));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        //If not otherwise stated, set the target as the player
        Player target, p;
        target = p = (Player) sender;
        ItemStack item = null;
        if (args.length == 0) {
            prefixedMessage(p, "&cIncorrect Usage: /ihoppers <player> <type> <amount>");
            return true;
        }
        if (args.length == 1) {
            //Assume argument is hopper type: /ihoppers mob
            if (args[0].equalsIgnoreCase("mob")) {
                item = ItemManager.mobHopper;
            } else if (args[0].equalsIgnoreCase("crop")) {
                item = ItemManager.cropHopper;
            } else {
                prefixedMessage(p, "&cIncorrect hopper type: mob, crop");
                return true;
            }
        } else {
            //Full Command: ihoppers crepppy mob 1
            target = Bukkit.getOnlinePlayers().stream().filter(x -> x.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
            if (args[1].equalsIgnoreCase("mob")) {
                item = ItemManager.mobHopper;
            } else if (args[1].equalsIgnoreCase("crop")) {
                item = ItemManager.cropHopper;
            } else {
                prefixedMessage(p, "&cIncorrect hopper type: mob, crop");
                return true;
            }
            if (target == null) {
                prefixedMessage(p, ChatColor.RED + "Please specify an online player");
                return true;
            } else {
                if (args.length > 2) {
                    item.setAmount(Integer.parseInt(args[2]));
                }
            }
        }
        target.getInventory().addItem(item);
        return true;
    }
}
