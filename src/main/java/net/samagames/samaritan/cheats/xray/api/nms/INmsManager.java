/**
 * @author Aleksey Terzi
 *
 */

package net.samagames.samaritan.cheats.xray.api.nms;

import net.samagames.samaritan.cheats.xray.api.types.BlockCoord;
import net.samagames.samaritan.cheats.xray.api.types.BlockState;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface INmsManager {
    void setMaxLoadedCacheFiles(int value);

    INBT createNBT();

    IChunkCache createChunkCache();

    IChunkManager getChunkManager(World world);

    void updateBlockTileEntity(BlockCoord blockCoord, Player player);

    void notifyBlockChange(World world, IBlockInfo blockInfo);

    int getBlockLightLevel(World world, int x, int y, int z);

    IBlockInfo getBlockInfo(World world, int x, int y, int z);

    BlockState getBlockState(World world, int x, int y, int z);

    int getBlockId(World world, int x, int y, int z);
}