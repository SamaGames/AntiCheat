package net.samagames.samaritan.cheats.xray.hook;

import io.netty.channel.Channel;
import net.samagames.api.shadows.EnumPacket;
import net.samagames.api.shadows.IPacketListener;
import net.samagames.api.shadows.Packet;
import net.samagames.api.shadows.play.client.PacketBlockDig;
import net.samagames.api.shadows.play.server.PacketChunkData;
import net.samagames.samaritan.cheats.xray.chunkmap.ChunkData;
import net.samagames.samaritan.cheats.xray.hithack.BlockHitManager;
import net.samagames.samaritan.cheats.xray.obfuscation.BlockUpdate;
import net.samagames.samaritan.cheats.xray.obfuscation.Calculations;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Silva on 23/12/2015.
 */
public class ShadowHook implements IPacketListener {
    @Override
    public List<Class<? extends Packet>> getWhiteListedPackets() {
        return Arrays.asList(PacketBlockDig.class, PacketChunkData.class);
    }

    @Override
    public void onPacket(Player player, Channel channel, Packet packet, EnumPacket.EnumPacketDirection networkDirection) {

        if(packet instanceof PacketChunkData)
        {
            packet.markDirty();
            PacketChunkData packetChunkData = (PacketChunkData)packet;
            ChunkData chunkData = new ChunkData();
            chunkData.chunkX = packetChunkData.getLocX();
            chunkData.chunkZ = packetChunkData.getLocY();
            chunkData.groundUpContinuous = packetChunkData.isGround_upContinuous();
            chunkData.primaryBitMask = packetChunkData.getSections();
            chunkData.data = packetChunkData.getData();
            chunkData.isOverworld = player.getWorld().getEnvironment() == World.Environment.NORMAL;

            try {
                byte[] newData = Calculations.obfuscateOrUseCache(chunkData, player);
                packetChunkData.setData(newData);
            } catch (IOException ignored) {}
        }
        else if(packet instanceof PacketBlockDig)
        {
            PacketBlockDig.DigType status = ((PacketBlockDig) packet).getStatus();
            if (status == PacketBlockDig.DigType.ABORT_DESTROY_BLOCK) {
                BlockHitManager.hitBlock(player, null);
            }
        }

    }
}
