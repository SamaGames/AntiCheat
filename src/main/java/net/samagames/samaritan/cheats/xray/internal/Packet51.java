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

import net.samagames.api.shadows.play.server.PacketChunkData;


public class Packet51 {

    private PacketChunkData packet;
    private ChunkData chunkData;

    public void setPacket(PacketChunkData packet) {

        this.packet = packet;
        chunkData = new ChunkData(packet.getChunkMap(), packet.getLocX(), packet.getLocY());
    }

    public ChunkData getChunkData() {
        return chunkData;
    }

}
