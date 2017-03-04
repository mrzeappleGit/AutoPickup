package me.MnMaxon.AutoPickup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import haveric.stackableItems.util.InventoryUtil;

public class Util
{

    public static void warn(Player p)
    {
        if (Config.warnOnFull
            && p != null
            && p.isValid()
            && ( ! AutoPickupPlugin.warnCooldown.containsKey(p.getName()) || AutoPickupPlugin.warnCooldown.get(p.getName()) < Calendar.getInstance().getTimeInMillis()))
        {
            p.sendMessage(Message.ERROR0FULL_INVENTORY + "");
            AutoPickupPlugin.warnCooldown.put(p.getName(), 5000 + Calendar.getInstance().getTimeInMillis());
        }
    }

    public static HashMap < Integer, ItemStack > giveItem(Player p, Inventory inv, ItemStack is)
    {
        if (is == null)
        {
            return new HashMap <> ();
        }

        if ( ! Config.usingStackableItems || p == null)
        {
            return inv.addItem(is);
        }
        ItemStack toSend = is.clone();
        ItemStack remaining = null;
        int freeSpaces = InventoryUtil.getPlayerFreeSpaces(p, toSend);
        if (freeSpaces < toSend.getAmount())
        {
            remaining = toSend.clone();
            remaining.setAmount(toSend.getAmount() - freeSpaces);
            toSend.setAmount(freeSpaces);
        }

        if (toSend.getAmount() > 0)
        {
            InventoryUtil.addItemsToPlayer(p, toSend, "pickup");
        }
        HashMap < Integer, ItemStack > map = new HashMap <> ();
        if (remaining != null)
        {
            map.put(0, remaining);
        }
        return map;
    }

    public static HashMap < Integer, ItemStack > giveItem(Player p, ItemStack is)
    {
        return giveItem(p, p.getInventory(), is);
    }

    public static ItemStack easyItem(String name, Material material, int amount, int durability, String... lore)
    {
        ItemStack is = new ItemStack(material);
        if (durability > 0)
        {
            is.setDurability((short)durability);
        }

        if (amount > 1)
        {
            is.setAmount(amount);
        }

        if (is.getItemMeta() != null)
        {
            ItemMeta im = is.getItemMeta();
            if (name != null)
            {
                im.setDisplayName(name);
            }

            if (lore != null)
            {
                ArrayList < String > loreList = new ArrayList <> ();
                Collections.addAll(loreList, lore);
                im.setLore(loreList);
            }
            is.setItemMeta(im);
        }
        return is;
    }

}