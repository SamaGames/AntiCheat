package net.samagames.anticheat.database;

import net.md_5.bungee.api.ChatColor;

public class JsonModMessage
{
	private String sender;
    private ChatColor senderPrefix;
    private String message;

	public JsonModMessage(String sender, ChatColor senderPrefix, String message)
    {
		this.sender = sender;
		this.senderPrefix = senderPrefix;
		this.message = message;
	}

	public String getSender() {
		return this.sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public ChatColor getSenderPrefix() {
		return this.senderPrefix;
	}

	public void setSenderPrefix(ChatColor senderPrefix) {
		this.senderPrefix = senderPrefix;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
