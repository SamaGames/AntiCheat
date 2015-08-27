package net.samagames.anticheat;

import net.samagames.anticheat.cheats.Cheats;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ACPlayer
{
    private HashMap<Cheats, CheatTask> cheats;
    private Player player;
    private double walkedDistance;
    private long startedSneak;
    private long startedSprinting;
    private boolean isSneaking = false;
    private boolean isSprinting = false;

    public ACPlayer(Player player)
    {
        this.player = player;
        this.cheats = new HashMap<>();
        this.isSneaking = false;
        this.isSprinting = false;
    }

    public void addCheat(Cheats cheat, CheatTask cheatTask)
    {
        this.cheats.put(cheat, cheatTask);
    }

    public void setSneaking(boolean sneaking)
    {
        if (!this.isSneaking && sneaking)
            this.startedSneak = System.currentTimeMillis();

        this.isSneaking = sneaking;
    }

    public void setSprinting(boolean sprinting)
    {
        if (!this.isSprinting && sprinting)
            this.startedSprinting = System.currentTimeMillis();

        this.isSprinting = sprinting;
    }

    public CheatTask getCheat(Cheats cheat)
    {
        return this.cheats.get(cheat);
    }

    public HashMap<Cheats, CheatTask> getCheats()
    {
        return this.cheats;
    }

    public int getPing()
    {
        return ((CraftPlayer) this.player).getHandle().ping;
    }

}
