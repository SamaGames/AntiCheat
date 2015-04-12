package net.samagames.anticheat.cheats.killaura;

import net.samagames.anticheat.cheats.VirtualLocation;
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

	private int displayedCount;
	private int hitCount;

	/**
	 * Construct the cheatLog
	 * @param player Player who cheated
	 * @param banTime The time the player will get banned
	 * @param targetsHits The hits <targetloc:playerloc>
	 */
	protected KillauraCheatLog(OfflinePlayer player, String banTime, HashMap<VirtualLocation, VirtualLocation> targetsHits, int displayedCount) {
		super(player, "KillAura", banTime);
		this.targetsHits = targetsHits;
		this.displayedCount = displayedCount;
		this.hitCount = targetsHits.size();
	}

	/**
	 * Construct the cheatLog
	 * @param player Player who cheated
	 * @param targetsHits The hits <targetloc:playerloc>
	 */
	protected KillauraCheatLog(OfflinePlayer player, HashMap<VirtualLocation, VirtualLocation> targetsHits, int displayedCount) {
		super(player, "KillAura");
		this.targetsHits = targetsHits;
		this.displayedCount = displayedCount;
		this.hitCount = targetsHits.size();
	}

	protected KillauraCheatLog() {
		super();
	}
}
