package net.samagames.samaritan.cheats;

import net.minecraft.server.v1_9_R2.MinecraftServer;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;

import java.util.Date;
import java.util.UUID;

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
public class BasicCheatLog
{
    private EnumCheat cheat;
    private String server;
    private Date date;
    private UUID playerID;
    private String playerName;
    private Double serverTps;
    private Integer playerLag;

    public BasicCheatLog(OfflinePlayer player, EnumCheat cheat)
    {
        this.server = SamaGamesAPI.get().getServerName();
        this.date = new Date();
        this.playerID = player.getUniqueId();
        this.playerName = player.getName();

        double[] tab = MinecraftServer.getServer().recentTps;
        this.serverTps = tab[0];

        this.playerLag = ((CraftPlayer) player).getHandle().ping;
        this.cheat = cheat;
    }

    public String getServer()
    {
        return this.server;
    }

    public Date getDate()
    {
        return this.date;
    }

    public UUID getPlayerID()
    {
        return this.playerID;
    }

    public String getPlayerName()
    {
        return this.playerName;
    }

    public Double getServerTps()
    {
        return this.serverTps;
    }

    public Integer getPlayerLag()
    {
        return this.playerLag;
    }

    public EnumCheat getCheat()
    {
        return this.cheat;
    }
}