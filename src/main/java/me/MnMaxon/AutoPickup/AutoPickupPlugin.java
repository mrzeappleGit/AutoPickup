package me.MnMaxon.AutoPickup; 

import haveric.stackableItems.util.InventoryUtil;
import me.MnMaxon.AutoPickup.commands.AutoBlockCommand;
import me.MnMaxon.AutoPickup.commands.AutoPickup;
import me.MnMaxon.AutoPickup.commands.AutoSell;
import me.MnMaxon.AutoPickup.commands.AutoSmeltCommand;


import org.bukkit.Bukkit; 
import org.bukkit.Material; 
import org.bukkit.World; 
import org.bukkit.entity.Player; 
import org.bukkit.inventory.Inventory; 
import org.bukkit.inventory.ItemStack; 
import org.bukkit.inventory.meta.ItemMeta; 
import org.bukkit.plugin.java.JavaPlugin; 

import java.util.*; 

public final class AutoPickupPlugin extends JavaPlugin 
{
    
    public static String dataFolder; 
    public static AutoPickupPlugin plugin; 
    public static List < String > autoSmelt = new ArrayList <> ();
    public static List < String > autoPickup = new ArrayList <> ();
    public static List < String > autoBlock = new ArrayList <> ();
    public static List < String > autoSell = new ArrayList <> (); 
    public static List < String > fullNotify = new ArrayList <> (); 
    public static HashMap < String, Long > warnCooldown = new HashMap <> (); 
    public static HashMap < Material, Short > smeltBlacklist = new HashMap <> (); 
    public static Boolean allowBlockGui; 
    public static Boolean autoChest; 

    @Deprecated
    public static void reloadConfigs()
    {
        Config.reloadConfigs(); 
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

    @Override
    public void onDisable()
    {
        if (Config.fortuneData != null)
        {
            Config.fortuneData.save(); 
        }
    }

    @Override
    public void onEnable()
    {
        plugin = this; 
        Config.setConfigFolder(this.getDataFolder().getAbsolutePath()); 
        Config.reloadConfigs(); 

        getServer().getPluginManager().registerEvents(new MainListener(), this); 
        ArrayList < String > plugins = new ArrayList <> (); 

        this.getCommand("AutoPickup").setExecutor(new AutoPickup()); 
        this.getCommand("AutoSmelt").setExecutor(new AutoSmeltCommand());
        this.getCommand("AutoBlock").setExecutor(new AutoBlockCommand());
        this.getCommand("AutoSell").setExecutor(new AutoSell());
        //this.getCommand("FullNotify").setExecutor(new Commands());

        //Set up compatablility Listeners
        if (getServer().getPluginManager().getPlugin("MythicMobs") != null)
        {
            getServer().getPluginManager().registerEvents(new MythicMobListener(), this); 
        }
        if (getServer().getPluginManager().getPlugin("TokenEnchant") != null)
        {
            getServer().getPluginManager().registerEvents(new TokenEnchantListener(), this); 
        }
        if (getServer().getPluginManager().getPlugin("QuickSell") != null)
        {
            plugins.add("QuickSell"); 
            Config.usingQuickSell = true; 
        }
        if (getServer().getPluginManager().getPlugin("StackableItems") != null)
        {
            plugins.add("StackableItems"); 
            Config.usingStackableItems = true; 
        }
        if (getServer().getPluginManager().getPlugin("AutoSell") != null)
        {
            plugins.add("AutoSell"); 
            Config.usingAutoSell = true; 
        }
        if (getServer().getPluginManager().getPlugin("MythicDrops") != null)
        {
            plugins.add("MythicDrops"); 
            getServer().getPluginManager().registerEvents(new MythicListener(), this); 
        }
        if (getServer().getPluginManager().getPlugin("MyPet") != null)
        {
            plugins.add("MyPet"); 
            if ( ! plugins.contains("MythicDrops"))
            {
                getServer().getPluginManager().registerEvents(new MythicListener(), this); 
            }
        }
        if (getServer().getPluginManager().getPlugin("FortuneBlocks") != null)
        {
            plugins.add("FortuneBlocks"); 
            Config.usingCompat = true; 
        }
        if (getServer().getPluginManager().getPlugin("PrisonGems") != null)
        {
            plugins.add("PrisonGems"); 
            Config.usingPrisonGems = true; 
        }
        if ( ! plugins.isEmpty())
        {
            String message = "[AutoPickup] Detected you are using "; 
            for (String pName:plugins)
            {
                if ( ! message.endsWith(" "))
                {
                    message = message + ", "; 
                }
                message = message + pName; 
            }
            Bukkit.getLogger().info(message); 
        }
        
        for (Player p:Bukkit.getOnlinePlayers())
        {
            if (p.hasPermission("AutoPickup.enabled"))
            {
                AutoPickupPlugin.autoPickup.add(p.getName()); 
            }
            if (p.hasPermission("AutoBlock.enabled"))
            {
                AutoPickupPlugin.autoBlock.add(p.getName()); 
            }
            if (p.hasPermission("AutoSmelt.enabled"))
            {
                AutoPickupPlugin.autoSmelt.add(p.getName()); 
            }
            
            if (p.hasPermission("AutoSell.enabled") && Config.usingQuickSell)
            {
                AutoPickupPlugin.autoSell.add(p.getName()); 
            }
        }
    }
}