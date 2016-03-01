package me.MnMaxon.AutoPickup;

import com.vk2gpz.tokenenchant.event.TEBlockExplodeEvent;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by MnMaxon on 2/16/2016.  Aren't I great?
 */
public class TokenEnchantListener implements Listener {
    @EventHandler
    public void onTokenEnchantExplode(TEBlockExplodeEvent e) {
        ItemStack inhand = e.getPlayer().getItemInHand();
        if (AutoPickupPlugin.FortuneData != null) {
            String worldId = e.getBlock().getWorld().getUID().toString();
            List<String> list = AutoPickupPlugin.FortuneData.getStringList(worldId);
            String vecString = e.getBlock().getLocation().toVector().toString();
            if (list.contains(vecString)) {
                inhand = null;
                list.remove(vecString);
                AutoPickupPlugin.FortuneData.set(worldId, list);
            }
        }
        if (AutoPickupPlugin.getBlockedWorlds().contains(e.getPlayer().getWorld())) return;
        String name = e.getPlayer().getName();
        for (Block b : e.blockList())
            SuperLoc.add(b.getLocation(), e.getPlayer(), AutoPickupPlugin.autoPickup.contains(name), AutoPickupPlugin.autoSmelt.contains(name), AutoPickupPlugin.autoBlock.contains(name), inhand);
    }
}
