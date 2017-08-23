package net.samagames.samaritan.utils;

import org.bukkit.Location;

/*
 * This file is part of AntiCheat (Samaritan).
 *
 * AntiCheat (Samaritan) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AntiCheat (Samaritan) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AntiCheat (Samaritan).  If not, see <http://www.gnu.org/licenses/>.
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