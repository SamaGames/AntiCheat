package net.samagames.samaritan.cheats.killaura;

import net.samagames.samaritan.cheats.BasicCheatLog;
import net.samagames.samaritan.cheats.EnumCheat;
import net.samagames.samaritan.utils.LocationWrapper;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class KillauraCheatLog extends BasicCheatLog
{
    private Map<LocationWrapper, LocationWrapper> targetsHits = new HashMap<>();
    private int displayedCount;
    private int hitCount;

    protected KillauraCheatLog(OfflinePlayer player, Map<LocationWrapper, LocationWrapper> targetsHits, int numberTouched, int displayedCount)
    {
        super(player, EnumCheat.KILLAURA);

        this.targetsHits = targetsHits;
        this.displayedCount = displayedCount;
        this.hitCount = numberTouched;
    }
}