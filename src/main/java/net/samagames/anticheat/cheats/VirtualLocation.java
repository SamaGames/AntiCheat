package net.samagames.anticheat.cheats;

import org.bukkit.Location;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by zyuiop
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class VirtualLocation {
	private double x;
	private double y;
	private double z;
	private String world;

	public VirtualLocation() {

	}

	public VirtualLocation(Location loc) {
		setX(loc.getX());
		setY(loc.getY());
		setZ(loc.getZ());
		this.world = loc.getWorld().getName();
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public String getWorld() {
		return world;
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
