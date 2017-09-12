package me.MnMaxon.AutoPickup.actions;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Config;
import me.MnMaxon.AutoPickup.util.Util;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

//TODO: this class must Die!
public class LocationActions
{
    public static final HashMap < Location, LocationActions> superLocs = new HashMap <> ();
    private final Player p;
    private final boolean autoPickup;
    private final boolean autoSmelt;
    private final boolean autoBlock;
    private final ItemStack itemStack;

    private LocationActions(Player p, boolean autoPickup, boolean autoSmelt, boolean autoBlock, ItemStack is)
    {
        this.p = p;
        this.autoPickup = autoPickup;
        this.autoSmelt = autoSmelt;
        this.autoBlock = autoBlock;
        this.itemStack = is;
    }

    public static void add(Location loc, Player p, boolean autoPickup, boolean autoSmelt, boolean autoBlock, ItemStack is)
    {
        final Location location = loc.getBlock().getLocation();

        if (superLocs.containsKey(location))
        {
            superLocs.remove(location);
        }

        final LocationActions sl = new LocationActions(p, autoPickup, autoSmelt, autoBlock, is);
        superLocs.put(location, sl);

        //TODO: whats this achieve?
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("AutoPickup"), () -> {
            if (superLocs.containsKey(location) && superLocs.get(location).equals(sl))
            {
                superLocs.remove(location);
            }
        }, 10L);
    }

    //TODO: DIE IN A FIRE!
    public static boolean doAutoActions(Item item, Location exactLoc)
    {
        Location loc = exactLoc.getBlock().getLocation();

        if (item == null ||  ! superLocs.containsKey(loc))
        {
            return false;
        }

        LocationActions sl = superLocs.get(loc);

        if (sl == null ||  ! sl.p.isValid())
        {
            return false;
        }

        ItemStack is = item.getItemStack();

        if (Config.usingPrisonGems && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Gem"))
        {
            return false;
        }

        item = Util.doFortune(sl.p, item, sl.itemStack);

        if (AutoPickupPlugin.autoSell.contains(sl.p.getName()))
        {

            if (AutoSell.sell(sl.p, item.getItemStack()))
            {
                return true;
            }
        }

        if (sl.autoPickup)
        {
            return AutoPickup.pickup(sl.p, item.getItemStack());
        }
        return false;
    }
}
