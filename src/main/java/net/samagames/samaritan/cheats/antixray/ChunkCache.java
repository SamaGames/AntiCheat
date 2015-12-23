package net.samagames.samaritan.cheats.antixray;

import gnu.trove.set.TByteSet;
import gnu.trove.set.hash.TByteHashSet;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Blocks;
import net.minecraft.server.v1_8_R3.World;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
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

    ConcurrentHashMap<Triple<Integer, Integer, String>, byte[][][]> cache = new ConcurrentHashMap<>();

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

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event)
    {
        int locX = event.getChunk().getX();
        int locY = event.getChunk().getZ();
        /*Bukkit.getScheduler().runTaskAsynchronously(Samaritan.get(), () -> {
            try {
                loadChunk(((CraftWorld)event.getWorld()).getHandle(), locX, locY);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        });*/
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event)
    {
        int locX = event.getChunk().getX();
        int locY = event.getChunk().getZ();

        //Bukkit.getScheduler().runTask(Samaritan.get(), () -> );
        //unloadChunk(((CraftWorld)event.getWorld()).getHandle(), locX, locY);
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
        /*if(event.getChangedType().equals(Material.AIR))
            updateBlock(event.getBlock());*/
    }

    public byte[][][] getCache(World world, int cx, int cy)
    {
        if(cache.containsKey(Triple.of(cx, cy, world.getWorld().getName())))
        {
            return cache.get(Triple.of(cx, cy, world.getWorld().getName()));
        }else{
            return loadChunk(world, cx, cy);
        }
    }

    public void unloadChunk(World world, int cx, int cy)
    {
        cache.remove(Triple.of(cx, cy, world.getWorld().getName()));
    }

    public byte[][][] loadChunk(World world, int cx, int cy)
    {
        //HashMap<Triple<Integer, Integer, Integer>, Byte> patch = new HashMap<>();
        byte[][][] patch1 = new byte[16][256][16];
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

        //load bear chunk
        world.getChunkAt(cx+1, cy);
        world.getChunkAt(cx-1, cy);
        world.getChunkAt(cx, cy+1);
        world.getChunkAt(cx, cy-1);
        for(int x = 0; x < 16; x++)
        {
            for(int y = 0; y < 256; y++)
            {
                for(int z = 0; z < 16; z++)
                {
                    byte newId = -1;
                    // int blockId = idArray[y << 8 | z << 4 | x];
                    //Samaritan.get().getLogger().info("" + blockId);
                    if (obfuscateBlocks[Block.getId( world.getType(new BlockPosition(startX + x, y, startZ + z)).getBlock() )] )
                    {
                        // On the otherhand, if radius is 0, or the nearby blocks are all non air, we can obfuscate
                        if ( !hasTransparentBlockAdjacent( world, new BlockPosition( startX + x, y, startZ + z ), initialRadius ) )
                        {
                            // Replace with random ore.
                            if ( randomOre >= replacementOres.length )
                            {
                                randomOre = 0;
                            }
                            newId = replacementOres[randomOre++] ;

                            //patch.put(Triple.of(x,( i << 4 )+y,z), newId);
                        }
                    }
                    patch1[x][y][z] = newId;
                }
            }
        }


        cache.put(Triple.of(cx, cy, world.getWorld().getName()), patch1);
        return patch1;
    }

    private void updateBlock(org.bukkit.block.Block block)
    {
        /*Bukkit.getScheduler().runTaskAsynchronously(Samaritan.get(), new Runnable() {
            @Override
            public void run() {*/
                updateNearbyBlocks(((CraftWorld)block.getWorld()).getHandle(), new BlockPosition(block.getX(),
                        block.getY(),
                        block.getZ()));/*
            }
        });*/

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
                world.getWorld().getName()))[localX][position.getY()][localZ] = (byte) 0;
    }

    private void updateNearbyBlocks(World world, BlockPosition position, int radius, boolean updateSelf)
    {
        // If the block in question is loaded
        if ( world.isLoaded( position ) )
        {
            // Get block id
            Block block = world.getType(position).getBlock();
            int id = Block.getId( block );
            // See if it needs update

            if (updateSelf  && obfuscateBlocks[id])
            {
                // Send the update
                world.notify( position );
                //Save the update
                int localX = position.getX() & 15;
                int localZ = position.getZ() & 15;
                cache.get(Triple.of(position.getX() >> 4, position.getZ() >> 4,
                        world.getWorld().getName()))[localX][position.getY()][localZ] = (byte) id;
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
                && world.getChunkAtWorldCoords(position).areNeighborsLoaded(radius);
    }
}
