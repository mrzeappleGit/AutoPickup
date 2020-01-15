package com.philderbeast.autopickup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import org.bukkit.event.player.PlayerJoinEvent;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMockFactory;

public class AutoPickupPluginTest {

	private ServerMock server;
    private AutoPickupPlugin plugin;
    
    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = (AutoPickupPlugin) MockBukkit.load(AutoPickupPlugin.class);
        plugin.onEnable();
    }

    @After
    public void tearDown()
    {
        MockBukkit.unload();
        
    }

    @Test
    public void dummyTest()
    {
        Assert.assertTrue(true);
    }

    @Test
    public void playerJoin()
    {
        PlayerMockFactory factory = new PlayerMockFactory(server);
		PlayerMock player = factory.createRandomPlayer();
        server.joinPlayer(player);
        server.getPluginManager().assertEventFired(PlayerJoinEvent.class, event -> event.getPlayer().equals(player)); 

        // //no permissions so they arnt added
        Assert.assertFalse(AutoPickupPlugin.autoBlock.contains(player.getName()));
        Assert.assertFalse(AutoPickupPlugin.autoPickup.contains(player.getName()));
        Assert.assertFalse(AutoPickupPlugin.autoSmelt.contains(player.getName()));

    }


}