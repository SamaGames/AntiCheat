package net.samagames.samaritan.listeners;

import net.samagames.samaritan.Samaritan;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
