package net.samagames.anticheat;

import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ACPlayer {
    public HashMap<String,  CheatTask> cheats = new HashMap<>();
    public double walkedDistance = 0.0;
    public boolean isSneaking = false;
    public long startedSneak = 0;
    public boolean iSprinting = false;
    public long startedSprinting = 0;
    private Player player;

    public ACPlayer(Player player) {
        this.player = player;
    }

    public void setSneaking(boolean sneaking)
    {
        if(!isSneaking && sneaking)
        {
            startedSneak = System.currentTimeMillis();
        }
        isSneaking = sneaking;
    }

    public void setSprinting(boolean sprinting)
    {
        if(!iSprinting && sprinting)
        {
            startedSprinting = System.currentTimeMillis();
        }
        iSprinting = sprinting;
    }

    public void addCheat(String name, CheatTask classe)
    {
        cheats.put(name, classe);
    }

    public CheatTask getCheat(String name)
    {
        return cheats.get(name);
    }

    public int getPing() {
        return ((CraftPlayer)player).getHandle().ping;
    }

}
