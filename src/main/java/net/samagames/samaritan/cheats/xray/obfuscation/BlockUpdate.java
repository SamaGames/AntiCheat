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

package net.samagames.samaritan.cheats.xray.obfuscation;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.samagames.samaritan.cheats.xray.OrebfuscatorConfig;
import net.samagames.samaritan.cheats.xray.internal.MinecraftInternals;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockUpdate {
    public static boolean needsUpdate(Block block) {
        return !OrebfuscatorConfig.isBlockTransparent(block.getTypeId());
    }

    public static void Update(Block block) {
        if (!needsUpdate(block)) {
            return;
        }

        Update(Arrays.asList(block));
    }

    public static void Update(List<Block> blocks) {
        if (blocks.isEmpty()) {
            return;
        }

        Set<BlockPosition> updateBlocks = new HashSet<>();
        for (Block block : blocks) {
            if (needsUpdate(block)) {
                BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
                updateBlocks.addAll(GetAjacentBlocks(block.getWorld(), new HashSet<>(), position, OrebfuscatorConfig.UpdateRadius));
            }
        }

        World world = blocks.get(0).getWorld();

        sendUpdates(world, updateBlocks);
    }

    private static void sendUpdates(World world, Set<BlockPosition> blocks) {
        for (BlockPosition block : blocks) {
            MinecraftInternals.notifyBlockChange(world, block);
        }
    }

    public static HashSet<BlockPosition> GetAjacentBlocks(World world, HashSet<BlockPosition> allBlocks, BlockPosition position, int countdown) {
        WorldServer w = ((CraftWorld)world).getHandle();
        int id = net.minecraft.server.v1_8_R3.Block.getId( w.getType(position).getBlock());
        if(w.isLoaded(position))
        {
            if ((OrebfuscatorConfig.isObfuscated(id, world.getEnvironment())
                    || OrebfuscatorConfig.isDarknessObfuscated(id))) {
                allBlocks.add(position);
            }

            if (countdown > 0) {
                GetAjacentBlocks(world, allBlocks, new BlockPosition(position.getX() + 1, position.getY(), position.getZ()), countdown - 1);
                GetAjacentBlocks(world, allBlocks, new BlockPosition(position.getX() - 1, position.getY(), position.getZ()), countdown - 1);
                GetAjacentBlocks(world, allBlocks, new BlockPosition(position.getX(), position.getY() + 1, position.getZ()), countdown - 1);
                GetAjacentBlocks(world, allBlocks, new BlockPosition(position.getX(), position.getY() - 1, position.getZ()), countdown - 1);
                GetAjacentBlocks(world, allBlocks, new BlockPosition(position.getX(), position.getY(), position.getZ() + 1), countdown - 1);
                GetAjacentBlocks(world, allBlocks, new BlockPosition(position.getX(), position.getY(), position.getZ() - 1), countdown - 1);
            }
        }

        return allBlocks;
    }
}
