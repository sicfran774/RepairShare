package io.sicfran.repairShare.listeners;

import io.sicfran.repairShare.RepairShare;
import io.sicfran.repairShare.helpers.Mending;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelfRepair implements Listener {
    private final RepairShare plugin;
    private final List<UUID> sneakingPlayers = new ArrayList<>();

    public SelfRepair(RepairShare plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event){
        UUID playerId = event.getPlayer().getUniqueId();
        if(event.isSneaking()) sneakingPlayers.add(playerId);
        else sneakingPlayers.remove(playerId);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        //check permissions
        if(!plugin.ALLOW_SELF_REPAIR || !player.hasPermission("repair.self_mend")) return;

        //Ignore players who opted out
        if(plugin.getIgnoredPlayers().contains(player.getUniqueId())) return;

        // Ignore players that aren't sneaking and/or not holding something in their hand
        if(!sneakingPlayers.contains(player.getUniqueId())) return;
        if(!(event.getHand() == EquipmentSlot.HAND)) return;

        PlayerInventory inventory = player.getInventory();
        ItemStack itemInHand = inventory.getItemInMainHand();

        if(!Mending.isMending(itemInHand.getItemMeta())) return;

        Damageable itemMeta = (Damageable) itemInHand.getItemMeta();

        if(!itemMeta.hasDamage()) return;
        if(player.getTotalExperience() <= 0) return; //Prevent repairing if no xp

        repairItemWithXp(itemInHand, itemMeta, player);
    }

    private void repairItemWithXp(ItemStack item, Damageable repairItem, Player player){
        int currentXp = player.getTotalExperience();

        // Do not consume more than what they have
        int xpAmountTaken = Math.max(
                -currentXp,
                -plugin.REPAIR_COST
        );

        int xpAmountGiven = (xpAmountTaken == -currentXp) ? currentXp : plugin.SELF_REPAIR_AMOUNT;

        player.giveExp(xpAmountTaken);

        player.swingMainHand();
        new Mending(plugin).repairItem(item, repairItem, xpAmountGiven, player);
    }
}
