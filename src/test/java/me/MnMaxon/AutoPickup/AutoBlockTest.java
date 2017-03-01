package me.MnMaxon.AutoPickup;

import org.junit.*;
import org.bukkit.ChatColor;


import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


import org.mockito.Mock; 
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class AutoBlockTest {

    @Mock Player player;
    @Mock PlayerInventory inventory;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addItemIngotsTest()
    {

        when(player.getInventory()).thenReturn(inventory);
        when(inventory.getName()).thenReturn(ChatColor.BLUE + "AutoPickup");

        ItemStack itemStack = mock(ItemStack.class);
        AutoBlock.addItem(player, itemStack);
    }

}