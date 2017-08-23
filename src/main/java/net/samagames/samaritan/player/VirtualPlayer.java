package net.samagames.samaritan.player;

import net.samagames.samaritan.cheats.CheatTask;
import net.samagames.samaritan.cheats.EnumCheat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
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
public class VirtualPlayer
{
    private Player player;

    private HashMap<EnumCheat, CheatTask> cheats = new HashMap<>();

    private Map<String, Object> playerData = new HashMap<>();
    private static final Map<UUID, VirtualPlayer> VIRTUAL_PLAYER_MAP = new HashMap<>();

    private VirtualPlayer(Player player)
    {
        this.player = player;
    }


    public static VirtualPlayer getVirtualPlayer(Player player)
    {
        if (VIRTUAL_PLAYER_MAP.containsKey(player.getUniqueId()))
            return VIRTUAL_PLAYER_MAP.get(player.getUniqueId());

        VirtualPlayer newPlayer = new VirtualPlayer(player);
        VIRTUAL_PLAYER_MAP.put(player.getUniqueId(), newPlayer);
        return newPlayer;
    }

    public static VirtualPlayer getVirtualPlayer(UUID uuid)
    {
        if (VIRTUAL_PLAYER_MAP.containsKey(uuid))
            return VIRTUAL_PLAYER_MAP.get(uuid);

        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return null;

        VirtualPlayer newPlayer = new VirtualPlayer(player);
        VIRTUAL_PLAYER_MAP.put(uuid, newPlayer);
        return newPlayer;
    }

    public static void removeVirtualPlayer(Player player)
    {
        VIRTUAL_PLAYER_MAP.remove(player.getUniqueId());
    }


    public int getPing()
    {
        return ((CraftPlayer) this.player).getHandle().ping;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void increasePacketCounter(String packet)
    {
        Integer i = (Integer) playerData.getOrDefault(packet, 0) + 1;
        this.setData(packet, i);
    }

    public Integer getPacketCounter(String packet)
    {
        return getDataOrDefault(packet, 0);
    }

    public void resetPacketCounter(String packet)
    {
        this.setData(packet, 0);
    }


    public Object getData(String key)
    {
        return getDataOrDefault(key, null);
    }

    public <E> E getDataOrDefault(String key, E defaultValue)
    {
        return (E) playerData.getOrDefault(key, defaultValue);
    }

    public void setData(String key, Object value)
    {
        playerData.put(key, value);
    }

    public void addCheat(EnumCheat cheat, CheatTask cheatTask)
    {
        this.cheats.put(cheat, cheatTask);
    }

    public CheatTask getCheat(EnumCheat cheat)
    {
        return this.cheats.get(cheat);
    }

    public HashMap<EnumCheat, CheatTask> getCheats()
    {
        return this.cheats;
    }

}
