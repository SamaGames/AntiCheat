package net.samagames.anticheat.database;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.samagames.anticheat.cheats.Cheats;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.Date;
import java.util.UUID;

public abstract class BasicCheatLog
{
    private Cheats cheat;
    private String server;
    private Date date;
    private UUID playerID;
    private String playerName;
    private Double serverTps;
    private Integer playerLag;

    public BasicCheatLog(OfflinePlayer player, Cheats cheat)
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

    public Cheats getCheat()
    {
        return this.cheat;
    }
}
