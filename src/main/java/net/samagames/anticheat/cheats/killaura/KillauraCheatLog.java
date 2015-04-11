package net.samagames.anticheat.cheats.killaura;

import net.samagames.anticheat.cheats.speedhack.VirtualLocation;
import net.samagames.anticheat.database.BasicCheatLog;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class KillauraCheatLog extends BasicCheatLog {

	private HashMap<VirtualLocation, VirtualLocation> targetsHits = new HashMap<>();

	/**
	 * Construct the cheatLog
	 * @param player Player who cheated
	 * @param banTime The time the player will get banned
	 * @param targetsHits The hits <targetloc:playerloc>
	 */
	protected KillauraCheatLog(OfflinePlayer player, String banTime, HashMap<VirtualLocation, VirtualLocation> targetsHits) {
		super(player, "KillAura", banTime);
		this.targetsHits = targetsHits;
	}

	/**
	 * Construct the cheatLog
	 * @param player Player who cheated
	 * @param targetsHits The hits <targetloc:playerloc>
	 */
	protected KillauraCheatLog(OfflinePlayer player, HashMap<VirtualLocation, VirtualLocation> targetsHits) {
		super(player, "KillAura");
		this.targetsHits = targetsHits;
	}

	protected KillauraCheatLog() {
		super();
	}
}
