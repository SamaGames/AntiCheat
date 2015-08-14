package net.samagames.anticheat.cheats;

import net.samagames.anticheat.CheatTask;
import net.samagames.anticheat.cheats.killaura.KillAura;

public enum Cheats
{
    KILLAURA("KillAura", "ForceField/KillAura", KillAura.class, true),
    ;

    private final String identifier;
    private final String banReason;
    private final Class<? extends CheatTask> cheatClass;
    private final boolean currentlyOnTest;
    private boolean enabled;

    Cheats(String identifier, String banReason, Class<? extends CheatTask> cheatClass, boolean currentlyOnTest)
    {
        this.identifier = identifier;
        this.banReason = banReason;
        this.cheatClass = cheatClass;
        this.currentlyOnTest = currentlyOnTest;
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

    public boolean isCurrentlyOnTest()
    {
        return this.currentlyOnTest;
    }
}
