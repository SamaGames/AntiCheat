package net.samagames.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by {USER}
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public abstract class CheatTask implements Runnable {

    protected Player player;
    protected BukkitTask task;

    public CheatTask(Player player) {
        this.player = player;

        task = Bukkit.getScheduler().runTaskTimer(AntiCheat.instance, this, 1, 1);
    }

    public void run() {
        if (!player.isOnline())
            this.cancel();
    }

    public void cancel() {
        if (task != null)
            task.cancel();
    }

}
