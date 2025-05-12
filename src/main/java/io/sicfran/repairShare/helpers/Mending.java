package io.sicfran.repairShare.helpers;

import io.sicfran.repairShare.RepairShare;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class Mending {
    private final RepairShare plugin;

    public Mending(RepairShare plugin){
        this.plugin = plugin;
    }

    public static boolean isMending(ItemMeta item){
        if(item == null){
            return false;
        }
        return item.getEnchants().containsKey(Enchantment.MENDING);
    }

    public void repairItem(ItemStack item, Damageable repairItem, int xpAmount, Player player){
        // Prevent overflow
        repairItem.setDamage(Math.max(0, repairItem.getDamage() - (int) (xpAmount * plugin.XP_MULTIPLIER)));

        // Play special sound to differentiate from normal XP
        player.playSound(player.getLocation(), Sound.ITEM_BOTTLE_FILL, 1, 1);

        // Save changes
        item.setItemMeta(repairItem);
    }
}
