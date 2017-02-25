package me.MnMaxon.AutoPickup;

import haveric.stackableItems.util.InventoryUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class AutoPickupPlugin extends JavaPlugin {
    public static String dataFolder;
    public static AutoPickupPlugin plugin;
    public static boolean infinityPick = false, deleteOnFull = true, warnOnFull = false, autoBlockXp = true, autoMob = true, autoMobXP = true, extraInfo = false,
            usingQuickSell = false, smeltFortune = false, usingCompat = false, usingAutoSell = false, usingStackableItems = false, usingPrisonGems = false;
    public static SuperYaml MainConfig, MessageConfig, SmeltConfig, WorldConfig, FortuneConfig, FortuneData = null;
    public static List<String> autoSmelt = new ArrayList<>(), autoPickup = new ArrayList<>(), autoBlock = new ArrayList<>(), autoSell = new ArrayList<>();
    public static List<String> fullNotify = new ArrayList<>();
    public static HashMap<String, Long> warnCooldown = new HashMap<>();
    public static HashMap<Material, Short> smeltBlacklist = new HashMap<>();
    private static List<String> blockedWorlds = new ArrayList<>();
    public static List<Material> fortuneList = new ArrayList<>();
    public static List<String> smeltList = new ArrayList<>();
    public static Boolean allowBlockGui;
    public static Boolean autoChest;

    @Deprecated
    public static void reloadConfigs() {
        Config.reloadConfigs();
    }

    public static ItemStack easyItem(String name, Material material, int amount, int durability, String... lore) {
        ItemStack is = new ItemStack(material);
        if (durability > 0) is.setDurability((short) durability);
        if (amount > 1) is.setAmount(amount);
        if (is.getItemMeta() != null) {
            ItemMeta im = is.getItemMeta();
            if (name != null) im.setDisplayName(name);
            if (lore != null) {
                ArrayList<String> loreList = new ArrayList<>();
                Collections.addAll(loreList, lore);
                im.setLore(loreList);
            }
            is.setItemMeta(im);
        }
        return is;
    }

    @Override
    public void onDisable() {
        if (FortuneData != null) FortuneData.save();
    }

    @Override
    public void onEnable() {
        plugin = this;
        dataFolder = this.getDataFolder().getAbsolutePath();
        Config.reloadConfigs();
        getServer().getPluginManager().registerEvents(new MainListener(), this);
        getServer().getPluginManager().registerEvents(new MythicMobListener(), this);
        ArrayList<String> plugins = new ArrayList<>();
        getServer().getPluginManager().registerEvents(new TokenEnchantListener(), this);
        if (getServer().getPluginManager().getPlugin("QuickSell") != null) {
            plugins.add("QuickSell");
            usingQuickSell = true;
        }
        if (getServer().getPluginManager().getPlugin("StackableItems") != null) {
            plugins.add("StackableItems");
            usingStackableItems = true;
        }
        if (getServer().getPluginManager().getPlugin("AutoSell") != null) {
            plugins.add("AutoSell");
            usingAutoSell = true;
        }
        if (getServer().getPluginManager().getPlugin("MythicDrops") != null) {
            plugins.add("MythicDrops");
            getServer().getPluginManager().registerEvents(new MythicListener(), this);
        }
        if (getServer().getPluginManager().getPlugin("MyPet") != null) {
            plugins.add("MyPet");
            if (!plugins.contains("MythicDrops"))
                getServer().getPluginManager().registerEvents(new MythicListener(), this);
        }
        if (getServer().getPluginManager().getPlugin("FortuneBlocks") != null) {
            plugins.add("FortuneBlocks");
            usingCompat = true;
        }
        if (getServer().getPluginManager().getPlugin("PrisonGems") != null) {
            plugins.add("PrisonGems");
            usingPrisonGems = true;
        }
        if (!plugins.isEmpty()) {
            String message = "[AutoPickup] Detected you are using ";
            for (String pName : plugins) {
                if (!message.endsWith(" ")) message = message + ", ";
                message = message + pName;
            }
            Bukkit.getLogger().info(message);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("AutoPickup.enabled")) AutoPickupPlugin.autoPickup.add(p.getName());
            if (p.hasPermission("AutoBlock.enabled")) AutoPickupPlugin.autoBlock.add(p.getName());
            if (p.hasPermission("AutoSmelt.enabled")) AutoPickupPlugin.autoSmelt.add(p.getName());
            if (p.hasPermission("AutoSell.enabled") && usingQuickSell) AutoPickupPlugin.autoSell.add(p.getName());
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("rl") || args[0].equalsIgnoreCase("reload"))) {
            reloadCommand(sender);
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You need to be a player to do this!");
            return true;
        }
        Player p = (Player) sender;
        if (getBlockedWorlds().contains(p.getWorld())) p.sendMessage(Message.ERROR0BLACKLISTED0WORLD + "");
        else switch (cmd.getName()) {
            case ("AutoSmelt"):
                if (args.length == 0)
                    if (!p.hasPermission("AutoSmelt.command")) p.sendMessage(Message.ERROR0NO_PERM + "");
                    else AutoSmelt.smelt(p);
                else if (args[0].equalsIgnoreCase("toggle"))
                    if (!p.hasPermission("AutoSmelt.toggle")) p.sendMessage(Message.ERROR0NO_PERM + "");
                    else if (autoSmelt.contains(p.getName())) {
                        autoSmelt.remove(p.getName());
                        p.sendMessage(Message.SUCCESS0TOGGLE0SMELT_OFF + "");
                    } else {
                        autoSmelt.add(p.getName());
                        p.sendMessage(Message.SUCCESS0TOGGLE0SMELT_ON + "");
                    }
                else displayHelp(p);
                break;
            case ("AutoPickup"):
                if (args.length == 0) openGui(p);
                else if (args[0].equalsIgnoreCase("toggle"))
                    if (!p.hasPermission("AutoPickup.toggle")) p.sendMessage(Message.ERROR0NO_PERM + "");
                    else if (autoPickup.contains(p.getName())) {
                        autoPickup.remove(p.getName());
                        p.sendMessage(Message.SUCCESS0TOGGLE0PICKUP_OFF + "");
                    } else {
                        autoPickup.add(p.getName());
                        p.sendMessage(Message.SUCCESS0TOGGLE0PICKUP_ON + "");
                    }
                else displayHelp(p);
                break;
            case ("AutoBlock"):
                if (args.length == 0)
                    if (!p.hasPermission("AutoBlock.command")) p.sendMessage(Message.ERROR0NO_PERM + "");
                    else AutoBlock.block(p);
                else if (args[0].equalsIgnoreCase("toggle"))
                    if (!p.hasPermission("AutoBlock.toggle")) p.sendMessage(Message.ERROR0NO_PERM + "");
                    else if (autoBlock.contains(p.getName())) {
                        autoBlock.remove(p.getName());
                        p.sendMessage(Message.SUCCESS0TOGGLE0BLOCK_OFF + "");
                    } else {
                        autoBlock.add(p.getName());
                        p.sendMessage(Message.SUCCESS0TOGGLE0BLOCK_ON + "");
                    }
                else displayHelp(p);
                break;
            case ("AutoSell"):
                if (!usingQuickSell) p.sendMessage(Message.ERROR0NO_QUICKSELL + "");
                else if (args.length == 0) p.sendMessage(ChatColor.RED + "Use like: /AutoSell toggle");
                else if (args[0].equalsIgnoreCase("toggle"))
                    if (!p.hasPermission("AutoSell.toggle")) p.sendMessage(Message.ERROR0NO_PERM + "");
                    else if (autoSell.contains(p.getName())) {
                        autoSell.remove(p.getName());
                        p.sendMessage(Message.SUCCESS0TOGGLE0AUTOSELL_OFF + "");
                    } else {
                        autoSell.add(p.getName());
                        p.sendMessage(Message.SUCCESS0TOGGLE0AUTOSELL_ON + "");
                    }
                else displayHelp(p);
                break;
        }
        return true;
    }

    public static void openGui(Player p) {
        if (allowBlockGui && p.hasPermission("AutoPickup.BlockGui")) {
            p.sendMessage(ChatColor.RED + "You do not have permission to open the gui");
            return;
        }
        int size = 18;
        Inventory newInv = Bukkit.createInventory(null, size, ChatColor.BLUE + "AutoPickup");
        // AP|AB|AS|A$|  |  |  |AS|AB
        // TO|TO|TO|TO|  |  |  |HE|SU
        ItemStack[] conts = newInv.getContents();
        conts[0] = easyItem(ChatColor.GREEN + "AutoPickup", Material.HOPPER, 1, 0, ChatColor.GRAY + "Sends mined blocks", ChatColor.GRAY + "straight to your inventory");
        conts[1] = easyItem(ChatColor.GREEN + "AutoBlock", Material.IRON_BLOCK, 1, 0, ChatColor.GRAY + "Turns ingots into blocks");
        conts[2] = easyItem(ChatColor.GREEN + "AutoSmelt", Material.FURNACE, 1, 0, ChatColor.GRAY + "Smelts all mined ores");
        if (usingQuickSell)
            conts[3] = easyItem(ChatColor.GREEN + "AutoSell", Material.GOLD_INGOT, 1, 0, ChatColor.GRAY + "Sells any possible", ChatColor.GRAY + "mined blocks");

        String autoPickupName = (autoPickup.contains(p.getName())) ? ChatColor.GREEN + "AutoPickup ENABLED" : ChatColor.RED + "AutoPickup DISABLED";
        String autoBlockName = (autoBlock.contains(p.getName())) ? ChatColor.GREEN + "AutoBlock ENABLED" : ChatColor.RED + "AutoBlock DISABLED";
        String autoSmeltName = (autoSmelt.contains(p.getName())) ? ChatColor.GREEN + "AutoSmelt ENABLED" : ChatColor.RED + "AutoSmelt DISABLED";
        String autoSellName = (autoSell.contains(p.getName())) ? ChatColor.GREEN + "AutoSell ENABLED" : ChatColor.RED + "AutoSell DISABLED";

        int apDur = (autoPickup.contains(p.getName())) ? 10 : 8;
        int abDur = (autoBlock.contains(p.getName())) ? 10 : 8;
        int asDur = (autoSmelt.contains(p.getName())) ? 10 : 8;
        int aSellDur = (autoSell.contains(p.getName())) ? 10 : 8;

        conts[9] = (p.hasPermission("AutoPickup.Toggle")) ? easyItem(autoPickupName, Material.INK_SACK, 1, apDur, ChatColor.GRAY + "Click to Toggle") : easyItem(autoPickupName, Material.INK_SACK, 1, apDur);
        conts[10] = (p.hasPermission("AutoBlock.Toggle")) ? easyItem(autoBlockName, Material.INK_SACK, 1, abDur, ChatColor.GRAY + "Click to Toggle") : easyItem(autoBlockName, Material.INK_SACK, 1, abDur);
        conts[11] = (p.hasPermission("AutoSmelt.Toggle")) ? easyItem(autoSmeltName, Material.INK_SACK, 1, asDur, ChatColor.GRAY + "Click to Toggle") : easyItem(autoSmeltName, Material.INK_SACK, 1, asDur);
        if (usingQuickSell)
            conts[12] = (p.hasPermission("AutoSell.Toggle")) ? easyItem(autoSellName, Material.INK_SACK, 1, aSellDur, ChatColor.GRAY + "Click to Toggle") : easyItem(autoSellName, Material.INK_SACK, 1, aSellDur);

        ItemStack locked = easyItem(ChatColor.RED + "LOCKED", Material.STAINED_GLASS_PANE, 1, 14);
        ItemStack empty = easyItem(null, Material.STAINED_GLASS_PANE, 1, 7);
        conts[7] = (p.hasPermission("AutoBlock.Command")) ? easyItem(ChatColor.GREEN + "Block Your Items", Material.IRON_INGOT, 1, 0) : locked;
        conts[8] = (p.hasPermission("AutoSmelt.Command")) ? easyItem(ChatColor.GREEN + "Smelt Your Items", Material.COAL, 1, 0) : locked;
        if (extraInfo)
            conts[16] = easyItem(ChatColor.RED + "Suggestions/Found Bugs", Material.LAVA_BUCKET, 1, 0, ChatColor.GRAY + "Contact " + ChatColor.GOLD + "MnMaxon" + ChatColor.GRAY + " on ", ChatColor.GRAY + "Spigot or Bukkit", ChatColor.GRAY + "Or, by email:", ChatColor.GRAY + "masontg777@aol.com");
        conts[17] = easyItem(ChatColor.RED + "Close", Material.ARROW, 1, 0);
        for (int i = 0; i < conts.length; i++) if (conts[i] == null) conts[i] = empty;

        if (p.getInventory() != null && p.getInventory().getName() != null && p.getInventory().getName().equals(ChatColor.BLUE + "AutoPickup")) {
            p.getInventory().setContents(conts);
            p.updateInventory();
        } else {
            newInv.setContents(conts);
            p.openInventory(newInv);
        }
    }

    public static List<World> getBlockedWorlds() {
        ArrayList<World> worlds = new ArrayList<>();
        for (String s : blockedWorlds) {
            World w = Bukkit.getWorld(s);
            if (w != null) worlds.add(w);
        }
        return worlds;
    }

    private void reloadCommand(CommandSender s) {
        if (s instanceof Player && !s.hasPermission("AutoSmelt.reload")) s.sendMessage(Message.ERROR0NO_PERM + "");
        else {
            reloadConfigs();
            s.sendMessage(Message.SUCCESS0RELOADED + "");
        }
    }

    public static void displayHelp(CommandSender s) {
        ChatColor c1 = null;
        ChatColor c2 = null;
        Random random = new Random();
        while (c1 == null || c1 == ChatColor.MAGIC || c1 == ChatColor.ITALIC || c1 == ChatColor.BLACK || c1 == ChatColor.UNDERLINE || c1 == ChatColor.BOLD || c1 == ChatColor.RESET || c1 == ChatColor.STRIKETHROUGH)
            c1 = ChatColor.values()[random.nextInt(ChatColor.values().length - 1)];
        while (c2 == null || c2 == c1 || c2 == ChatColor.MAGIC || c2 == ChatColor.ITALIC || c2 == ChatColor.BLACK || c2 == ChatColor.UNDERLINE || c2 == ChatColor.BOLD || c2 == ChatColor.RESET || c2 == ChatColor.STRIKETHROUGH)
            c2 = ChatColor.values()[random.nextInt(ChatColor.values().length - 1)];
        ArrayList<String> messages = new ArrayList<>();
        messages.add("AutoPickup-Displays this screen");
        if (usingQuickSell) messages.add("AutoSell toggle-Toggles auto sell");
        messages.add("AutoPickup toggle-Toggles auto pickup");
        messages.add("AutoBlock toggle-Toggles auto block");
        messages.add("AutoBlock-Turns anything that can be into a block");
        messages.add("AutoSmelt-Smelts anything that can be smelted in your inventory");
        messages.add("AutoSmelt toggle-Toggles auto smelt");
        messages.add("AutoSmelt reload-Reloads the plugin");
        s.sendMessage(c1 + "==== " + c2 + AutoPickupPlugin.plugin.getName() + c1 + " ====");
        for (String message : messages)
            s.sendMessage(c2 + "/" + message.replace("-", c1 + " - "));
        s.sendMessage(c1 + "For more help: " + c2 + "http://goo.gl/WdfLpK");
        s.sendMessage(c1 + "Shortcuts: " + c2 + "/ap = /AutoPickup, /ab = /AutoBlock, /as = /AutoSmelt");
    }

    public static void warn(Player p) {
        if (warnOnFull && p != null && p.isValid() && (!warnCooldown.containsKey(p.getName()) || warnCooldown.get(p.getName()) < Calendar.getInstance().getTimeInMillis())) {
            p.sendMessage(Message.ERROR0FULL_INVENTORY + "");
            warnCooldown.put(p.getName(), 5000 + Calendar.getInstance().getTimeInMillis());
        }
    }

    public static HashMap<Integer, ItemStack> giveItem(Player p, Inventory inv, ItemStack is) {
        if (is == null) return new HashMap<>();
        if (!usingStackableItems || p == null) return inv.addItem(is);
        ItemStack toSend = is.clone();
        ItemStack remaining = null;
        int freeSpaces = InventoryUtil.getPlayerFreeSpaces(p, toSend);
        if (freeSpaces < toSend.getAmount()) {
            remaining = toSend.clone();
            remaining.setAmount(toSend.getAmount() - freeSpaces);
            toSend.setAmount(freeSpaces);
        }
        if (toSend.getAmount() > 0) InventoryUtil.addItemsToPlayer(p, toSend, "pickup");
        HashMap<Integer, ItemStack> map = new HashMap<>();
        if (remaining != null) map.put(0, remaining);
        return map;
    }

    public static HashMap<Integer, ItemStack> giveItem(Player p, ItemStack is) {
        return giveItem(p, p.getInventory(), is);
    }
}