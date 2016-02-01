package net.samagames.samaritan.command;

import net.md_5.bungee.api.ChatColor;
import net.samagames.api.SamaGamesAPI;
import net.samagames.samaritan.cheats.EnumCheat;
import net.samagames.samaritan.cheats.killaura.KillAuraTask;
import net.samagames.samaritan.player.VirtualPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class CommandSamaritan implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if(strings.length <= 0)
        {
            commandSender.sendMessage(ChatColor.RED + "RUN ANOTHER SIMULATION, MACHINE! RUN ANOTHER DAMN SIMULATION! #RIP");
            return true;
        }

        if (SamaGamesAPI.get().getPermissionsManager().hasPermission(commandSender, "anticheat.check"))
        {
            if(strings.length < 1)
                return false;

            if (strings[0].equals("check"))
            {
                if(strings.length >= 2)
                {
                    Player player = Bukkit.getPlayer(strings[1]);

                    if(player == null)
                    {
                        commandSender.sendMessage(ChatColor.RED + "Erreur nom du joueur");
                        return true;
                    }

                    VirtualPlayer virtualPlayer = VirtualPlayer.getVirtualPlayer(player);

                    if(virtualPlayer == null)
                    {
                        commandSender.sendMessage(ChatColor.RED + "Erreur joueur non surveillé par Samaritian ! Vigilance !");
                        return true;
                    }

                    KillAuraTask killAura = (KillAuraTask) virtualPlayer.getCheat(EnumCheat.KILLAURA);

                    if(killAura == null)
                    {
                        commandSender.sendMessage(ChatColor.RED + "Erreur KillAura désactivé");
                        return true;
                    }

                    killAura.testNow();

                    commandSender.sendMessage(ChatColor.GREEN + "Un test de KillAura vient d'être fait sur: " + player.getName());
                    commandSender.sendMessage(ChatColor.GOLD + "Si le joueur fait un mouvement brusque vers l'arrière c'est qu'il cheat !");
                    return true;
                }
            }

            return true;
        }

        return true;
    }
}