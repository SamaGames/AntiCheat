package net.samagames.anticheat.globalListeners;

import net.samagames.anticheat.ACPlayer;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.cheats.speedhack.SpeedHack;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14
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

    @EventHandler
    public void onVeloctiyChange(PlayerVelocityEvent event)
    {
        ACPlayer acp = AntiCheat.getPlayer(event.getPlayer().getUniqueId());
        SpeedHack speedHack = (SpeedHack) acp.getCheat("SpeedHack");
        if(speedHack == null)
            return;

        speedHack.updateVelocity(event.getVelocity());
    }

    public boolean hasMoved(final Location from, final Location to) {
        return from.getX() != from.getX() || from.getY() != from.getY() || from.getZ() != to.getZ();
    }

    public double getDistance(final Location from, final Location to) { //  m
        double XDiff = from.getX() - to.getX();
        double ZDiff = from.getZ() - to.getZ();
        double YDiff = from.getY() - to.getY();

        YDiff = 0.0; //On s'en fou pour l'instant
        return Math.sqrt((XDiff * XDiff) + (ZDiff*ZDiff) + (YDiff*YDiff));
    }
}
