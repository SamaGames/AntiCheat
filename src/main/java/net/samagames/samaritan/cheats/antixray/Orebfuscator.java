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

import net.samagames.samaritan.Samaritan;
import net.samagames.samaritan.cheats.CheatModule;
import net.samagames.samaritan.cheats.antixray.cache.ObfuscatedDataCache;
import net.samagames.samaritan.cheats.antixray.hithack.BlockHitManager;
import net.samagames.samaritan.cheats.antixray.hook.ChunkProcessingThread;
import net.samagames.samaritan.cheats.antixray.hook.OrebfuscatorPlayerHook;
import net.samagames.samaritan.cheats.antixray.hook.ShadowHook;
import net.samagames.samaritan.cheats.antixray.internal.MinecraftInternals;
import net.samagames.samaritan.cheats.antixray.listeners.OrebfuscatorBlockListener;
import net.samagames.samaritan.cheats.antixray.listeners.OrebfuscatorEntityListener;
import net.samagames.samaritan.cheats.antixray.listeners.OrebfuscatorPlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

/**
 * Orebfuscator Anti X-RAY
 *
 * @author lishid
 */
public class Orebfuscator extends CheatModule {

    public static final Logger logger = Logger.getLogger("Minecraft.XRAY");
    public static Orebfuscator instance;

    @Override
    public void enable(Samaritan plugin) {
        // Get plugin manager
        PluginManager pm = plugin.getServer().getPluginManager();
        synchronized (this)
        {
            instance = this;
        }

        // Load configurations
        OrebfuscatorConfig.load();

        // Orebfuscator events
        pm.registerEvents(new OrebfuscatorPlayerListener(), plugin);
        pm.registerEvents(new OrebfuscatorEntityListener(), plugin);
        pm.registerEvents(new OrebfuscatorBlockListener(), plugin);

        pm.registerEvents(new OrebfuscatorPlayerHook(), plugin);

        plugin.getShadows().registerListener(new ShadowHook());

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
    }

    @Override
    public void disable(Samaritan plugin) {
        super.disable(plugin);
        ObfuscatedDataCache.clearCache();
        BlockHitManager.clearAll();
        ChunkProcessingThread.KillAll();
    }

    public void runTask(Runnable task) {
        Samaritan.get().getServer().getScheduler().runTask(Samaritan.get(), task);
    }

    /**
     * Log an information
     */
    public static void log(String text) {
        logger.info("[Xray] " + text);
    }

    /**
     * Log an error
     */
    public static void log(Throwable e) {
        logger.severe("[Xray] " + e.toString());
        e.printStackTrace();
    }

    /**
     * Send a message to a player
     */
    public static void message(CommandSender target, String message) {
        target.sendMessage(ChatColor.AQUA + "[Xray] " + message);
    }
}
