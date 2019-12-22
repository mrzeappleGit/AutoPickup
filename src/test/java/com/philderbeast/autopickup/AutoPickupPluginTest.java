package com.philderbeast.autopickup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import org.bukkit.event.player.PlayerJoinEvent;

import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.MockBukkit;

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
        Assert.assertTrue(true);`
    }

    @Test
    public void playerJoin()
    {
        server.addPlayer();

        // this failes due to an error int he mocking library
        server.getPluginManager().assertEventFired(event -> { return event instanceof PlayerJoinEvent; });

        // //no permissions so they arnt added
        // Assert.assertFalse(AutoPickupPlugin.autoBlock.contains(player.getName()));
        // Assert.assertFalse(AutoPickupPlugin.autoPickup.contains(player.getName()));
        // Assert.assertFalse(AutoPickupPlugin.autoSmelt.contains(player.getName()));

        // server.execute("autopickup", player);
        // Assert.assertTrue(AutoPickupPlugin.autoPickup.contains(player.getName()));
    }


}