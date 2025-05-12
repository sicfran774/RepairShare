package io.sicfran.repairShare.listeners;

import io.sicfran.repairShare.RepairShare;
import io.sicfran.repairShare.helpers.Mending;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class OnExpChange implements Listener {
    private final RepairShare plugin;

    public OnExpChange(RepairShare plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemMend(PlayerItemMendEvent event){
        Player player = event.getPlayer();

        //Ignore players who opted out
        if(plugin.getIgnoredPlayers().contains(player.getUniqueId())) return;

        ItemStack item = event.getItem();
        Damageable itemMeta = (Damageable) item.getItemMeta();

        event.setRepairAmount(Math.min(itemMeta.getDamage(), (int) (event.getExperienceOrb().getExperience() * plugin.XP_MULTIPLIER)));
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event){
        Player player = event.getPlayer();

        //Ignore players who opted out
        if(plugin.getIgnoredPlayers().contains(player.getUniqueId())) return;
        // Check permissions (true by default)
        if(!player.hasPermission("repair.auto_mend")) return;

        ItemStack[] inventory = player.getInventory().getContents();

        List<Integer> repairableItems = new ArrayList<>();
        for(int i = 0; i < inventory.length; i++){
            ItemStack item = inventory[i];

            if(item == null) continue;

            ItemMeta itemMeta = item.getItemMeta();
            //This item has the Mending enchantment
            if(Mending.isMending(itemMeta)){
                Damageable itemDamageMeta = (Damageable) itemMeta;
                //This item can be repaired
                if(itemDamageMeta.hasDamage()) repairableItems.add(i);
            }
        }

        // No possible repairs, give xp as normal
        if(repairableItems.isEmpty()) return;

        // Repair an item with mending at random
        int xpAmount = event.getAmount();
        event.setAmount(0);

        int randomIndexFromItems = repairableItems.get(new Random().nextInt(repairableItems.size()));
        ItemStack item = inventory[randomIndexFromItems];
        Damageable repairItem = (Damageable) item.getItemMeta();
        if(repairItem == null) {
            plugin.getLogger().severe("Error getting item to repair");
            return;
        }

        new Mending(plugin).repairItem(item, repairItem, xpAmount, player);
    }
}
