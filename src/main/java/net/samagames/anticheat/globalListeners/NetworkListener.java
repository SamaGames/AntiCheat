package net.samagames.anticheat.globalListeners;

import net.samagames.anticheat.ACPlayer;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.speedhack.KillAura;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

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

    /*@EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        if(!hasMoved(event.getFrom(), event.getTo()))
            return;

        ACPlayer acp = AntiCheat.getPlayer(event.getPlayer().getUniqueId());
        double distance = getDistance(event.getFrom(), event.getTo());
        acp.walkedDistance += distance;
    }*/

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent event)
    {
        ACPlayer acp = AntiCheat.getPlayer(event.getPlayer().getUniqueId());
        KillAura ka = (KillAura) acp.getCheat("KillAura");

        ka.onClick(event);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event)
    {
        ACPlayer acp = AntiCheat.getPlayer(event.getPlayer().getUniqueId());
        acp.setSneaking(event.isSneaking());
    }

    @EventHandler
    public void onPlayerSprint(PlayerToggleSprintEvent event)
    {
        ACPlayer acp = AntiCheat.getPlayer(event.getPlayer().getUniqueId());
        acp.setSprinting(event.isSprinting());
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
