package me.MnMaxon.AutoPickup;

import org.junit.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import org.mockito.Mock; 
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;



public class AutoBlockTest {

    @Mock Player player;

    @Before
    public void setUp()
    {
        MockitoAnnotations.initMocks(this);
    }

    //@Test
    public void addItemIngotsTest()
    {

        ItemStack itemStack = mock(ItemStack.class);

        AutoBlock ab = new AutoBlock();
        ab.addItem(player, itemStack);
    }


}