package net.samagames.anticheat;

import net.samagames.permissionsbukkit.PermissionsBukkit;
import net.zyuiop.MasterBundle.MasterBundle;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import redis.clients.jedis.ShardedJedis;

import java.util.Arrays;
import java.util.Map;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class CommandAnticheat implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (! PermissionsBukkit.hasPermission(commandSender, "anticheat.command"))   {
			commandSender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission.");
			return true;
		}

		if (strings.length < 2) {
			commandSender.sendMessage(ChatColor.RED + "/anticheat <subcommand> <args...>");
			return true;
		}

		String com = strings[0];
		if (com.equals("enable")) {
			String cheat = StringUtils.join(Arrays.copyOfRange(strings, 1, strings.length), " ");
			Bukkit.getScheduler().runTaskAsynchronously(AntiCheat.instance, () -> {
				ShardedJedis jedis = MasterBundle.jedis();
				jedis.hset("banrules", cheat, String.valueOf(true));
				jedis.close();

				commandSender.sendMessage(ChatColor.GREEN + "Ce cheat provoque désormais un bannissement.");
			});
		} else if (com.equals("disable")) {
			String cheat = StringUtils.join(Arrays.copyOfRange(strings, 1, strings.length), " ");
			Bukkit.getScheduler().runTaskAsynchronously(AntiCheat.instance, () -> {
				ShardedJedis jedis = MasterBundle.jedis();
				jedis.hset("banrules", cheat, String.valueOf(false));
				jedis.close();

				commandSender.sendMessage(ChatColor.GREEN + "Ce cheat ne provoque désormais plus de bannissements.");
			});
		} else if (com.equals("list")) {
			commandSender.sendMessage(ChatColor.GOLD + "========[Liste des règles de bannissement]========");
			for (Map.Entry<String, Boolean> rule : AntiCheat.banRules.getRules().entrySet()) {
				commandSender.sendMessage(ChatColor.GOLD + " - " + (rule.getValue() ? ChatColor.GREEN + "(Bannit)" : ChatColor.RED + "(Ne bannit pas)" ) + " " + rule.getKey());
			}
		}
		return true;
	}
}
