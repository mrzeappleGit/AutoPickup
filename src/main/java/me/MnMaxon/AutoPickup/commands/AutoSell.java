package me.MnMaxon.AutoPickup.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Config;
import me.MnMaxon.AutoPickup.util.Message;

public class AutoSell implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player p = (Player)sender;
        if ( ! Config.usingQuickSell)
        {
            p.sendMessage(Message.ERROR0NO_QUICKSELL + "");
            return true;
        }else if (args.length == 0)
        {
            p.sendMessage(ChatColor.RED + "Use like: /AutoSell toggle");
            return true;
        } else if (args[0].equalsIgnoreCase("toggle"))
        {
            if ( ! p.hasPermission("AutoSell.toggle"))
            {
                p.sendMessage(Message.ERROR0NO_PERM + "");
            }
            else if (AutoPickupPlugin.autoSell.contains(p.getName()))
            {
                AutoPickupPlugin.autoSell.remove(p.getName());
                p.sendMessage(Message.SUCCESS0TOGGLE0AUTOSELL_OFF + "");
            }else
            {
                AutoPickupPlugin.autoSell.add(p.getName());
                p.sendMessage(Message.SUCCESS0TOGGLE0AUTOSELL_ON + "");
            }
            return true;
        }
        return false;
    }

}

