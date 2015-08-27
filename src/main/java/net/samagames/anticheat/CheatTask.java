package net.samagames.anticheat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class CheatTask
{
    protected final ScheduledExecutorService poolExecutor;
    protected final Player player;
    protected boolean doTask;

    public CheatTask(Player player, boolean doTask)
    {
        this.player = player;
        this.doTask = doTask;
        this.poolExecutor = Executors.newScheduledThreadPool(1);

        if (AntiCheat.getInstance().isDeveloppmentExecution())
            AntiCheat.getInstance().log(Level.INFO, "Register executor for " + player.getName());

        if (doTask)
            this.poolExecutor.scheduleAtFixedRate(this::run, 3000L, 50L, TimeUnit.MILLISECONDS);
    }

    public void run()
    {
        if (!this.player.isOnline())
        {
            this.cancel();
            return;
        }

        try
        {
            Bukkit.getScheduler().runTask(AntiCheat.getInstance(), this::exec);
            this.asyncExec();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void asyncExec()
    {
    }

    public void exec()
    {
    }

    public void cancel()
    {
        if (this.poolExecutor != null)
            this.poolExecutor.shutdown();
    }

}
