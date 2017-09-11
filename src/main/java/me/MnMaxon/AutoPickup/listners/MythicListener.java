package me.MnMaxon.AutoPickup.listners;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Config;
import me.MnMaxon.AutoPickup.SuperLoc;
import org.bukkit.Bukkit;
import org.bukkit.Location; 
import org.bukkit.entity.Player; 
import org.bukkit.entity.Projectile; 
import org.bukkit.event.EventHandler; 
import org.bukkit.event.EventPriority; 
import org.bukkit.event.Listener; 
import org.bukkit.event.entity.EntityDamageByEntityEvent; 
import org.bukkit.event.entity.EntityDeathEvent; 

import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 

/**
 * Created by MnMaxon on 6/7/2015. */
@SuppressWarnings("ALL")
public class MythicListener implements Listener
{

    private final Map < Integer, String > damageMap = new HashMap <> ();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onKill(EntityDeathEvent e) 
    {
        int id = e.getEntity().getEntityId(); 
        Player killer = null; 
        if (damageMap.containsKey(id))
        {
            killer = Bukkit.getPlayer(damageMap.get(id)); 
        }

        if (killer == null || Config.getBlockedWorlds().contains(killer.getWorld()))
        {
            return; 
        }

        List < Location > locs = new ArrayList <> (); 
        Location loc = e.getEntity().getLocation(); 
        locs.add(loc); 
        locs.add(loc.clone().add(0, 0, .5)); 
        locs.add(loc.clone().add(0, 0,  -.5)); 
        locs.add(loc.clone().add(.5, 0, 0)); 
        locs.add(loc.clone().add( -.5, 0, 0)); 
        locs.add(loc); 
        locs.add(loc); 

        for (Location location:locs)
        {
            SuperLoc.add(location, killer, true, AutoPickupPlugin.autoSmelt.contains(killer.getName()), AutoPickupPlugin.autoBlock.contains(killer.getName()), null);
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if ( ! Config.autoMob || e.getEntity()instanceof Player)
        {
            return; 
        }
        Player damager;

        if (e.getDamager()instanceof Player)
        {
            damager = (Player)e.getDamager(); 
        }else if (e.getDamager()instanceof Projectile && ((Projectile)e.getDamager()).getShooter()instanceof Player)
        {
            damager = (Player)((Projectile)e.getDamager()).getShooter(); 
        }else 
        {
            return; 
        }
        
        damageMap.put(e.getEntity().getEntityId(), damager.getName()); 
    }
}
