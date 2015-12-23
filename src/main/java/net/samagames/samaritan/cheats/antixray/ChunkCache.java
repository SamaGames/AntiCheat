package net.samagames.samaritan.cheats.antixray;

import gnu.trove.set.TByteSet;
import gnu.trove.set.hash.TByteHashSet;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.spigotmc.SpigotWorldConfig;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Silva on 23/12/2015.
 */
public class ChunkCache implements Listener {

    /*========================================================================*/
    // Used to keep track of which blocks to obfuscate
    private final boolean[] obfuscateBlocks = new boolean[ Short.MAX_VALUE ];
    // Used to select a random replacement ore
    private final byte[] replacementOres;

    //private final ConcurrentHashMap<Triple<Integer, Integer, String>, byte[][][]> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Triple<Integer, Integer, String>, char[]> cache = new ConcurrentHashMap<>();

    public ChunkCache()
    {
        SpigotWorldConfig config = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().spigotConfig;
        // Set all listed blocks as true to be obfuscated

        for ( int id : ( config.engineMode == 1 ) ? config.hiddenBlocks : config.replaceBlocks )
        {
            obfuscateBlocks[id] = true;
        }

        // For every block
        TByteSet blocks = new TByteHashSet();
        for ( Integer i : config.hiddenBlocks )
        {
            Block block = Block.getById( i );
            // Check it exists and is not a tile entity
            if ( block != null && !block.isTileEntity() )
            {
                // Add it to the set of replacement blocks
                blocks.add( (byte) (int) i );
            }
        }
        // Bake it to a flat array of replacements
        replacementOres = blocks.toArray();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event)
    {
        updateBlock(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockExplode(BlockExplodeEvent event)
    {
        updateBlock(event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPhysic(BlockPhysicsEvent event)
    {
        if(event.getChangedType().equals(Material.AIR))
            updateBlock(event.getBlock());
    }

    public char[] getCache(World world, int cx, int cy)
    {
        if(cache.containsKey(Triple.of(cx, cy, world.getWorld().getName())))
        {
            return cache.get(Triple.of(cx, cy, world.getWorld().getName()));
        }else{
            return loadChunk(world, cx, cy, 0, new byte[0]);
        }
    }

    public void applyCache(World world, int cx, int cy, int bitmask, byte[] buffer)
    {
        if(cache.containsKey(Triple.of(cx, cy, world.getWorld().getName())))
        {
            char[] bytes = cache.get(Triple.of(cx, cy, world.getWorld().getName()));
            int index = 0;

            // Chunks can have up to 16 sections
            for ( int i = 0; i < 16; i++ )
            {
                // If the bitmask indicates this chunk is sent...
                if ( ( bitmask & 1 << i ) != 0 )
                {
                    // Work through all blocks in the chunk, y,z,x
                    for ( int y = 0; y < 16; y++ )
                    {
                        for ( int z = 0; z < 16; z++ )
                        {
                            for ( int x = 0; x < 16; x++ )
                            {
                                // For some reason we can get too far ahead of ourselves (concurrent modification on bulk chunks?) so if we do, just abort and move on
                                if ( index >= buffer.length )
                                {
                                    index++;
                                    continue;
                                }
                                if(bytes[(( i << 4 ) + y) << 8 | z << 4 | x] >> 4 != -1)
                                {
                                    int newId = (bytes[(( i << 4 ) + y) << 8 | z << 4 | x] >> 4) & 0xFF;
                                    newId <<= 4;
                                    buffer[index << 1] = (byte) (newId & 0xFF);
                                    buffer[(index << 1) + 1] = (byte) ((newId >> 8) & 0xFF);
                                }

                                index++;
                            }
                        }
                    }
                }
            }
        }else{
            loadChunk(world, cx, cy, bitmask, buffer);
        }
    }

    public void unloadChunk(World world, int cx, int cy)
    {
        cache.remove(Triple.of(cx, cy, world.getWorld().getName()));
    }

    public char[] loadChunk(World world, int cx, int cy, int bitmask, byte[] buffer)
    {
        //HashMap<Triple<Integer, Integer, Integer>, Byte> patch = new HashMap<>();
        //byte[][][] patch1 = new byte[16][256][16];
        char[] patch = new char[4096*16];
        // If the world is marked as obfuscated

        // Initial radius to search around for air
        int initialRadius = 1;
        // Which block in the buffer we are looking at, anywhere from 0 to 16^4
        int index = 0;
        // The iterator marking which random ore we should use next
        int randomOre = 0;

        // Chunk corner X and Z blocks
        int startX = cx << 4;
        int startZ = cy << 4;

        ChunkSection[] sections = world.getChunkAt(cx, cy).getSections();
        // Chunks can have up to 16 sections
        for ( int i = 0; i < 16; i++ )
        {
            ChunkSection section = sections[i];
            if(section == null)
                continue;

            char[] idArray = section.getIdArray();
            // If the bitmask indicates this chunk is sent...
            boolean needSection = ( bitmask & 1 << i ) != 0;
            //load bear chunk
            for(int y = 0; y < 16; y++)
            {
                for(int z = 0; z < 16; z++)
                {
                    for(int x = 0; x < 16; x++)
                    {
                        byte newId = -1;
                         int blockId = idArray[y << 8 | z << 4 | x] >> 4;
                        //Samaritan.get().getLogger().info("" + blockId);
                        //if (obfuscateBlocks[Block.getId( world.getType(new BlockPosition(startX + x, ( i << 4 ) + y, startZ + z)).getBlock() )] )
                        if (obfuscateBlocks[blockId] && isLoaded( world, new BlockPosition( startX + x, ( i << 4 ) + y, startZ + z ), initialRadius ) )
                        {
                            // On the otherhand, if radius is 0, or the nearby blocks are all non air, we can obfuscate
                            if ( !hasTransparentBlockAdjacent( world, new BlockPosition( startX + x, ( i << 4 ) + y, startZ + z ), initialRadius ) )
                            {
                                // Replace with random ore.
                                if ( randomOre >= replacementOres.length )
                                {
                                    randomOre = 0;
                                }
                                newId = replacementOres[randomOre++] ;

                                //patch.put(Triple.of(x,( i << 4 )+y,z), newId);
                                if (needSection &&  index < buffer.length )
                                {
                                    int tnewId = newId << 4;
                                    buffer[index << 1] = (byte) (tnewId & 0xFF);
                                    buffer[(index << 1) + 1] = (byte) ((tnewId >> 8) & 0xFF);

                                }
                            }
                        }
                        //patch1[x][( i << 4 ) + y][z] = newId;
                        patch[y << 8 | z << 4 | x] = (char) (newId << 4);

                        index++;
                    }
                }
            }
        }
        cache.put(Triple.of(cx, cy, world.getWorld().getName()), patch);
        return patch;
    }

    private void updateBlock(org.bukkit.block.Block block)
    {

        updateNearbyBlocks(((CraftWorld)block.getWorld()).getHandle(), new BlockPosition(block.getX(),
                block.getY(),
                block.getZ()));


        //loadChunk(((CraftWorld)block.getWorld()).getHandle(), block.getChunk().getX(), block.getChunk().getZ());
    }

    /**
     * Starts the timings handler, then updates all blocks within the set radius
     * of the given coordinate, revealing them if they are hidden ores.
     */
    public void updateNearbyBlocks(World world, BlockPosition position)
    {
        updateNearbyBlocks( world, position, 2, false ); // 2 is the radius, we shouldn't change it as that would make it exponentially slower
        //Save the update
        int localX = position.getX() & 15;
        int localZ = position.getZ() & 15;
        cache.get(Triple.of(position.getX() >> 4, position.getZ() >> 4,
                world.getWorld().getName()))[position.getY() << 8 | localZ << 4 | localX] = (char) 0;
    }

    private void updateNearbyBlocks(World world, BlockPosition position, int radius, boolean updateSelf)
    {
        // If the block in question is loaded
        if ( world.isLoaded( position ) )
        {
            int x = position.getX() & 15;
            int y = position.getX() & 15;
            int z = position.getX() & 15;
            // Get block id
            ChunkSection section = world.getChunkAtWorldCoords(position).getSections()[position.getY() >> 4];
            if(section != null)
            {
                int id = section.getIdArray()[y << 8 | z << 4 | x] >> 4;
                // See if it needs update
                if (updateSelf  && obfuscateBlocks[id])
                {
                    // Send the update
                    world.notify( position );
                    //Save the update
                    int localX = position.getX() & 15;
                    int localZ = position.getZ() & 15;
                    cache.get(Triple.of(position.getX() >> 4, position.getZ() >> 4,
                            world.getWorld().getName()))[position.getY() << 8 | localZ << 4 | localX] = (char) (id << 4);
                }
            }
            // Check other blocks for updates
            if ( radius > 0 )
            {
                updateNearbyBlocks( world, position.east(), radius - 1, true );
                updateNearbyBlocks( world, position.west(), radius - 1, true );
                updateNearbyBlocks( world, position.up(), radius - 1, true );
                updateNearbyBlocks( world, position.down(), radius - 1, true );
                updateNearbyBlocks( world, position.south(), radius - 1, true );
                updateNearbyBlocks( world, position.north(), radius - 1, true );
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

    private static boolean isSolidBlock(Block block) {
        // Mob spawners are treated as solid blocks as far as the
        // game is concerned for lighting and other tasks but for
        // rendering they can be seen through therefor we special
        // case them so that the antixray doesn't show the fake
        // blocks around them.
        return block.isOccluding() && block != Blocks.MOB_SPAWNER && block != Blocks.BARRIER;
    }

    private static boolean isLoaded(World world, BlockPosition position, int radius)
    {
        return world.isLoaded( position )
                && ( radius == 0 ||
                ( isLoaded( world, position.east(), radius - 1 )
                        && isLoaded( world, position.west(), radius - 1 )
                        && isLoaded( world, position.up(), radius - 1 )
                        && isLoaded( world, position.down(), radius - 1 )
                        && isLoaded( world, position.south(), radius - 1 )
                        && isLoaded( world, position.north(), radius - 1 ) ) );
    }
}
