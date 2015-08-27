package net.samagames.anticheat.cheats.killaura;

import net.samagames.anticheat.cheats.Cheats;
import net.samagames.anticheat.database.BasicCheatLog;
import net.samagames.anticheat.utils.VirtualLocation;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;

public class KillauraCheatLog extends BasicCheatLog
{
    private HashMap<VirtualLocation, VirtualLocation> targetsHits = new HashMap<>();
    private int displayedCount;
    private int hitCount;

    protected KillauraCheatLog(OfflinePlayer player, HashMap<VirtualLocation, VirtualLocation> targetsHits, int numberTouched, int displayedCount)
    {
        super(player, Cheats.KILLAURA);

        this.targetsHits = targetsHits;
        this.displayedCount = displayedCount;
        this.hitCount = numberTouched;
    }
}
