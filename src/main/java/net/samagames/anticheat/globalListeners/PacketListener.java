package net.samagames.anticheat.globalListeners;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.samagames.anticheat.ACPlayer;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.cheats.Cheats;
import net.samagames.anticheat.cheats.killaura.KillAura;
import net.samagames.anticheat.packets.TinyProtocol;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

public class PacketListener extends TinyProtocol
{
    public PacketListener(Plugin plugin) {
        super(plugin);
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet)
    {
        if(sender == null)
            return super.onPacketInAsync(sender, channel, packet);

        ACPlayer acp = AntiCheat.getInstance().getPlayer(sender.getUniqueId());

        if(acp == null)
            if(sender == null)
                return super.onPacketInAsync(sender, channel, packet);

        if(packet instanceof PacketPlayInUseEntity)
        {
            PacketPlayInUseEntity p = (PacketPlayInUseEntity)packet;

            if(p.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)
            {
                int id = -1;

                try
                {
                    Field a = p.getClass().getDeclaredField("a");
                    a.setAccessible(true);
                    id = (int) a.get(p);
                }
                catch (NoSuchFieldException | IllegalAccessException e)
                {
                    e.printStackTrace();
                }

                KillAura killAura = ((KillAura) acp.getCheat(Cheats.KILLAURA));

                if(killAura != null)
                {
                    killAura.onClick(id);
                }
            }
        }
        else if(packet instanceof PacketPlayInFlying.PacketPlayInPosition)
        {
            /*PacketPlayInFlying.PacketPlayInPosition p = (PacketPlayInFlying.PacketPlayInPosition) packet;
            SpeedHack speedHack = ((SpeedHack) acp.getCheat(Cheats.SPEEDHACK));

            if(speedHack != null)
                speedHack.updateLocation(p.a(), p.b(), p.c());*/
        }
        else if(packet instanceof PacketPlayInEntityAction)
        {
            /*PacketPlayInEntityAction p = (PacketPlayInEntityAction)packet;

            SpeedHack speedHack = ((SpeedHack) acp.getCheat(Cheats.SPEEDHACK));

            if(speedHack != null)
                speedHack.playerAction(p.b());*/
        }

        return super.onPacketInAsync(sender, channel, packet);
    }
}
