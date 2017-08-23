package net.samagames.samaritan.cheats.packetspamming;

import io.netty.channel.Channel;
import net.samagames.api.shadows.EnumPacket;
import net.samagames.api.shadows.IPacketListener;
import net.samagames.api.shadows.Packet;
import net.samagames.api.shadows.play.client.PacketBlockDig;
import net.samagames.api.shadows.play.client.PacketPlayer;
import net.samagames.api.shadows.util.Direction;
import net.samagames.samaritan.Samaritan;
import net.samagames.samaritan.cheats.BasicCheatLog;
import net.samagames.samaritan.cheats.CheatModule;
import net.samagames.samaritan.cheats.EnumCheat;
import net.samagames.samaritan.player.VirtualPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * This file is part of AntiCheat (Samaritan).
 *
 * AntiCheat (Samaritan) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AntiCheat (Samaritan) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AntiCheat (Samaritan).  If not, see <http://www.gnu.org/licenses/>.
 */
public class PacketSpamming extends CheatModule implements IPacketListener, Runnable
{
    public Samaritan samaritan;

    private static final Map<String, Long> PACKET_TIMER = new HashMap<>();
    private static final Map<String, Integer> LISTENED_PACKET = new HashMap<>();
    private BukkitTask task;

    @Override
    public void enable(Samaritan plugin)
    {
        LISTENED_PACKET.put("PacketPlayer", 500);
        this.samaritan = plugin;
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this, 20L, 20L);
        plugin.getShadows().registerListener(this);
    }

    @Override
    public void disable(Samaritan plugin)
    {
        LISTENED_PACKET.clear();
        PACKET_TIMER.clear();
        task.cancel();
    }

    @Override
    public List<Class<? extends Packet>> getWhiteListedPackets()
    {
        return Arrays.asList(PacketPlayer.class, PacketBlockDig.class);
    }

    @Override
    public void onPacket(Player player, Channel channel, Packet packet, EnumPacket.EnumPacketDirection networkDirection)
    {
        VirtualPlayer vPlayer = VirtualPlayer.getVirtualPlayer(player);
        if (packet instanceof PacketPlayer)
        {
            String packetName = packet.getClass().getSimpleName();
            vPlayer.increasePacketCounter(packetName);
        }
        else if (packet instanceof PacketBlockDig)
        {
            PacketBlockDig dig = (PacketBlockDig) packet;

            // Check FastBow
            if (dig.getStatus().equals(PacketBlockDig.DigType.RELEASE_USE_ITEM))
            {
                Vector posVec = dig.getPosition();

                // Check if the packet is correct (BlockPos(0, 0, 0), Facing=DOWN)
                if (posVec.getX() == 0 && posVec.getY() == 0 && posVec.getZ() == 0 && dig.getDirection().equals(Direction.DOWN))
                {
                    Object lastPacket = vPlayer.getData("lastPacket-PFB");

                    // Possible packet overflow (FastBow?)
                    if (lastPacket != null && lastPacket instanceof PacketPlayer)
                    {
                        PacketPlayer packetPlayer = (PacketPlayer) lastPacket;

                        // You can't be on the air and have a fallDistance of 0, you are cheating, FastBow confirmed!
                        if (player.getFallDistance() <= 0 && !packetPlayer.isOnGround())
                        {
                            Integer i = vPlayer.getDataOrDefault("fastbow-alert", 0);
                            Boolean alreadyAlerted = vPlayer.getDataOrDefault("fastbow-alreadyAlerted", false);
                            if (i > 20 && !alreadyAlerted)
                            {
                                // Async, get the instance
                                samaritan.getPunishmentsManager().automaticBan(player, EnumCheat.FASTBOW, new BasicCheatLog(player, EnumCheat.FASTBOW));
                                vPlayer.setData("fastbow-alreadyAlerted", true);
                            }
                            else
                            {
                                i++;
                                vPlayer.setData("fastbow-alert", i);
                            }

                        }

                    }
                }
                // A normal client doesn't do that!
                else
                {
                    // TODO: Increase hack possibility percent or ignore (Maybe this will be used in the future by hacked client)
                }
            }
        }

        vPlayer.setData("lastPacket-PFB", packet);

    }

    @Override
    public void run()
    {
        for (final String packetName : LISTENED_PACKET.keySet())
        {
            Long lastTime = PACKET_TIMER.getOrDefault(packetName, -1L);
            if ((System.currentTimeMillis() - lastTime) > 10000)
            {
                PACKET_TIMER.put(packetName, System.currentTimeMillis());
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    VirtualPlayer vPlayer = VirtualPlayer.getVirtualPlayer(player);
                    final int packetCount = vPlayer.getPacketCounter(packetName);

                    if (packetCount > LISTENED_PACKET.get(packetName))
                    {
                        samaritan.getPunishmentsManager().automaticBan(player, EnumCheat.PACKET_SPAMMING, new BasicCheatLog(player, EnumCheat.PACKET_SPAMMING)
                        {
                            private Object packet = new Object()
                            {
                                private String name = packetName;
                                private int count = packetCount;
                                private int limit = LISTENED_PACKET.get(packetName);
                            };
                        });
                    }
                    vPlayer.resetPacketCounter(packetName);
                }
            }
        }
    }
}
