package me.MnMaxon.AutoPickup; 

import org.bukkit.inventory.ItemStack; 

/**
 * Created by MnMaxon on 5/26/2015. */
public class AutoResult 
{
    private final ItemStack newItem; 
    private final ItemStack original; 
    private final boolean changed; 

    public AutoResult(ItemStack newItem, ItemStack original, boolean changed)
    {
        this.newItem = newItem; 
        this.original = original; 
        this.changed = changed; 
    }

    public ItemStack getNewItem()
    {
        return newItem; 
    }

    public boolean isChanged()
    {
        return changed; 
    }

    public ItemStack getOriginal()
    {
        return original; 
    }
}
