/*
 * Copyright (C) 2011-2014 lishid.  All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package net.samagames.samaritan.cheats.antixray;

import io.netty.channel.Channel;
import net.samagames.api.shadows.EnumPacket;
import net.samagames.api.shadows.IPacketListener;
import net.samagames.api.shadows.Packet;
import net.samagames.api.shadows.play.server.PacketChunkData;
import net.samagames.api.shadows.play.server.PacketChunkDataBulk;
import net.samagames.samaritan.Samaritan;
import net.samagames.samaritan.cheats.CheatModule;
import net.samagames.samaritan.cheats.antixray.cache.ObfuscatedDataCache;
import net.samagames.samaritan.cheats.antixray.hithack.BlockHitManager;
import net.samagames.samaritan.cheats.antixray.hook.ChunkProcessingThread;
import net.samagames.samaritan.cheats.antixray.hook.OrebfuscatorPlayerHook;
import net.samagames.samaritan.cheats.antixray.internal.MinecraftInternals;
import net.samagames.samaritan.cheats.antixray.listeners.OrebfuscatorBlockListener;
import net.samagames.samaritan.cheats.antixray.listeners.OrebfuscatorEntityListener;
import net.samagames.samaritan.cheats.antixray.listeners.OrebfuscatorPlayerListener;
import net.samagames.samaritan.cheats.antixray.obfuscation.Calculations;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;
import java.util.List;

/**
 * Orebfuscator Anti X-RAY
 *
 * @author lishid
 */
public class Orebfuscator extends CheatModule implements IPacketListener {

    public static Orebfuscator instance;
    private static Samaritan plugin;

    @Override
    public void enable(Samaritan plugin)
    {
        this.plugin = plugin;
        // Get plugin manager
        PluginManager pm = plugin.getServer().getPluginManager();

        instance = this;
        // Load configurations
        OrebfuscatorConfig.load();

        // Orebfuscator events
        pm.registerEvents(new OrebfuscatorPlayerListener(), plugin);
        pm.registerEvents(new OrebfuscatorEntityListener(), plugin);
        pm.registerEvents(new OrebfuscatorBlockListener(), plugin);

        pm.registerEvents(new OrebfuscatorPlayerHook(), plugin);

        // Disable spigot's built-in orebfuscator since it has limited functionality
        try {
            Class.forName("org.spigotmc.AntiXray");
            Orebfuscator.log("Spigot found! Automatically disabling built-in AntiXray.");
            for (World world : plugin.getServer().getWorlds()) {
                MinecraftInternals.tryDisableSpigotAntiXray(world);
            }
        } catch (Exception e) {
            // Spigot not found
        }

        plugin.getShadows().registerListener(this);
    }

    @Override
    public void disable(Samaritan plugin)
    {
        super.disable(plugin);
        ObfuscatedDataCache.clearCache();
        BlockHitManager.clearAll();
        ChunkProcessingThread.KillAll();
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    public static void log(String message)
    {
        plugin.getLogger().info(message);
    }
    public static void log(Exception message)
    {
        message.printStackTrace();
    }

    public static Samaritan getPlugin() {
        return plugin;
    }

    @Override
    public List<Class<? extends Packet>> getWhiteListedPackets()
    {
        return Arrays.asList(PacketChunkData.class, PacketChunkDataBulk.class);
    }

    @Override
    public void onPacket(Player player, Channel channel, Packet packet, EnumPacket.EnumPacketDirection networkDirection) {
        if (packet instanceof PacketChunkData)
        {
            PacketChunkData chunkData = (PacketChunkData) packet;
            Calculations.Obfuscate(chunkData, player);
        }else if (packet instanceof PacketChunkDataBulk)
        {
            PacketChunkDataBulk chunkData = (PacketChunkDataBulk) packet;
            Calculations.Obfuscate(chunkData, player);
        }
    }
}
