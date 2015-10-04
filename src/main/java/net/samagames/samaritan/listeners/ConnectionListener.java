package net.samagames.samaritan.listeners;

import net.samagames.samaritan.Samaritan;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ConnectionListener implements Listener
{
    private final Samaritan plugin;

    public ConnectionListener(Samaritan samaritan)
    {
        this.plugin = samaritan;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        plugin.login(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        plugin.logout(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerKickEvent event)
    {
        plugin.logout(event.getPlayer());
    }
}
