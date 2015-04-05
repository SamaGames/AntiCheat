package net.samagames.anticheat.database;

import net.minecraft.server.v1_8_R1.MinecraftServer;
import net.zyuiop.MasterBundle.MasterBundle;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

/**
 * This class is serialized using Gson
 * Please override it with your custom CheatLog, depending on what cheat you are tracking
 */
public abstract class BasicCheatLog {

	private String server;
	private Date date;
	private UUID playerID;
	private String playerName;
	private Double serverTps;
	private Integer playerLag;
	private String cheatName;
	private String banTime;

	/**
	 * Default constructor for a cheat log
	 * @param player The player who cheated
	 * @param cheatName The name of the tracked cheat
	 * @param banTime The time the player were banned for the cheat (litteral time, "None" if the player was not banned)
	 */
	protected BasicCheatLog(Player player, String cheatName, String banTime) {
		this.server = MasterBundle.getServerName();
		this.date = new Date();
		this.playerID = player.getUniqueId();
		this.playerName = player.getName();

		double[] tab = MinecraftServer.getServer().recentTps;
		this.serverTps = tab[0];

		this.playerLag = ((CraftPlayer) player).getHandle().ping;

		this.banTime = banTime;
		this.cheatName = cheatName;
	}

	protected BasicCheatLog() {
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public UUID getPlayerID() {
		return playerID;
	}

	public void setPlayerID(UUID playerID) {
		this.playerID = playerID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Double getServerTps() {
		return serverTps;
	}

	public void setServerTps(Double serverTps) {
		this.serverTps = serverTps;
	}

	public Integer getPlayerLag() {
		return playerLag;
	}

	public void setPlayerLag(Integer playerLag) {
		this.playerLag = playerLag;
	}

	public String getCheatName() {
		return cheatName;
	}

	public void setCheatName(String cheatName) {
		this.cheatName = cheatName;
	}

	public String getBanTime() {
		return banTime;
	}

	public void setBanTime(String banTime) {
		this.banTime = banTime;
	}
}
