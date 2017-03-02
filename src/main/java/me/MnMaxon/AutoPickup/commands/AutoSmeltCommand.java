package me.MnMaxon.AutoPickup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.AutoSmelt;
import me.MnMaxon.AutoPickup.Message;

public class AutoSmeltCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(cmd.getName().equals("AutoSmelt"))
        {
            Player p = (Player)sender; 
            if (args.length == 0)
            {
                if ( ! p.hasPermission("AutoSmelt.command"))
                {
                    p.sendMessage(Message.ERROR0NO_PERM + ""); 
                }
                else 
                {
                    AutoSmelt.smelt(p); 
                }
            } else if (args[0].equalsIgnoreCase("toggle"))
            {
                if ( ! p.hasPermission("AutoSmelt.toggle"))
                {
                    p.sendMessage(Message.ERROR0NO_PERM + ""); 
                } else if (AutoPickupPlugin.autoSmelt.contains(p.getName()))
                {
                    AutoPickupPlugin.autoSmelt.remove(p.getName()); 
                    p.sendMessage(Message.SUCCESS0TOGGLE0SMELT_OFF + ""); 
                }else 
                {
                    AutoPickupPlugin.autoSmelt.add(p.getName()); 
                    p.sendMessage(Message.SUCCESS0TOGGLE0SMELT_ON + ""); 
                }
            } else
            {
                Common.displayHelp(p); 
            }
        }
        return false;
    }

}

  