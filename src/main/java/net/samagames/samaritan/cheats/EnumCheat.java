package net.samagames.samaritan.cheats;

import net.samagames.samaritan.cheats.packetspamming.PacketSpamming;
import net.samagames.samaritan.cheats.speedhack.SpeedHack;
import net.samagames.samaritan.cheats.xray.Orebfuscator;
import net.samagames.samaritan.cheats.killaura.KillAura;
import net.samagames.samaritan.cheats.killaura.KillAuraTask;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public enum EnumCheat
{
    KILLAURA("KillAura", "ForceField/KillAura", KillAuraTask.class, KillAura.class, false),
    PACKET_SPAMMING("PacketSpamming", "Packet Spamming", null, PacketSpamming.class, true),
    FASTBOW("FastBow", "FastBow", null, CheatModule.class, true),
    XRAY("XRay", "XRay", null, Orebfuscator.class, true),
    SPEEDHACK("SpeedHack", "SpeedHack", null, SpeedHack.class, true),
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
