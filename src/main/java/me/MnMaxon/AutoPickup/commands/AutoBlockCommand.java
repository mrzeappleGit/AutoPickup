package me.MnMaxon.AutoPickup.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.MnMaxon.AutoPickup.AutoBlock;

import me.MnMaxon.AutoPickup.AutoPickupPlugin;
import me.MnMaxon.AutoPickup.Message;

public class AutoBlockCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(cmd.getName().equals("AutoBlock"))
        {
            Player p = (Player)sender; 
            if (args.length == 0)
            {
                if ( ! p.hasPermission("AutoBlock.command"))
                {
                    p.sendMessage(Message.ERROR0NO_PERM + ""); 
                } else 
                {
                    AutoBlock.block(p); 
                }
            } else if (args[0].equalsIgnoreCase("toggle"))
            {
                if ( ! p.hasPermission("AutoBlock.toggle"))
                {
                    p.sendMessage(Message.ERROR0NO_PERM + ""); 
                }else if (AutoPickupPlugin.autoBlock.contains(p.getName()))
                {
                    AutoPickupPlugin.autoBlock.remove(p.getName()); 
                    p.sendMessage(Message.SUCCESS0TOGGLE0BLOCK_OFF + ""); 
                }else 
                {
                    AutoPickupPlugin.autoBlock.add(p.getName()); 
                    p.sendMessage(Message.SUCCESS0TOGGLE0BLOCK_ON + ""); 
                }
            } else
            {
                Common.displayHelp(p); 
            }
        }
        return false;
    }

}

  