package me.MnMaxon.AutoPickup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;

/**
 * Created by MnMaxon on 5/26/2015.  Aren't I great?
 */
public class AutoSmelt {
    public static AutoResult smelt(ItemStack is) {
        if (is == null) return new AutoResult(null, null, false);
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            if (!(recipe instanceof FurnaceRecipe)) continue;
            if (((FurnaceRecipe) recipe).getInput().getType() != is.getType()) continue;
            ItemStack newItem = recipe.getResult();
            if ((!AutoPickupPlugin.smeltList.isEmpty() && !AutoPickupPlugin.smeltList.contains(is.getType().name())) ||
                    (AutoPickupPlugin.smeltBlacklist.containsKey(newItem.getType()) && (AutoPickupPlugin.smeltBlacklist.get(newItem.getType()) < 0
                            || AutoPickupPlugin.smeltBlacklist.get(newItem.getType()) == newItem.getDurability())))
                return new AutoResult(is, is, false);
            newItem.setAmount(is.getAmount());
            return new AutoResult(newItem, is, true);
        }
        return new AutoResult(is, is, false);
    }


    public static void smelt(Player p, boolean notify)
    {
        if (p == null || !p.isValid())
        {
            return;
        }

        boolean changed = false;
        ItemStack[] cont = p.getInventory().getContents();
        for (int i = 0; i < cont.length; i++) 
        {
            AutoResult result = smelt(cont[i]);
            if (result.isChanged()) {
                changed = true;
                p.getInventory().removeItem(cont[i]);
                p.getInventory().addItem(result.getNewItem());
                p.updateInventory();
            }
        }

        if (changed) 
        {
            if(notify)
            {
                p.sendMessage(Message.SUCCESS0SMELTED_INVENTORY + "");
            }
        } else 
        {
            if(notify)
            {
                p.sendMessage(Message.ERROR0SMELTED_INVENTORY + "");
            }
        }
    }

    public static void smelt(Player p) {
        if (p == null || !p.isValid()) return;
        boolean changed = false;
        ItemStack[] cont = p.getInventory().getContents();
        for (int i = 0; i < cont.length; i++) {
            AutoResult result = smelt(cont[i]);
            if (result.isChanged()) {
                changed = true;
                cont[i] = result.getNewItem();
            }
        }
        if (changed) {
            p.getInventory().setContents(cont);
            p.updateInventory();
            p.sendMessage(Message.SUCCESS0SMELTED_INVENTORY + "");
        } else p.sendMessage(Message.ERROR0SMELTED_INVENTORY + "");
    }
}
