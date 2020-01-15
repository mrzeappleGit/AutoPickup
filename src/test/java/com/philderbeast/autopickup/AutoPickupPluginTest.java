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

    @Before
    public void setUp()
    {
        server = MockBukkit.mock();
        MockBukkit.load(AutoPickupPlugin.class);
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

        // this fails due to an error in the mocking library
        server.joinPlayer(player);
        server.getPluginManager().assertEventFired(PlayerJoinEvent.class, event -> event.getPlayer().equals(player)); 

        // //no permissions so they arnt added
        Assert.assertFalse(AutoPickupPlugin.autoBlock.contains(player.getName()));
        Assert.assertFalse(AutoPickupPlugin.autoPickup.contains(player.getName()));
        Assert.assertFalse(AutoPickupPlugin.autoSmelt.contains(player.getName()));

    }


}