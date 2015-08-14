package net.samagames.anticheat.database;

import com.google.gson.Gson;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.cheats.Cheats;
import net.samagames.api.SamaGamesAPI;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import redis.clients.jedis.Jedis;

import java.util.UUID;

public class PunishmentsManager
{
	public void addCheatLog(BasicCheatLog log)
    {
		Jedis jedis = SamaGamesAPI.get().getResource();
		jedis.sadd("anticheat:log:" + log.getPlayerID(), new Gson().toJson(log));
		jedis.close();
	}


	private void dispatch(String... args)
    {
		Jedis jedis = SamaGamesAPI.get().getBungeeResource();
        jedis.publish("redisbungee-allservers", StringUtils.join(args, "#####"));
        jedis.close();
	}

	public void manualDefBan(OfflinePlayer player, String reason)
    {
		this.insertBan(player.getUniqueId(), reason);
		dispatch("kick", player.getUniqueId().toString(), "Vous avez été banni définitivement : " + reason);

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
        Jedis jedis = SamaGamesAPI.get().getResource();
		jedis.set("banlist:reason:" + player, reason);
		jedis.close();
	}

	public void automaticBan(final OfflinePlayer player, final Cheats cheat, final BasicCheatLog log)
    {
		this.broadcastSamaritan("Quels sont vos ordres ?");
		Bukkit.getScheduler().runTaskLater(AntiCheat.getInstance(), () -> this.broadcastGreer("Tu te trompes, mon cher Samaritain, quels sont tes ordres pour nous ?"), 20L);
		Bukkit.getScheduler().runTaskLater(AntiCheat.getInstance(), () ->
        {
            this.broadcastSamaritan("Eliminez ce tricheur : " + player.getName() + ", il est une menace pour le programme : " + log.getCheat().getBanReason());

            if (AntiCheat.getInstance().isDeveloppmentExecution())
                return;

            this.manualDefBan(player, cheat.getBanReason());
            this.addCheatLog(log);
        }, 3 * 20L);
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
