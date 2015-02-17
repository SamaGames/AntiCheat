package net.samagames.anticheat.speedhack;

import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.CheatTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by {USER}
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class SpeedHack extends CheatTask {

    protected Location currentLocation;
    protected Location previousLocation;
    protected int level = 0;

    public SpeedHack(Player player) {
        super(player);

        this.currentLocation = player.getLocation();
        this.previousLocation = player.getLocation();
    }

    @Override
    public void run() {
        super.run();

        currentLocation = player.getLocation().clone();

        if (!hasMoved())
            return;

        double distance = getHorizontalDistance();
        //AntiCheat.log("[M] " + player.getName() + " d="+distance);

        findSpeedHack(distance);

        previousLocation = currentLocation.clone();
    }

    public boolean hasMoved() {
        return currentLocation.getX() != previousLocation.getX() || currentLocation.getY() != previousLocation.getY() || currentLocation.getZ() != currentLocation.getZ();
    }

    public double getHorizontalDistance() {
        return Math.sqrt(Math.pow(currentLocation.getX() - previousLocation.getX(), 2) + Math.pow(currentLocation.getZ() - previousLocation.getZ(), 2));
    }

    public double getMaxDistancePerTick() {
        double maxPerSecond = 6.3;
        if (player.isSneaking())
            maxPerSecond = 1.5;


        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                maxPerSecond += effect.getAmplifier();
            } else if (effect.getType().equals(PotionEffectType.SLOW)) {
                double reduction = 15.0 * effect.getAmplifier();
                maxPerSecond = (maxPerSecond * (100.0 - reduction)) / 100.0;
            }
        }

        if (player.isFlying()) {
            maxPerSecond = 11;
        }

        double perMilliSecond = maxPerSecond / 1000.0;
        int ping = AntiCheat.getPlayer(player.getUniqueId()).getPing();

        AntiCheat.log("ping = "+ping+", max = "+perMilliSecond * (50 + ping) + " // WalkSpeed : "+player.getWalkSpeed()+" // PerSec : "+maxPerSecond);

        return perMilliSecond * (50 + ping + 20);
    }

    public void findSpeedHack(double distance) {
        if (distance > getMaxDistancePerTick()) { // distance maxi par seconde
            AntiCheat.log("[M] " + player.getName() + " is speedhacking (d = " + distance);
            level ++;
            if (level >= 10) {
                Bukkit.broadcastMessage(ChatColor.RED + "Le joueur "+player.getName()+" speedhack !");
                level = 0;
            } else {
                Bukkit.getScheduler().runTaskLater(AntiCheat.instance, new Runnable() {
                    @Override
                    public void run() {
                        if (level > 0)
                            level --;
                    }
                }, 5*20L);
            }
        }
    }
}
