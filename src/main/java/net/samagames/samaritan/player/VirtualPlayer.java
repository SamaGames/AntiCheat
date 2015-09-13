package net.samagames.samaritan.player;

import net.samagames.samaritan.cheats.CheatTask;
import net.samagames.samaritan.cheats.EnumCheat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class VirtualPlayer
{
    private Player player;

    private HashMap<EnumCheat, CheatTask> cheats;

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
        return (Integer) getDataOrDefault(packet, 0);
    }

    public void resetPacketCounter(String packet)
    {
        this.setData(packet, 0);
    }


    public Object getData(String key)
    {
        return getDataOrDefault(key, null);
    }

    public Object getDataOrDefault(String key, Object defaultValue)
    {
        return playerData.getOrDefault(key, defaultValue);
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
