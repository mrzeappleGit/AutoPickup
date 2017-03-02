package me.MnMaxon.AutoPickup;

import me.MnMaxon.AutoPickup.API.DropToInventoryEvent;
import me.mrCookieSlime.QuickSell.Shop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SuperLoc {
    public static HashMap<Location, SuperLoc> superLocs = new HashMap<>();
    private final Player p;
    private final boolean autoPickup;
    private final boolean autoSmelt;
    private final boolean autoBlock;
    private final ItemStack itemStack;

    private SuperLoc(Player p, boolean autoPickup, boolean autoSmelt, boolean autoBlock, ItemStack is) {
        this.p = p;
        this.autoPickup = autoPickup;
        this.autoSmelt = autoSmelt;
        this.autoBlock = autoBlock;
        this.itemStack = is;
    }

    public static void add(Location loc, Player p, boolean autoPickup, boolean autoSmelt, boolean autoBlock, ItemStack is) {
        final Location location = loc.getBlock().getLocation();
        if (superLocs.containsKey(location)) superLocs.remove(location);
        final SuperLoc sl = new SuperLoc(p, autoPickup, autoSmelt, autoBlock, is);
        superLocs.put(location, sl);
        Bukkit.getScheduler().scheduleSyncDelayedTask(AutoPickupPlugin.plugin, new Runnable() {
            @Override
            public void run() {
                if (superLocs.containsKey(location) && superLocs.get(location).equals(sl)) superLocs.remove(location);
            }
        }, 10L);
    }

    public static boolean doStuff(Item item, Location exactLoc) {
        Location loc = exactLoc.getBlock().getLocation();
        if (item == null || !superLocs.containsKey(loc)) return false;
        SuperLoc sl = superLocs.get(loc);
        if (sl == null || !sl.p.isValid()) return false;
        ItemStack is = item.getItemStack();
        if (Config.usingPrisonGems && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().contains("Gem"))
            return false;
        boolean fortunify = false;
        if (sl.autoSmelt) {
            item.setItemStack(AutoSmelt.smelt(is).getNewItem());
            if (Config.smeltFortune && Arrays.asList(Material.IRON_INGOT, Material.GOLD_INGOT).contains(item.getItemStack().getType()))
                fortunify = true;
        }
        if (Config.fortuneList.contains(item.getItemStack().getType())) fortunify = true;
        //For coal, diamond, emerald, nether quartz, and lapis lazuli, level I gives a 33% chance to multiply drops by 2
        // (averaging 33% increase), level II gives a chance to multiply drops by 2 or 3 (25% chance each, averaging 75% increase),
        // and level III gives a chance to multiply drops by 2, 3, or 4 (20% chance each, averaging 120% increase).
        if (fortunify && sl.itemStack != null && sl.itemStack.getEnchantments().containsKey(Enchantment.LOOT_BONUS_BLOCKS)) {
            int level = sl.itemStack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            int multiplier = new Random().nextInt(level + 2) + 1;
            item.getItemStack().setAmount(item.getItemStack().getAmount() * multiplier);
        }
        if (AutoPickupPlugin.autoSell.contains(sl.p.getName())) {
//            Shop shop = Shop.getHighestShop(sl.p);
            double highestPrice = 0;
            Shop highestShop = null;
            for (Shop shop : Shop.list())
                if (shop.hasUnlocked(sl.p) && shop.getPrices().getPrice(item.getItemStack()) > highestPrice)
                    highestShop = shop;
            if (highestShop != null) {
                highestShop.sell(sl.p, true, item.getItemStack());
                item.setItemStack(null);
                return true;
            }
        }
        if (sl.autoPickup) {
            ArrayList<ItemStack> items = new ArrayList<>();
            items.add(item.getItemStack());
            DropToInventoryEvent die = new DropToInventoryEvent(sl.p, items);
            Bukkit.getServer().getPluginManager().callEvent(die);
            Collection<ItemStack> remaining = new ArrayList<>();
            if (die.isCancelled()) {
                SuperLoc.superLocs.remove(loc);
                for (ItemStack spawn : die.getItems()) exactLoc.getWorld().dropItem(exactLoc, spawn);
                return true;
            } else for (ItemStack give : die.getItems()) {
                if (sl.autoBlock) remaining.addAll(AutoBlock.addItem(sl.p, give).values());
                else remaining.addAll(AutoPickupPlugin.giveItem(sl.p, give).values());
            }
            if (!remaining.isEmpty()) {
                if (!die.isCancelled()) AutoPickupPlugin.warn(sl.p);
                if (!Config.deleteOnFull) return false;
            }
            return true;
        }
        return false;
    }
}
