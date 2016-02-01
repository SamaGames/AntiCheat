/*
 * Copyright (C) 2011-2014 lishid.  All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.samagames.samaritan.cheats.xray.internal;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.samagames.samaritan.cheats.xray.utils.ReflectionHelper;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

//Volatile

public class PlayerHook {

    public Channel getChannel(CraftPlayer player) throws Exception {
        Field[] fields = NetworkManager.class.getDeclaredFields();
        Field channelField = null;
        for (Field f : fields) {
            if (Channel.class.isAssignableFrom(f.getType())) {
                channelField = f;
                break;
            }
        }
        if (channelField == null) {
            throw new Exception("Cannot find netty channel field in NetworkManager!");
        }
        channelField.setAccessible(true);
        NetworkManager networkManager = (NetworkManager) player.getHandle().playerConnection.networkManager;
        return (Channel) channelField.get(networkManager);
    }

    public void HookChunkQueue(Player p) {
        CraftPlayer player = (CraftPlayer) p;
        ReflectionHelper.setPrivateFinal(player.getHandle(), "chunkCoordIntPairQueue",
                new ChunkQueue(player, player.getHandle().chunkCoordIntPairQueue));
    }
}
