package net.samagames.anticheat.globalListeners;

import net.samagames.anticheat.AntiCheat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by {USER}
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class NetworkListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        AntiCheat.login(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        AntiCheat.logout(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerKickEvent event) {
        AntiCheat.logout(event.getPlayer());
    }
}
