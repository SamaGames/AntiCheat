package net.samagames.samaritan.cheats;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.Date;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
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