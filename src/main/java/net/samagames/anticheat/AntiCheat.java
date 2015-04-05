package net.samagames.anticheat;


import net.samagames.anticheat.globalListeners.NetworkListener;
import net.samagames.anticheat.speedhack.KillAura;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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

    public void onEnable() {
        instance = this;

        //cheats.add(SpeedHack.class);
        cheats.add(KillAura.class);

        Bukkit.getPluginManager().registerEvents(new NetworkListener(), this);

        for (Player player : Bukkit.getOnlinePlayers()) {
            login(player);
        }
    }

    public void broadcast(String message)
    {

    }
}
