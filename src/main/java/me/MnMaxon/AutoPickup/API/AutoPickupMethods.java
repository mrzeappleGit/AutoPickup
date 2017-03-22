package me.MnMaxon.AutoPickup.API;

import me.MnMaxon.AutoPickup.AutoBlock;
import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.AutoResult;
import me.MnMaxon.AutoPickup.AutoSmelt;
import me.MnMaxon.AutoPickup.Config;
import me.MnMaxon.AutoPickup.SuperLoc;
import me.MnMaxon.AutoPickup.Util;
import me.MnMaxon.AutoPickup.commands.Common;

import me.mrCookieSlime.QuickSell.Shop;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class AutoPickupMethods
{
    public static void openGui(Player player)
    {
        Common.openGui(player);
    }

    public static void autoGive(Player player, ItemStack item)
    {

        if (AutoPickupPlugin.autoBlock.contains(player.getName()))
        {
            //start by autoblocking the player inventory
            blockInventory(player, false);
        }

        if (AutoPickupPlugin.autoSell.contains(player.getName()))
        {
            double highestPrice = 0;
            Shop highestShop = null;
            for (Shop shop : Shop.list())
            {
                if (shop.hasUnlocked(player) && shop.getPrices().getPrice(item) > highestPrice)
                {
                    highestShop = shop;
                }
            }

            if (highestShop != null)
            {
                highestShop.sell(player, true, item);
                return;
            }
        }

        if (AutoPickupPlugin.autoSmelt.contains(player.getName()))
        {
            item = smelt(item).getNewItem();
        }

        if (AutoPickupPlugin.autoPickup.contains(player.getName()))
        {
            ArrayList<ItemStack> items = new ArrayList<>();
            items.add(item);
            DropToInventoryEvent die = new DropToInventoryEvent(player, items);
            Bukkit.getServer().getPluginManager().callEvent(die);
            Collection<ItemStack> remaining = new ArrayList<>();

            if (die.isCancelled())
            {
                SuperLoc.superLocs.remove(die.getPlayer().getLocation().getBlock().getLocation());
                for (ItemStack spawn : die.getItems())
                {
                    player.getWorld().dropItem(player.getLocation(), spawn);
                }

                return;

            } else
            {
                for (ItemStack give : die.getItems())
                {
                    if (AutoPickupPlugin.autoBlock.contains(player.getName()))
                    {
                        remaining.addAll(AutoBlock.addItem(player, give).values());
                    } else
                    {
                        remaining.addAll(Util.giveItem(player, give).values());
                    }
                }
            }

            if (!remaining.isEmpty())
            {
                if (!die.isCancelled())
                {
                    Util.warn(player);
                }

                if (!Config.deleteOnFull)
                {
                    for (ItemStack is : remaining)
                    {
                        player.getWorld().dropItem(player.getLocation(), is);
                    }
                }
            }
        }
    }

    public static AutoResult smelt(ItemStack item)
    {
        return AutoSmelt.smelt(item);
    }


    @Deprecated
    public static void smeltInventory(Player player)
    {
        AutoSmelt.smelt(player);
    }

    @Deprecated
    public static void blockInventory(Player player)
    {
        AutoBlock.block(player);
    }

    public static void blockInventory(Player player, boolean notify)
    {
        AutoBlock.block(player, notify);
    }

    public static void addBlockedItem(Player player, ItemStack is)
    {
        AutoBlock.addItem(player, is);
    }
}
