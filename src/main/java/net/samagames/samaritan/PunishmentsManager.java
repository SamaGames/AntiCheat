package net.samagames.samaritan;

import com.google.gson.Gson;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.samagames.api.SamaGamesAPI;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.restfull.response.ErrorResponse;
import net.samagames.restfull.response.StatusResponse;
import net.samagames.samaritan.cheats.BasicCheatLog;
import net.samagames.samaritan.cheats.EnumCheat;
import net.samagames.samaritan.utils.JsonCaseLine;
import net.samagames.tools.JsonModMessage;
import net.samagames.tools.ModChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import redis.clients.jedis.Jedis;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class PunishmentsManager
{

    private final Samaritan samaritan;

    public PunishmentsManager(Samaritan samaritan)
    {
        this.samaritan = samaritan;
    }

    public void addCheatLog(BasicCheatLog log)
    {
        // TODO: RestAPI endpoint?
        Jedis jedis = SamaGamesAPI.get().getBungeeResource();
        jedis.sadd("anticheat:log:" + log.getPlayerID(), new Gson().toJson(log));
        jedis.close();
    }

    public void manualDefBan(OfflinePlayer player, String reason)
    {
        this.insertBan(player.getUniqueId(), reason);

        System.out.println(player.getUniqueId() + " " + ComponentSerializer.toString(new TextComponent("Vous avez été banni définitivement : " + reason)));
        SamaGamesAPI.get().getProxyDataManager().apiexec("kick", player.getUniqueId() + " " + new Gson().toJson(new TextComponent("Vous avez été banni définitivement : " + reason)));

        JsonCaseLine sanction = new JsonCaseLine();
        sanction.setAddedBy("Samaritain");
        sanction.setMotif(reason);
        sanction.setType("Bannissement");
        sanction.setDurationTime(-1L);

        ModerationTools.addSanction(sanction, player.getUniqueId());
        ModerationTools.broadcastSanction(sanction, player.getName());
    }

    private void insertBan(UUID player, String reason)
    {
        Object result = RestAPI.getInstance().sendRequest("player/ban", new Request().addProperty("reason", (reason == null) ? "Vous êtes banni." : reason).addProperty("playerUUID", player).addProperty("punisherUUID", new UUID(0,0)).addProperty("expiration", 0), StatusResponse.class, "POST");

        if (result instanceof ErrorResponse || (result instanceof StatusResponse && !(((StatusResponse) result).getStatus())))
        {
            Bukkit.getLogger().warning("Error when trying to ban " + player + " (" + result + ")");
        }
    }

    public void automaticBan(final OfflinePlayer player, final EnumCheat cheat, final BasicCheatLog log)
    {
        if (cheat.isBeta())
        {
            new JsonModMessage("Samaritan", ModChannel.REPORT, ChatColor.DARK_RED, player + "#####" + SamaGamesAPI.get().getGameManager() + "#####" + cheat.getBanReason()).send();
        }
        else
        {
            this.broadcastSamaritan("Quels sont vos ordres ?");
            Bukkit.getScheduler().runTaskLater(samaritan, () -> this.broadcastGreer("Tu te trompes, mon cher Samaritain, quels sont tes ordres pour nous ?"), 20L);
            Bukkit.getScheduler().runTaskLater(samaritan, () ->
            {
                this.broadcastSamaritan("Eliminez ce tricheur : " + player.getName() + ", il est une menace pour le programme : " + log.getCheat().getBanReason());
                this.manualDefBan(player, cheat.getBanReason());
                this.addCheatLog(log);
            }, 3 * 20L);
        }
    }

    private void broadcastSamaritan(String message)
    {
        String prefix = ChatColor.RED + "[" + ChatColor.GRAY + "Samaritan" + ChatColor.RED + "] ";
        Bukkit.broadcastMessage(prefix + ChatColor.RED + message);
    }

    private void broadcastGreer(String message)
    {
        String prefix = ChatColor.RED + "[" + ChatColor.GRAY + "Greer" + ChatColor.RED + "] ";
        Bukkit.broadcastMessage(prefix + ChatColor.RED + message);
    }
}
