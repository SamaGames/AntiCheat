package net.samagames.anticheat.database;

import net.md_5.bungee.api.ChatColor;
import net.zyuiop.MasterBundle.FastJedis;
import net.zyuiop.MasterBundle.MasterBundle;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by {USER}
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ModerationTools {
    public static void addSanction(JsonCaseLine sanction, UUID player) {
        Long time = System.currentTimeMillis();
        sanction.setTimestamp(time);
        String json = new Gson().toJson(sanction);
        ShardedJedis jedis = FastJedis.jedis();
        jedis.zadd("case:" + player, time, json);
        FastJedis.back(jedis);
    }

    public static void broadcastSanction(JsonCaseLine sanction, String toPseudo) {
        String message = "Bannissement appliqué sur " + toPseudo;
        message += " pour la raison " + sanction.getMotif();

        if (sanction.getDuration() != null)
            message += " pendant "+sanction.getDuration();

        message += ".";

        modMessage("Samaritan", ChatColor.DARK_RED, message);

        Jedis j = MasterBundle.getRedisBungee();
        String from = "Samaritan";
        String motif = "Le joueur " + toPseudo + " a été banni pour " + sanction.getMotif();
        j.publish("cheat", from + "#####" + sanction.getMotif());
        j.close();
    }

	public static void sendModMessage(JsonModMessage message) {
		Jedis j = MasterBundle.getRedisBungee();
		j.publish("redisbungee-allservers", "Mod::"+new Gson().toJson(message));
		j.close();
	}

    public static void modMessage(String from, ChatColor prefix, String message) {
		sendModMessage(new JsonModMessage(from, prefix, message));
    }
}
