package net.samagames.anticheat.speedhack;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class VirtualLocation {
	private int x;
	private int y;
	private int z;
	private String world;

	public VirtualLocation() {

	}

	public VirtualLocation(Location loc) {
		setX(loc.getBlockX());
		setY(loc.getBlockY());
		setZ(loc.getBlockZ());
		this.world = loc.getWorld().getName();
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public VirtualLocation(String string) {
		String[] parts = string.split("/");
		x = Integer.valueOf(parts[0]);
		y = Integer.valueOf(parts[1]);
		z = Integer.valueOf(parts[2]);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (! (o instanceof VirtualLocation))
			return false;

		VirtualLocation that = (VirtualLocation) o;

		if (x != that.x)
			return false;
		if (y != that.y)
			return false;
		if (z != that.z)
			return false;

		return true;
	}

	public String toString() {
		return x + "/" + y + "/" + z;
	}
}
