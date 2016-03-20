package net.samagames.samaritan.cheats.speedhack;

import net.samagames.samaritan.Samaritan;
import net.samagames.samaritan.cheats.BasicCheatLog;
import net.samagames.samaritan.cheats.CheatModule;
import net.samagames.samaritan.cheats.EnumCheat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedHack extends CheatModule implements Listener
{
    private Samaritan plugin;

    @Override
    public void enable(Samaritan plugin)
    {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**@EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (player.isInsideVehicle())
            return;

        Block under = player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();

        if (player.isSprinting())
        {
            if (under.getType() == Material.AIR)
            {
                this.plugin.getPunishmentsManager().automaticBan(player, EnumCheat.SPEEDHACK, new BasicCheatLog(player, EnumCheat.SPEEDHACK));
            }
            else if (event.getPlayer().getFoodLevel() <= 3)
            {
                this.plugin.getPunishmentsManager().automaticBan(player, EnumCheat.SPEEDHACK, new BasicCheatLog(player, EnumCheat.SPEEDHACK));
            }
            else
            {
                Location calculatedMax = event.getFrom().multiply(getSpeedMultiplier(player)).multiply(player.isSprinting() ? 1.4D : player.isSneaking() ? 0.5D : 1.0D);

                if (distance(event.getFrom(), event.getTo()) > distance(event.getFrom(), calculatedMax))
                    this.plugin.getPunishmentsManager().automaticBan(player, EnumCheat.SPEEDHACK, new BasicCheatLog(player, EnumCheat.SPEEDHACK));
            }
        }
    }

    private static double getSpeedMultiplier(Player player)
    {
        Block under = player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
        double multiplier = 1.0D;

        if (under.getType() == Material.ICE || under.getType() == Material.PACKED_ICE)
            multiplier *= 1.4D;

        if (player.getLocation().getBlock().getType() == Material.WEB)
            multiplier *= 0.12D;

        if (under.getType() == Material.AIR)
            multiplier *= 1.5D;

        multiplier *= getSpeedAmplifier(player);

        if (player.isBlocking())
            multiplier *= 0.5D;

        return multiplier;
    }

    private static double getSpeedAmplifier(Player player)
    {
        double amplifier = 1.0D;

        for (PotionEffect effect : player.getActivePotionEffects())
        {
            if (effect.getType() == PotionEffectType.SPEED)
                amplifier *= 1.0D + (0.2D * (effect.getAmplifier() + 1));

            if (effect.getType() == PotionEffectType.SLOW)
                amplifier *= 1.0D - (0.15D * (effect.getAmplifier() + 1));
        }

        return amplifier;
    }

    private static double distance(Location one, Location two)
    {
        double xSquare = (two.getX() - one.getX()) * (two.getX() - one.getX());
        double zSquare = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());

        return xSquare + zSquare;
    }**/
}
