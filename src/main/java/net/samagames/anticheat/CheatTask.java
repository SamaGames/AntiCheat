package net.samagames.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public abstract class CheatTask {

    protected Player player;
    protected ScheduledExecutorService poolExecutor = Executors.newScheduledThreadPool(1);

    protected boolean doTask = false;

    public CheatTask(Player player, boolean doTask) {
        this.player = player;
        this.doTask = doTask;

        AntiCheat.log("Register executor for " + player.getName());

        if(doTask)
        {
            //poolExecutor.schedule(() -> run(), 3L, TimeUnit.SECONDS);
            poolExecutor.scheduleAtFixedRate(() -> run(), 3000L, 50L, TimeUnit.MILLISECONDS);
        }
    }

    public void run() {
        if (!player.isOnline())
        {
            this.cancel();
        }

        try{
            Bukkit.getScheduler().runTask(AntiCheat.instance, new Runnable() {
                @Override
                public void run() {
                    exec();
                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try{
            asyncExec();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        //poolExecutor.schedule(() -> run(), 50L, TimeUnit.MILLISECONDS);
    }

    public void asyncExec(){}

    public void exec(){}

    public void cancel() {
        if (poolExecutor != null)
        {
            poolExecutor.shutdown();
            poolExecutor = null;
        }
    }

}
