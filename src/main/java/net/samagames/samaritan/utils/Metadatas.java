package net.samagames.samaritan.utils;

import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

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
public final class Metadatas
{
    private Metadatas()
    {

    }

    public static Object getMetadata(JavaPlugin plugin, Metadatable object, String key)
    {
        List<MetadataValue> values = object.getMetadata(key);
        for (MetadataValue value : values)
        {
            if (value.getOwningPlugin().equals(plugin))
            {
                return value.value();
            }
        }

        return null;
    }

    public static void setMetadata(JavaPlugin plugin, Metadatable object, String key, Object value)
    {
        object.setMetadata(key, new FixedMetadataValue(plugin, value));
    }
}

