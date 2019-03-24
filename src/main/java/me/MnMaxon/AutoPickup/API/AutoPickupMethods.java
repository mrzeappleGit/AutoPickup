package me.MnMaxon.AutoPickup.API;

import me.MnMaxon.AutoPickup.actions.AutoBlock;
import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.actions.AutoPickup;
import me.MnMaxon.AutoPickup.util.AutoResult;
import me.MnMaxon.AutoPickup.actions.AutoSmelt;
import me.MnMaxon.AutoPickup.commands.Common;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
            blockInventory(player, false);
        }

        if (AutoPickupPlugin.autoSmelt.contains(player.getName()))
        {
            item = smelt(item).getNewItem();
        }

        if (AutoPickupPlugin.autoPickup.contains(player.getName()))
        {
            AutoPickup.pickup(player, item);
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
