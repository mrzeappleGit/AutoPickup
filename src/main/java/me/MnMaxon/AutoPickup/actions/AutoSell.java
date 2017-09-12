/**
 * Copyright 2017 (c) Phillip Ledger <PLB Technology Group> and Contributors.
 * All rights reserved.
 * <p>
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 **/
package me.MnMaxon.AutoPickup.actions;

import me.mrCookieSlime.QuickSell.Shop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AutoSell
{

    public static boolean sell(Player player, ItemStack item)
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
            return true;
        }
        return false;
    }
}
