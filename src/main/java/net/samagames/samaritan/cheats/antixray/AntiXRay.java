package net.samagames.samaritan.cheats.antixray;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.World;
import net.samagames.api.shadows.EnumPacket;
import net.samagames.api.shadows.IPacketListener;
import net.samagames.api.shadows.Packet;
import net.samagames.api.shadows.play.server.PacketBlockChange;
import net.samagames.api.shadows.play.server.PacketChunkData;
import net.samagames.api.shadows.play.server.PacketChunkDataBulk;
import net.samagames.samaritan.Samaritan;
import net.samagames.samaritan.cheats.CheatModule;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk.ChunkMap;

/**
 * Created by Silva on 22/12/2015.
 */
public class AntiXRay extends CheatModule implements IPacketListener, Listener
{
    private ChunkCache cache = new ChunkCache();

    public AntiXRay() {


    }

    @Override
    public void enable(Samaritan plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().registerEvents(cache, plugin);
        plugin.getShadows().registerListener(this);
    }

    @Override
    public void disable(Samaritan plugin)
    {
        super.disable(plugin);
    }

    @Override
    public List<Class<? extends Packet>> getWhiteListedPackets()
    {
        return Arrays.asList(PacketChunkData.class, PacketChunkDataBulk.class, PacketBlockChange.class);
    }

    @Override
    public void onPacket(Player player, Channel channel, Packet packet, EnumPacket.EnumPacketDirection networkDirection)
    {
        if (packet instanceof PacketChunkData)
        {
            PacketChunkData chunkData = (PacketChunkData) packet;
            ChunkMap chunkMap = chunkData.getChunkMap();
            int locX = chunkData.getLocX();
            int locY = chunkData.getLocY();
            obfuscate(locX,
                    locY,
                    chunkMap.b,
                    chunkMap.a,
                    ((CraftWorld)player.getWorld()).getHandle());
        }else if (packet instanceof PacketChunkDataBulk)
        {
            PacketChunkDataBulk chunkData = (PacketChunkDataBulk) packet;
            int length = chunkData.getLocsX().length;
            for(int i = 0; i < length; i++)
            {
                ChunkMap chunkMap = chunkData.getChunksMap()[i];
                int locX = chunkData.getLocsX()[i];
                int locY = chunkData.getLocsY()[i];
                obfuscate(locX,
                        locY,
                        chunkMap.b,
                        chunkMap.a,
                        ((CraftWorld)player.getWorld()).getHandle());
            }
        }
    }

    /**
     * Removes all non exposed ores from the chunk buffer.
     */
    public void obfuscate(int chunkX, int chunkY, int bitmask, byte[] buffer, World world)
    {
        this.cache.applyCache(world, chunkX, chunkY, bitmask, buffer);
    }
}