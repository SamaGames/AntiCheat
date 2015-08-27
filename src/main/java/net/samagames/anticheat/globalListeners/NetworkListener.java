package net.samagames.anticheat.globalListeners;

import net.samagames.anticheat.AntiCheat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public class NetworkListener implements Listener
{
    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        AntiCheat.getInstance().login(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        AntiCheat.getInstance().logout(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerKickEvent event)
    {
        AntiCheat.getInstance().logout(event.getPlayer());
    }

    @EventHandler
    public void onVeloctiyChange(PlayerVelocityEvent event)
    {
        /*ACPlayer acp = AntiCheat.getInstance().getPlayer(event.getPlayer().getUniqueId());
        SpeedHack speedHack = (SpeedHack) acp.getCheat("SpeedHack");
        if(speedHack == null)
            return;

        speedHack.updateVelocity(event.getVelocity());*/
    }
}
