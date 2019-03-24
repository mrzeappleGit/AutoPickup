package me.MnMaxon.AutoPickup;

import me.MnMaxon.AutoPickup.commands.AutoBlockCommand;
import me.MnMaxon.AutoPickup.commands.AutoPickup;
import me.MnMaxon.AutoPickup.commands.AutoSmeltCommand;
import me.MnMaxon.AutoPickup.commands.FullNotify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.MnMaxon.AutoPickup.listners.MainListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoPickupPlugin extends JavaPlugin
{

    //TODO: move these
    public static final List < String > autoSmelt = new ArrayList<>();
    public static final List < String > autoPickup = new ArrayList<>();
    public static final List < String > autoBlock = new ArrayList<>();
    public static final List < String > fullNotify = new ArrayList<>();
    public static final HashMap < String, Long > warnCooldown = new HashMap<>();

    @Override
    public void onDisable()
    {
        Config.saveAll();
    }

    @Override
    public void onEnable()
    {
        Config.setConfigFolder(this.getDataFolder().getAbsolutePath());
        Config.reloadConfigs();

        getServer().getPluginManager().registerEvents(new MainListener(), this);

        ArrayList < String > plugins = new ArrayList <> ();

        this.getCommand("AutoPickup").setExecutor(new AutoPickup());
        this.getCommand("AutoSmelt").setExecutor(new AutoSmeltCommand());
        this.getCommand("AutoBlock").setExecutor(new AutoBlockCommand());
        this.getCommand("FullNotify").setExecutor(new FullNotify());

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
        }
    }
}