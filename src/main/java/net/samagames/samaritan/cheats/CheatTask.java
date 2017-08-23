package net.samagames.samaritan.cheats;

import net.samagames.samaritan.Samaritan;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * This file is part of AntiCheat (Samaritan).
 *
 * AntiCheat (Samaritan) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AntiCheat (Samaritan) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AntiCheat (Samaritan).  If not, see <http://www.gnu.org/licenses/>.
 */
public class CheatTask
{
    protected final ScheduledExecutorService poolExecutor;
    protected final Player player;
    protected boolean doTask;
    protected Samaritan samaritan;

    public CheatTask(Player player, boolean doTask)
    {
        this.samaritan = Samaritan.get();
        this.player = player;
        this.doTask = doTask;
        this.poolExecutor = Executors.newScheduledThreadPool(1);

        System.out.println("Adding task " + player.getDisplayName());
        if (doTask)
            this.poolExecutor.scheduleAtFixedRate(this::run, this.getInitialTime(), this.getRunInterval(), TimeUnit.MILLISECONDS);
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
            Bukkit.getScheduler().runTask(samaritan, this::exec);
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

    public long getRunInterval()
    {
        return 50L;
    }

    public long getInitialTime()
    {
        return 20L;
    }

}
