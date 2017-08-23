package net.samagames.samaritan.cheats.killaura;

import net.samagames.samaritan.cheats.BasicCheatLog;
import net.samagames.samaritan.cheats.EnumCheat;
import net.samagames.samaritan.utils.LocationWrapper;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.Map;

/*
 * This file is part of AntiCheat (Samaritan).
 *
 * AntiCheat (Samaritan) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AntiCheat (Samaritan) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AntiCheat (Samaritan).  If not, see <http://www.gnu.org/licenses/>.
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