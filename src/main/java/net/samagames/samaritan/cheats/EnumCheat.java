package net.samagames.samaritan.cheats;

import net.samagames.samaritan.cheats.packetspamming.PacketSpamming;
import net.samagames.samaritan.cheats.speedhack.SpeedHack;
import net.samagames.samaritan.cheats.xray.Orebfuscator;
import net.samagames.samaritan.cheats.killaura.KillAura;
import net.samagames.samaritan.cheats.killaura.KillAuraTask;

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
public enum EnumCheat
{
    KILLAURA("KillAura", "ForceField/KillAura", KillAuraTask.class, KillAura.class, false),
    PACKET_SPAMMING("PacketSpamming", "Packet Spamming", null, PacketSpamming.class, true),
    FASTBOW("FastBow", "FastBow", null, CheatModule.class, true),
    XRAY("XRay", "XRay", null, Orebfuscator.class, true),
    //SPEEDHACK("SpeedHack", "SpeedHack", null, SpeedHack.class, true),
    ;

    private final String identifier;
    private final String banReason;
    private final Class<? extends CheatTask> cheatClass;
    private final boolean beta;
    private CheatModule cheatModule;
    private boolean enabled;

    EnumCheat(String identifier, String banReason, Class<? extends CheatTask> cheatTaskClass, Class<? extends CheatModule> cheatModule, boolean beta)
    {
        this.identifier = identifier;
        this.banReason = banReason;
        this.cheatClass = cheatTaskClass;
        try
        {
            this.cheatModule = cheatModule.newInstance();
        } catch (ReflectiveOperationException e)
        {
            this.cheatModule = new CheatModule();
        }

        this.beta = beta;
        this.enabled = true;
    }

    public void disable()
    {
        this.enabled = false;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public String getBanReason()
    {
        return this.banReason;
    }

    public Class<? extends CheatTask> getCheatClass()
    {
        return this.cheatClass;
    }

    public boolean isBeta()
    {
        return this.beta;
    }

    public CheatModule getCheatModule()
    {
        return cheatModule;
    }
}
