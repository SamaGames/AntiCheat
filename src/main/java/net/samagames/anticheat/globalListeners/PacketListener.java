package net.samagames.anticheat.globalListeners;

import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R1.EnumEntityUseAction;
import net.minecraft.server.v1_8_R1.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R1.PacketPlayInPosition;
import net.minecraft.server.v1_8_R1.PacketPlayInUseEntity;
import net.samagames.anticheat.ACPlayer;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.cheats.killaura.KillAura;
import net.samagames.anticheat.cheats.speedhack.SpeedHack;
import net.samagames.anticheat.packets.TinyProtocol;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 11/04/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class PacketListener extends TinyProtocol{
    /**
     * Construct a new instance of TinyProtocol, and start intercepting packets for all connected clients and future clients.
     * <p/>
     *
     * @param plugin - the plugin.
     */
    public PacketListener(Plugin plugin) {
        super(plugin);
    }

    @Override
    public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
        if(sender == null)
            return super.onPacketInAsync(sender, channel, packet);
        ACPlayer acp = AntiCheat.instance.getPlayer(sender.getUniqueId());
        if(acp == null)
            if(sender == null)
                return super.onPacketInAsync(sender, channel, packet);

        if(packet instanceof PacketPlayInUseEntity)
        {
            PacketPlayInUseEntity p = (PacketPlayInUseEntity)packet;
            if(p.a() == EnumEntityUseAction.ATTACK)
            {
                int id = -1;
                try {
                    Field a = p.getClass().getDeclaredField("a");
                    a.setAccessible(true);
                    id = (int) a.get(p);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                KillAura killAura = ((KillAura) acp.getCheat("KillAura"));
                if(killAura != null)
                {
                    killAura.onClick(id);
                }
            }
        }else if(packet instanceof PacketPlayInPosition)
        {
            PacketPlayInPosition p = (PacketPlayInPosition)packet;
            SpeedHack speedHack = ((SpeedHack) acp.getCheat("SpeedHack"));
            if(speedHack != null)
            {
                speedHack.updateLocation(
                        p.a(),
                        p.b(),
                        p.c());
            }
        }else if(packet instanceof PacketPlayInEntityAction)
        {
            PacketPlayInEntityAction p = (PacketPlayInEntityAction)packet;

            SpeedHack speedHack = ((SpeedHack) acp.getCheat("SpeedHack"));
            if(speedHack != null)
            {
                speedHack.playerAction(p.b());
            }
        }

        return super.onPacketInAsync(sender, channel, packet);
    }
}
