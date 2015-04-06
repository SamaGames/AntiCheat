package net.samagames.anticheat.database;

import net.samagames.anticheat.AntiCheat;
import net.zyuiop.MasterBundle.MasterBundle;
import org.bukkit.Bukkit;
import redis.clients.jedis.ShardedJedis;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class BanRules {

	private HashMap<String, Boolean> mustBan = new HashMap<>();

	public BanRules(AntiCheat plugin) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::refreshRules, 0L, 20*300L);
	}

	public HashMap<String, Boolean> getRules() {
		return mustBan;
	}

	private void refreshRules() {
		ShardedJedis jedis = MasterBundle.jedis();
		Map<String, String> data = jedis.hgetAll("banrules");
		jedis.close();

		for (String cheat : data.keySet()) {
			try {
				Boolean bool = Boolean.valueOf(data.get(cheat));
				mustBan.put(cheat, bool);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean mustBan(String cheatName) {
		Boolean val = mustBan.get(cheatName);
		if (val == null)
			return true;
		return val;
	}

}
