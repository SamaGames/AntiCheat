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
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.samagames.api.shadows.play.server.PacketChunkData;
import net.samagames.api.shadows.play.server.PacketChunkDataBulk;
import net.samagames.samaritan.cheats.xray.OrebfuscatorConfig;
import net.samagames.samaritan.cheats.xray.cache.ObfuscatedCachedChunk;
import net.samagames.samaritan.cheats.xray.internal.ChunkData;
import net.samagames.samaritan.cheats.xray.internal.Packet51;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class Calculations {
    public static final int BYTES_PER_BLOCK = 2;

    public static final int BLOCKS_PER_SECTION = 16 * 16 * 16;
    public static final int BYTES_PER_SECTION = BYTES_PER_BLOCK * BLOCKS_PER_SECTION;

    public static final int MAX_SECTIONS_PER_CHUNK = 16;
    public static final int MAX_BYTES_PER_CHUNK = BYTES_PER_SECTION * MAX_SECTIONS_PER_CHUNK;


    public static final ThreadLocal<byte[]> buffer = new ThreadLocal<byte[]>() {
        protected byte[] initialValue() {
            return new byte[MAX_BYTES_PER_CHUNK];
        }
    };

    private static Map<Player, Map<ChunkAddress, Set<MinecraftBlock>>> signsMap = new WeakHashMap<Player, Map<ChunkAddress, Set<MinecraftBlock>>>();

    private static Map<ChunkAddress, Set<MinecraftBlock>> getPlayerSignsMap(Player player) {
        Map<ChunkAddress, Set<MinecraftBlock>> map = signsMap.get(player);
        if (map == null) {
            map = new HashMap<ChunkAddress, Set<MinecraftBlock>>();
            signsMap.put(player, map);
        }
        return map;
    }

    private static void putSignsList(Player player, int chunkX, int chunkZ, Set<MinecraftBlock> blocks) {
        Map<ChunkAddress, Set<MinecraftBlock>> map = getPlayerSignsMap(player);
        ChunkAddress address = new ChunkAddress(chunkX, chunkZ);
        map.put(address, blocks);
    }

    public static Set<MinecraftBlock> getSignsList(Player player, int chunkX, int chunkZ) {
        Map<ChunkAddress, Set<MinecraftBlock>> map = getPlayerSignsMap(player);
        ChunkAddress address = new ChunkAddress(chunkX, chunkZ);
        return map.get(address);
    }

    public static void putSignsList(Player player, int chunkX, int chunkZ, List<Block> proximityBlocks) {
        Set<MinecraftBlock> signs = new HashSet<MinecraftBlock>();
        for (Block b : proximityBlocks) {
            if (b.getState() instanceof Sign) {
                signs.add(new MinecraftBlock(b));
            }
        }
        putSignsList(player, chunkX, chunkZ, signs);
    }

    public static void Obfuscate(PacketChunkData packet, Player player) {
        // Assuming that NoLagg will pass a Packet51
        Packet51 packet51 = new Packet51();
        packet51.setPacket(packet);
        Calculations.Obfuscate(packet51, player);
        packet.getChunkMap().a = packet51.getChunkData().buffer;
        packet.getChunkMap().b = packet51.getChunkData().mask;
    }

    public static void Obfuscate(PacketChunkDataBulk packet, Player player) {
        ChunkInfo[] infos = getInfo(packet, player);

        int i = 0;
        for (ChunkInfo info : infos) {
            ComputeChunkInfoAndObfuscate(info);
            packet.getChunksMap()[i].a = info.original;
            packet.getChunksMap()[i].b = info.chunkMask;
            i++;
        }
    }

    public static void Obfuscate(Packet51 packet, Player player) {
        ChunkInfo info = getInfo(packet, player);

        if (info.chunkMask == 0) {
            return;
        }

        ComputeChunkInfoAndObfuscate(info);
    }

    public static ChunkInfo[] getInfo(PacketChunkDataBulk packet, Player player) {
        ChunkInfo[] infos = new ChunkInfo[packet.getChunksMap().length];

        for (int i = 0; i < packet.getChunksMap().length; i++) {
            // Create an info objects
            ChunkInfo info = new ChunkInfo(player,
                    new ChunkData(packet.getChunksMap()[i],
                            packet.getLocsX()[i],
                            packet.getLocsY()[i]),
                    buffer.get());
            infos[i] = info;
        }

        return infos;
    }

    public static ChunkInfo getInfo(Packet51 packet, Player player) {
        ChunkInfo info = new ChunkInfo(player, packet.getChunkData(), buffer.get());
        return info;
    }

    public static void ComputeChunkInfoAndObfuscate(ChunkInfo info) {
        // Obfuscate
        if (!OrebfuscatorConfig.isWorldDisabled(info.world.getName()) && // World not enabled
                OrebfuscatorConfig.obfuscateForPlayer(info.player) && // Should the player have obfuscation?
                OrebfuscatorConfig.Enabled) // Plugin enabled
        {
            byte[] obfuscated = Obfuscate(info);
            // Copy the data out of the buffer
            System.arraycopy(obfuscated, 0, info.original, 0, info.bytes);
        }
    }

    public static byte[] Obfuscate(ChunkInfo info) {
        Environment environment = info.world.getEnvironment();
        // Used for caching
        ObfuscatedCachedChunk cache = null;
        // Hash used to check cache consistency
        long hash = 0L;
        // Blocks kept track for ProximityHider
        ArrayList<Block> proximityBlocks = new ArrayList<Block>();
        // Start with caching false
        info.useCache = false;

        int initialRadius = OrebfuscatorConfig.InitialRadius;

        // Copy data into buffer
        System.arraycopy(info.original, 0, info.buffer, 0, info.bytes);

        // Caching
        if (OrebfuscatorConfig.UseCache) {
            // Sanitize buffer for caching
            PrepareBufferForCaching(info.buffer, info.bytes);

            // Get cache folder
            File cacheFolder = new File(OrebfuscatorConfig.getCacheFolder(), info.world.getName());
            // Create cache objects
            cache = new ObfuscatedCachedChunk(cacheFolder, info.chunkX, info.chunkZ);
            info.useCache = true;
            // Hash the chunk
            hash = CalculationsUtil.Hash(info.buffer, info.bytes);

            // Check if hash is consistent
            cache.Read();

            long storedHash = cache.getHash();
            int[] proximityList = cache.proximityList;

            if (storedHash == hash && cache.data != null) {
                // Decrypt chest list
                if (proximityList != null) {
                    for (int i = 0; i < proximityList.length; i += 3) {
                        Block b = CalculationsUtil.getBlockAt(info.player.getWorld(), proximityList[i], proximityList[i + 1], proximityList[i + 2]);
                        proximityBlocks.add(b);
                    }
                }

                // Caching done, de-sanitize buffer
                RepaintChunkToBuffer(cache.data, info);

                // ProximityHider add blocks
                putSignsList(info.player, info.chunkX, info.chunkZ, proximityBlocks);
                ProximityHider.AddProximityBlocks(info.player, proximityBlocks);

                // Hash match, use the cached data instead and skip calculations
                return cache.data;
            }
        }

        // Track of pseudo-randomly assigned randomBlock
        int randomIncrement = 0;
        int randomIncrement2 = 0;
        int ramdomCave = 0;
        // Track of whether a block should be obfuscated or not
        boolean obfuscate = false;
        boolean specialObfuscate = false;

        int engineMode = OrebfuscatorConfig.EngineMode;
        int maxChance = OrebfuscatorConfig.AirGeneratorMaxChance;
        int incrementMax = maxChance;

        int randomBlocksLength = OrebfuscatorConfig.getRandomBlocks(false, environment).length;
        boolean randomAlternate = false;

        int startX = info.chunkX << 4;
        int startZ = info.chunkZ << 4;

        int index = 0;

        for (int i = 0; i < 16; i++) {
            if ((info.chunkMask & 1 << i) != 0) {
                OrebfuscatorConfig.shuffleRandomBlocks();
                for (int y = 0; y < 16; y++) {
                    for (int z = 0; z < 16; z++) {
                        incrementMax = (maxChance + OrebfuscatorConfig.random(maxChance)) / 2;
                        for (int x = 0; x < 16; x++) {
                            int blockData = chunkGetBlockData(info.original, index);
                            int blockMeta = blockDataToMeta(blockData);
                            int blockId = blockDataToId(blockData);

                            if (blockId >= 256) {
                                index++;
                                continue;
                            }

                            int blockY = (i << 4) + y;

                            // Initialize data
                            obfuscate = false;
                            specialObfuscate = false;

                            WorldServer worldServer = ((CraftWorld) info.world).getHandle();

                            // Check if the block should be obfuscated for the default engine modes
                            if (OrebfuscatorConfig.isObfuscated(blockId, environment)) {
                                if (initialRadius == 0) {
                                    // Do not interfere with PH
                                    if (OrebfuscatorConfig.UseProximityHider && OrebfuscatorConfig.isProximityObfuscated(blockY, blockId)) {
                                        if (!hasTransparentBlockAdjacent(worldServer, new BlockPosition(startX + x, blockY, startZ + z), 1)) {
                                            obfuscate = true;
                                        }
                                    } else {
                                        // Obfuscate all blocks
                                        obfuscate = true;
                                    }
                                } else {
                                    // Check if any nearby blocks are transparent
                                    if (!hasTransparentBlockAdjacent(worldServer, new BlockPosition(startX + x, blockY, startZ + z), initialRadius)) {
                                        obfuscate = true;
                                    }
                                }
                            }

                            // Check if the block should be obfuscated because of proximity check
                            if (!obfuscate && OrebfuscatorConfig.UseProximityHider && OrebfuscatorConfig.isProximityObfuscated(blockY, blockId)) {
                                if (OrebfuscatorConfig.isProximityHiderOn(blockY, blockId)) {
                                    Block block = CalculationsUtil.getBlockAt(info.player.getWorld(), startX + x, blockY, startZ + z);
                                    if (block != null) {
                                        proximityBlocks.add(block);
                                    }
                                    obfuscate = true;
                                    if (OrebfuscatorConfig.UseSpecialBlockForProximityHider) {
                                        specialObfuscate = true;
                                    }
                                }
                            }

                            // Check if the block is obfuscated
                            if (obfuscate) {
                                //Samaritan.get().getLogger().info("je suis ici");
                                if (specialObfuscate) {
                                    // Proximity hider
                                    blockId = OrebfuscatorConfig.ProximityHiderID;
                                } else {
                                    randomIncrement2 = OrebfuscatorConfig.random(incrementMax);
                                    // CalculationsUtil.increment(randomIncrement2, incrementMax);

                                    if (engineMode == 1) {
                                        // Engine mode 1, replace with stone
                                        blockId = (environment == Environment.NETHER ? 87 : 1);
                                    } else if (engineMode == 2) {
                                        // Ending mode 2, replace with random block
                                        if (randomBlocksLength > 1)
                                            randomIncrement = CalculationsUtil.increment(randomIncrement, randomBlocksLength);
                                        blockId = OrebfuscatorConfig.getRandomBlock(randomIncrement, randomAlternate, environment);
                                        randomAlternate = !randomAlternate;
                                    }
                                    // Anti texturepack and freecam
                                    if (OrebfuscatorConfig.AntiTexturePackAndFreecam) {
                                    // Add random air blocks
                                        if (randomIncrement2 == 0) {
                                            ramdomCave = 1 + OrebfuscatorConfig.random(3);
                                        }

                                        if (ramdomCave > 0) {
                                            blockId = 0;
                                            ramdomCave--;
                                        }
                                    }
                                }

                                blockMeta = 0;
                            }

                            // Check if the block should be obfuscated because of the darkness
                            if (!obfuscate && OrebfuscatorConfig.DarknessHideBlocks && OrebfuscatorConfig.isDarknessObfuscated(blockId)) {
                                if (!areAjacentBlocksBright(info, startX + x, (i << 4) + y, startZ + z, 1)) {
                                    // Hide block, setting it to air
                                    blockId = 0;
                                    blockMeta = 0;
                                }
                            }

                            chunkSetBlockIdMeta(info.buffer, index, blockId, blockMeta);
                            index++;
                        }
                    }
                }
            }
        }

        putSignsList(info.player, info.chunkX, info.chunkZ, proximityBlocks);
        ProximityHider.AddProximityBlocks(info.player, proximityBlocks);

        // If cache is still allowed
        if (info.useCache) {
            // Save cache
            int[] proximityList = new int[proximityBlocks.size() * 3];
            for (int i = 0; i < proximityBlocks.size(); i++) {
                Block b = proximityBlocks.get(i);
                if (b != null) {
                    proximityList[i * 3] = b.getX();
                    proximityList[i * 3 + 1] = b.getY();
                    proximityList[i * 3 + 2] = b.getZ();
                }
            }
            cache.Write(hash, info.buffer, proximityList);
        }

        // Free memory taken by cache quickly
        if (cache != null) {
            cache.free();
        }

        // Caching done, de-sanitize buffer
        if (OrebfuscatorConfig.UseCache) {
            RepaintChunkToBuffer(info.buffer, info);
        }

        return info.buffer;
    }

    //16 bit char for block data, including 12 bits for block id
    private static final int BLOCKID_MAX = 4096;
    private static char[] cacheMap = new char[BLOCKID_MAX];

    static {
        buildCacheMap();
    }

    public static void buildCacheMap() {
        for (int i = 0; i < 4096; i++) {
            cacheMap[i] = (char) i;
            if (OrebfuscatorConfig.isBlockTransparent((short) i) && !isBlockSpecialObfuscated(64, (char) i)) {
                cacheMap[i] = 0;
            }
        }
    }

    private static void PrepareBufferForCaching(byte[] data, int bytes) {
        for (int i = 0; i < bytes / 2; i++) {
            int blockId = chunkGetBlockId(data, i);

            blockId = cacheMap[blockId % BLOCKID_MAX];

            chunkSetBlockId(data, i, blockId);
        }
    }

    private static boolean isBlockSpecialObfuscated(int y, char id) {
        if (OrebfuscatorConfig.DarknessHideBlocks && OrebfuscatorConfig.isDarknessObfuscated(id)) {
            return true;
        }
        if (OrebfuscatorConfig.UseProximityHider && OrebfuscatorConfig.isProximityObfuscated(y, id)) {
            return true;
        }
        return false;
    }

    private static void RepaintChunkToBuffer(byte[] data, ChunkInfo info) {
        byte[] original = info.original;
        int bytes = info.bytes;

        for (int i = 0; i < bytes / 2; i++) {
            int newId = chunkGetBlockId(data, i);
            int originalId = chunkGetBlockId(original, i);

            if (newId == 0 && originalId != 0) {
                if (OrebfuscatorConfig.isBlockTransparent((short) originalId)) {
                    if (!isBlockSpecialObfuscated(0, (char) originalId)) {
                        chunkSetBlockId(data, i, originalId);
                    }
                }
            }
        }
    }

    private static boolean hasTransparentBlockAdjacent(World world, BlockPosition position, int radius)
    {
        return !isSolidBlock(world.getType(position, false).getBlock()) /* isSolidBlock */
                || ( radius > 0
                && ( hasTransparentBlockAdjacent( world, position.east(), radius - 1 )
                || hasTransparentBlockAdjacent( world, position.west(), radius - 1 )
                || hasTransparentBlockAdjacent( world, position.up(), radius - 1 )
                || hasTransparentBlockAdjacent( world, position.down(), radius - 1 )
                || hasTransparentBlockAdjacent( world, position.south(), radius - 1 )
                || hasTransparentBlockAdjacent( world, position.north(), radius - 1 ) ) );
    }

    private static boolean isSolidBlock(net.minecraft.server.v1_8_R3.Block block) {
        // Mob spawners are treated as solid blocks as far as the
        // game is concerned for lighting and other tasks but for
        // rendering they can be seen through therefor we special
        // case them so that the antixray doesn't show the fake
        // blocks around them.
        return block.isOccluding() && block != Blocks.MOB_SPAWNER && block != Blocks.BARRIER;
    }

    public static boolean areAjacentBlocksBright(ChunkInfo info, int x, int y, int z, int countdown) {
        if (CalculationsUtil.isChunkLoaded(info.world, x >> 4, z >> 4)) {
            if (info.world.getBlockAt(x, y, z).getLightLevel() > 0) {
                return true;
            }
        } else {
            return true;
        }

        if (countdown == 0)
            return false;

        if (areAjacentBlocksBright(info, x, y + 1, z, countdown - 1))
            return true;
        if (areAjacentBlocksBright(info, x, y - 1, z, countdown - 1))
            return true;
        if (areAjacentBlocksBright(info, x + 1, y, z, countdown - 1))
            return true;
        if (areAjacentBlocksBright(info, x - 1, y, z, countdown - 1))
            return true;
        if (areAjacentBlocksBright(info, x, y, z + 1, countdown - 1))
            return true;
        if (areAjacentBlocksBright(info, x, y, z - 1, countdown - 1))
            return true;

        return false;
    }

    /**
     * Blocks are 2-bytes aligned
     * Every 2 bytes represents the block data as:
     * First byte = lower 8 bits
     * Second byte = upper 8 bits
     *
     * @param buffer
     * @param index
     * @return
     */
    private static int chunkGetBlockData(byte[] buffer, int index) {
        index = index << 1;
        return (buffer[index] & 0xFF) | ((buffer[index + 1] & 0xFF) << 8);
    }

    private static int chunkGetBlockId(byte[] buffer, int index) {
        return chunkGetBlockData(buffer, index) >> 4;
    }

    private static int blockDataToId(int blockData) {
        return blockData >> 4;
    }

    private static int blockDataToMeta(int blockData) {
        return blockData & 0xF;
    }

    private static int blockIdMetaToData(int blockId, int blockMeta) {
        return blockMeta | (blockId << 4);
    }

    private static void chunkSetBlockId(byte[] buffer, int index, int id) {
        int blockData = chunkGetBlockData(buffer, index);

        chunkSetBlockIdMeta(buffer, index, id, blockDataToMeta(blockData));
    }

    private static void chunkSetBlockIdMeta(byte[] buffer, int index, int id, int meta) {
        int blockData = blockIdMetaToData(id, meta);
        chunkSetBlockData(buffer, index, blockData);
    }

    private static void chunkSetBlockData(byte[] buffer, int index, int data) {
        index = index << 1;
        buffer[index] = (byte) (data & 0xFF);
        buffer[index + 1] = (byte) ((data >> 8) & 0xFF);
    }
}