package net.samagames.anticheat.database;

import net.zyuiop.MasterBundle.FastJedis;
import net.zyuiop.MasterBundle.MasterBundle;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class PunishmentsManager {
	/**
	 * This method is synchronous. Be carefull, it's not instant !
	 * @param log The log to append
	 */
	public void addCheatLog(BasicCheatLog log) {
		ShardedJedis jedis = MasterBundle.jedis();
		jedis.sadd("anticheat:log:" + log.getPlayerID(), new Gson().toJson(log));
		jedis.close();
	}

	/**
	 * Returns the Ban score
	 * @param player The player you want to get the banscore
	 * @return BanScore. 0 = never banned. 1 = banned once. 2 = Banned twice...
	 */
	public int getBanScore(UUID player) {
		ShardedJedis jedis = MasterBundle.jedis();
		String score = jedis.hget("anticheat:banscores", player.toString());
		jedis.close();

		return (score == null) ? 0 : Integer.valueOf(score);
	}

	public void increaseBanScore(UUID player) {
		ShardedJedis jedis = MasterBundle.jedis();
		jedis.hincrBy("anticheat:banscores", player.toString(), 1);
		jedis.close();
	}

	public void manualTempBan(Player player, Date end, String reason) {
		long time = ((end.getTime() - new Date().getTime()) / 1000);
		insertBan(player.getUniqueId(), reason, (int) time);

		String timeStr = formatTime(time + 1);
		dispatch("kick", player.getUniqueId().toString(), "Vous avez été banni " + timeStr + " : " + reason);

		JsonCaseLine sanction = new JsonCaseLine();
		sanction.setAddedBy("Samaritain");
		sanction.setMotif(reason);
		sanction.setType("Bannissement");
		sanction.setDurationTime(time);
		sanction.setDuration(timeStr);

		ModerationTools.addSanction(sanction, player.getUniqueId());
		ModerationTools.broadcastSanction(sanction, player.getName());
	}

	private void dispatch(String... args) {
		Jedis j = MasterBundle.getRedisBungee();
		j.publish("redisbungee-allservers", StringUtils.join(args, "#####"));
		j.close();
	}

	public void manualDefBan(Player player, String reason) {
		insertBan(player.getUniqueId(), reason, null);
		dispatch("kick", player.getUniqueId().toString(), "Vous avez été banni définitivement : " + reason);

		JsonCaseLine sanction = new JsonCaseLine();
		sanction.setAddedBy("Samaritain");
		sanction.setMotif(reason);
		sanction.setType("Bannissement");
		sanction.setDurationTime(-1L);

		ModerationTools.addSanction(sanction, player.getUniqueId());
		ModerationTools.broadcastSanction(sanction, player.getName());
	}

	private void insertBan(UUID player, String reason, Integer duration) {
		ShardedJedis jedis = FastJedis.jedis();
		jedis.set("banlist:reason:" + player, reason);
		if (duration != null)
			jedis.expire("banlist:reason:" + player, duration);
		jedis.close();
	}

	/**
	 * Bans the player and return the time of the ban
	 * @param player The player to ban
	 * @param reason The reason of the ban
	 */
	public void automaticBan(Player player, String reason, BasicCheatLog log) {
		Integer months = (getBanScore(player.getUniqueId()) + 1) * 3;
		if (months > 6) {
			manualDefBan(player, reason);
			log.setBanTime("Définitif");
		} else {
			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(new Date()); // sets calendar time/date
			cal.add(Calendar.MONTH, months);
			Date end = cal.getTime();
			manualTempBan(player, end, reason);

			long time = ((end.getTime() - new Date().getTime()) / 1000);
			insertBan(player.getUniqueId(), reason, (int) time);
			log.setBanTime(formatTime(time + 1));
		}
		increaseBanScore(player.getUniqueId());
		addCheatLog(log);
	}

	public static String formatTime(long time) {
		int days = (int) time / (3600*24);
		int remainder = (int) time - days * (3600*24);
		int hours = remainder / 3600;
		remainder = remainder - (hours * 3600);
		int mins = remainder / 60;

		String ret = "";
		if (days > 0) {
			ret+= days+" jours ";
		}

		if (hours > 0) {
			ret += hours+" heures ";
		}

		if (mins > 0) {
			ret += mins+" minutes ";
		}

		if (ret.equals("") && mins == 0)
			ret += "moins d'une minute";

		return ret;
	}
}
