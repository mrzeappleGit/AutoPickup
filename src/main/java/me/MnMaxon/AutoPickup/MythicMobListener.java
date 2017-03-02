package me.MnMaxon.AutoPickup; 

import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent; 
import org.bukkit.entity.Player; 
import org.bukkit.event.EventHandler; 
import org.bukkit.event.Listener; 
import org.bukkit.inventory.ItemStack; 

import java.util.ArrayList; 
import java.util.HashMap; 

/**
 * Created by MnMaxon on 2/16/2016.  Aren't I great?
 */
public class MythicMobListener implements Listener 
{

    @EventHandler(ignoreCancelled = true)
    public void onKill(MythicMobDeathEvent e)
    {
        if (e.getKiller() == null || !(e.getKiller() instanceof Player)) 
        {
            return;
        }

        Player killer = (Player) e.getKiller();

        if (Config.getBlockedWorlds().contains(killer.getWorld()))
        {
            return;
        }

        if (Config.autoMob)
        {
            ArrayList<ItemStack> newDrops = new ArrayList<>();
            for (ItemStack drop : e.getDrops()) 
            {
                HashMap<Integer, ItemStack> remaining = killer.getInventory().addItem(drop);
                for (ItemStack remainder : remaining.values())
                {
                    newDrops.add(remainder);
                }
            }

            if (!newDrops.isEmpty())
            {
                Util.warn(killer);
            }

            e.getDrops().clear();
            if (!Config.deleteOnFull) 
            {
                for (ItemStack is : newDrops)
                {
                    e.getDrops().add(is);
                }
            }
        }
        
        if (Config.autoMobXP) {
            killer.giveExp(e.getExp());
            e.setExp(0);
        }
    }
}
