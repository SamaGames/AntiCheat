package net.samagames.samaritan;

import com.google.gson.Gson;
import net.md_5.bungee.api.ChatColor;
import net.samagames.api.SamaGamesAPI;
import net.samagames.samaritan.util.JsonCaseLine;
import net.samagames.tools.JsonModMessage;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class ModerationTools
{

    private static final SamaGamesAPI API = SamaGamesAPI.get();

    private ModerationTools()
    {

    }

    public static void addSanction(JsonCaseLine sanction, UUID player)
    {
        // TODO: Reimplement it
        /*Long time = System.currentTimeMillis();
        sanction.setTimestamp(time);
        String json = new Gson().toJson(sanction);

        Jedis jedis = SamaGamesAPI.get().getResource();
        jedis.zadd("case:" + player, time, json);
        jedis.close();*/
    }

    public static void broadcastSanction(JsonCaseLine sanction, String toPseudo)
    {
        String message = "Bannissement appliqué sur " + toPseudo + " pour la raison " + sanction.getMotif() + ".";
        modMessage("Samaritan", ChatColor.DARK_RED, message);

        Jedis jedis = API.getBungeeResource();
        jedis.publish("cheat", toPseudo + "#####" + sanction.getMotif());
        jedis.close();
    }

    public static void sendModMessage(JsonModMessage message)
    {
        Jedis jedis = API.getBungeeResource();
        jedis.publish("moderationchan", new Gson().toJson(message));
        jedis.close();
    }

    public static void modMessage(String from, ChatColor prefix, String message)
    {
        sendModMessage(new JsonModMessage(from, prefix, message));
    }
}
