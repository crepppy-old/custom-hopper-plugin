package net.immortalmc.hoppers.listeners;

import net.immortalmc.hoppers.Hopper;
import net.immortalmc.hoppers.ImmortalHoppers;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.stream.Collectors;

public class HopperInteractEvent implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        //If a player shift clicks on a hopper, don't open the inventory
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock().getType() != Material.HOPPER || e.getPlayer().isSneaking()) return;
        Hopper h = ImmortalHoppers
                .getInstance().getHoppers().stream().filter(x -> x.getLocation().equals(e.getClickedBlock().getLocation())).findFirst().orElse(null);
        if(h == null) {
            ImmortalHoppers.getInstance().getHoppers()
                    .add(h = new Hopper(e.getClickedBlock().getLocation(), 1, null));
        }
        e.setCancelled(true);
        e.getPlayer().openInventory(Bukkit.createInventory(null, 9, "Hopper -> " + WordUtils.capitalize(h.getLevel().toString())));
        System.out.println(ImmortalHoppers.getInstance().getHoppers().stream().map(x -> x.getLevel().toString() + ": " + x.getLocation().getBlockX() + " " + x.getLocation().getBlockZ()).collect(Collectors
                .joining(", ")));

        //todo open inventory
    }
}
