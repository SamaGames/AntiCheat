package net.samagames.anticheat.utils;

import org.bukkit.Location;

public class VirtualLocation
{
	private String world;
	private double x;
	private double y;
	private double z;

	public VirtualLocation(Location loc)
    {
        this.world = loc.getWorld().getName();
		this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
	}

	public double getX()
    {
		return this.x;
	}

	public void setX(double x)
    {
		this.x = x;
	}

	public double getY()
    {
		return this.y;
	}

	public void setY(double y)
    {
		this.y = y;
	}

	public double getZ()
    {
		return this.z;
	}

	public void setZ(double z)
    {
		this.z = z;
	}

	public String getWorld()
    {
		return this.world;
	}

	@Override
	public boolean equals(Object o)
    {
		if (this == o)
			return true;

		if (!(o instanceof VirtualLocation))
			return false;

		VirtualLocation that = (VirtualLocation) o;

		if (this.x != that.x)
			return false;

		if (this.y != that.y)
			return false;

		if (this.z != that.z)
			return false;

		return true;
	}

	public String toString() {
		return this.x + "/" + this.y + "/" + this.z;
	}
}
