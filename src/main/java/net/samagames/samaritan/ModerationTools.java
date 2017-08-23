package net.samagames.samaritan;

import net.samagames.api.SamaGamesAPI;
import net.samagames.samaritan.utils.JsonCaseLine;
import net.samagames.tools.JsonModMessage;
import net.samagames.tools.ModChannel;
import org.bukkit.ChatColor;
import redis.clients.jedis.Jedis;

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
        new JsonModMessage("Samaritan", ModChannel.SANCTION, ChatColor.DARK_RED, "Joueur " + toPseudo + " banni pour le motif " + sanction.getMotif() + ". Durée : Définitif").send();

        Jedis jedis = API.getBungeeResource();
        jedis.publish("cheat", toPseudo + "#####" + sanction.getMotif());
        jedis.close();
    }
}
