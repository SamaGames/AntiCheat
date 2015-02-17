package net.samagames.anticheat;

import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by {USER}
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class ACPlayer {
    private Player player;

    public ACPlayer(Player player) {
        this.player = player;
    }

    public int getPing() {
        return ((CraftPlayer)player).getHandle().ping;
    }

}
