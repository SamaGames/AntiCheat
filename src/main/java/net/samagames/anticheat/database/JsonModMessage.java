package net.samagames.anticheat.database;

import net.md_5.bungee.api.ChatColor;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class JsonModMessage {

	protected String sender;
	protected ChatColor senderPrefix;
	protected String message;

	public JsonModMessage() {
	}

	public JsonModMessage(String sender, ChatColor senderPrefix, String message) {
		this.sender = sender;
		this.senderPrefix = senderPrefix;
		this.message = message;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public ChatColor getSenderPrefix() {
		return senderPrefix;
	}

	public void setSenderPrefix(ChatColor senderPrefix) {
		this.senderPrefix = senderPrefix;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
