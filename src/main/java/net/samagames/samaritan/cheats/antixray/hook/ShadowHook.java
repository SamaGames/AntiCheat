package net.samagames.samaritan.cheats.antixray.hook;

import io.netty.channel.Channel;
import net.samagames.api.shadows.EnumPacket;
import net.samagames.api.shadows.IPacketListener;
import net.samagames.api.shadows.Packet;
import net.samagames.api.shadows.play.client.PacketBlockDig;
import net.samagames.api.shadows.play.server.PacketChunkData;
import net.samagames.samaritan.cheats.antixray.hithack.BlockHitManager;
import net.samagames.samaritan.cheats.antixray.obfuscation.BlockUpdate;
import net.samagames.samaritan.cheats.antixray.obfuscation.Calculations;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
            Calculations.Obfuscate((PacketChunkData) packet, player);
        }else if(packet instanceof PacketBlockDig)
        {
            PacketBlockDig.DigType status = ((PacketBlockDig) packet).getStatus();
            if (status == PacketBlockDig.DigType.ABORT_DESTROY_BLOCK) {
                if (!BlockHitManager.hitBlock(player, null)) {
                    //can't cancel packet
                }

            }
            if(status == PacketBlockDig.DigType.START_DESTROY_BLOCK)
            {
                Vector vector = ((PacketBlockDig) packet).getPosition();
                Location location = new Location(player.getWorld(), vector.getX(), vector.getY(), vector.getZ());
                BlockUpdate.Update(location.getBlock());
            }
        }

    }
}
