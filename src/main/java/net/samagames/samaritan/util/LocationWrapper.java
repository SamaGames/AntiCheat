package net.samagames.samaritan.util;

import org.bukkit.Location;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class LocationWrapper
{
    private String world;
    private double x;
    private double y;
    private double z;

    public LocationWrapper(Location loc)
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

        if (!(o instanceof LocationWrapper))
            return false;

        LocationWrapper that = (LocationWrapper) o;

        if (this.x != that.x)
            return false;

        if (this.y != that.y)
            return false;

        if (this.z != that.z)
            return false;

        return true;
    }

    @Override
    public String toString()
    {
        return this.x + "/" + this.y + "/" + this.z;
    }
}