package net.samagames.anticheat;


import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R1.EnumEntityUseAction;
import net.minecraft.server.v1_8_R1.PacketPlayInUseEntity;
import net.samagames.anticheat.database.BanRules;
import net.samagames.anticheat.database.PunishmentsManager;
import net.samagames.anticheat.globalListeners.NetworkListener;
import net.samagames.anticheat.packets.TinyProtocol;
import net.samagames.anticheat.speedhack.KillAura;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by {USER}
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class AntiCheat extends JavaPlugin implements Listener {

    public static HashMap<UUID, ACPlayer> acplayers = new HashMap<>();
    public static HashSet<Class<? extends CheatTask>> cheats = new HashSet<>();
    public static AntiCheat instance;
    public static PunishmentsManager punishmentsManager;
	public static BanRules banRules;

    public TinyProtocol protocol;

    public static void login(Player player) {
        ACPlayer acp = new ACPlayer(player);
        for (Class<? extends CheatTask> cheat : cheats) {
            try {
                acp.addCheat(cheat.getSimpleName(), cheat.getConstructor(Player.class).newInstance(player));
                log("Load :" + cheat.getSimpleName());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        acplayers.put(player.getUniqueId(), acp);

        log("Added "+ player.getName());
    }

    public static void logout(Player player) {
        acplayers.remove(player.getUniqueId());
    }

    public static ACPlayer getPlayer(UUID player) {
        return acplayers.get(player);
    }

    public static void log(String phrase) {
        Bukkit.getLogger().info(phrase);
    }

    public static void log(Level level, String phrase) {
        Bukkit.getLogger().log(level, phrase);
    }

    public static void broadcastSamaritan(String message)
    {
        String prefix = ChatColor.RED + "[" + ChatColor.GRAY + "Samaritan" + ChatColor.RED + "] ";
        Bukkit.broadcastMessage(prefix + ChatColor.RED + message);
    }

    public static void broadcastGreer(String message)
    {
        String prefix = ChatColor.RED + "[" + ChatColor.GRAY + "Greer" + ChatColor.RED + "] ";
        Bukkit.broadcastMessage(prefix + ChatColor.RED + message);
    }

    public void onEnable() {
        instance = this;

		banRules = new BanRules(this);
        punishmentsManager = new PunishmentsManager();
        //cheats.add(SpeedHack.class);
        cheats.add(KillAura.class);

        protocol = new TinyProtocol(this) {
            @Override
            public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                if(packet instanceof PacketPlayInUseEntity)
                {
                    PacketPlayInUseEntity p = (PacketPlayInUseEntity)packet;
                    if(p.a() == EnumEntityUseAction.ATTACK)
                    {
                        ACPlayer acp = getPlayer(sender.getUniqueId());
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

                        ((KillAura)acp.getCheat("KillAura")).onClick(id);
                    }
                }

                return super.onPacketInAsync(sender, channel, packet);
            }
        };

        Bukkit.getPluginManager().registerEvents(new NetworkListener(), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            login(player);
        }
    }
}
