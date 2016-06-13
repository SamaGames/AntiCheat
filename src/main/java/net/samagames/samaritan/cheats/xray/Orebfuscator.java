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

package net.samagames.samaritan.cheats.xray;

import java.util.logging.Logger;

import net.samagames.api.shadows.ShadowsAPI;
import net.samagames.samaritan.Samaritan;
import net.samagames.samaritan.cheats.CheatModule;
import net.samagames.samaritan.cheats.xray.api.nms.INmsManager;
import net.samagames.samaritan.cheats.xray.cache.ObfuscatedDataCache;
import net.samagames.samaritan.cheats.xray.hithack.BlockHitManager;
import net.samagames.samaritan.cheats.xray.hook.ShadowHook;
import net.samagames.samaritan.cheats.xray.listeners.OrebfuscatorBlockListener;
import net.samagames.samaritan.cheats.xray.listeners.OrebfuscatorChunkListener;
import net.samagames.samaritan.cheats.xray.listeners.OrebfuscatorEntityListener;
import net.samagames.samaritan.cheats.xray.listeners.OrebfuscatorPlayerListener;
import net.samagames.samaritan.cheats.xray.v1_9_R2.NmsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

/**
 * Orebfuscator Anti X-RAY
 *
 * @author lishid
 */
public class Orebfuscator extends CheatModule
{

    public static final Logger logger = Logger.getLogger("Minecraft.OFC");
    public static Orebfuscator instance;

    public static INmsManager nms;

    private Samaritan plugin;

    @Override
    public void enable(Samaritan plugin) {
        // Get plugin manager
        PluginManager pm = plugin.getServer().getPluginManager();

        instance = this;
        nms = createNmsManager();
        this.plugin = plugin;

        // Load configurations
        OrebfuscatorConfig.load();

        // Orebfuscator events
        pm.registerEvents(new OrebfuscatorPlayerListener(), plugin);
        pm.registerEvents(new OrebfuscatorEntityListener(), plugin);
        pm.registerEvents(new OrebfuscatorBlockListener(), plugin);
        pm.registerEvents(new OrebfuscatorChunkListener(), plugin);

        ShadowsAPI.get().registerListener(new ShadowHook());
    }

    private static INmsManager createNmsManager() {
        return new NmsManager();
    }

    @Override
    public void disable(Samaritan plugin) {
        ObfuscatedDataCache.closeCacheFiles();
        BlockHitManager.clearAll();
        plugin.getServer().getScheduler().cancelTasks(plugin);
    }

    public void runTask(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    /**
     * Log an information
     */
    public static void log(String text) {
        logger.info("[OFC] " + text);
    }

    /**
     * Log an error
     */
    public static void log(Throwable e) {
        logger.severe("[OFC] " + e.toString());
        e.printStackTrace();
    }

    /**
     * Send a message to a player
     */
    public static void message(CommandSender target, String message) {
        target.sendMessage(ChatColor.AQUA + "[OFC] " + message);
    }
}