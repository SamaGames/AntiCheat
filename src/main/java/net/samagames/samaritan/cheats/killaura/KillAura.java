package net.samagames.samaritan.cheats.killaura;

import io.netty.channel.Channel;
import net.samagames.api.shadows.EnumPacket;
import net.samagames.api.shadows.IPacketListener;
import net.samagames.api.shadows.Packet;
import net.samagames.api.shadows.play.client.PacketUseEntity;
import net.samagames.samaritan.Samaritan;
import net.samagames.samaritan.cheats.CheatModule;
import net.samagames.samaritan.cheats.CheatTask;
import net.samagames.samaritan.cheats.EnumCheat;
import net.samagames.samaritan.player.VirtualPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

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
public class KillAura extends CheatModule implements IPacketListener
{
    @Override
    public void enable(Samaritan plugin)
    {
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
        return Collections.singletonList(PacketUseEntity.class);
    }

    @Override
    public void onPacket(Player player, Channel channel, Packet packet, EnumPacket.EnumPacketDirection networkDirection)
    {
        if (packet instanceof PacketUseEntity)
        {
            PacketUseEntity useEntity = (PacketUseEntity) packet;
            if (useEntity.getAction() == PacketUseEntity.Action.ATTACK)
            {
                VirtualPlayer virtualPlayer = VirtualPlayer.getVirtualPlayer(player);
                CheatTask task = virtualPlayer.getCheat(EnumCheat.KILLAURA);

                if (task != null && task instanceof KillAuraTask)
                {
                    ((KillAuraTask) virtualPlayer.getCheat(EnumCheat.KILLAURA)).onClick(useEntity.getEntityId());
                }
            }
        }
    }
}
