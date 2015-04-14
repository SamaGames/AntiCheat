package net.samagames.anticheat.commands;

import net.samagames.anticheat.ACPlayer;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.cheats.killaura.KillAura;
import net.samagames.permissionsbukkit.PermissionsBukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 13/04/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class CommandSamaritan implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(strings.length <= 0)
        {
            commandSender.sendMessage(ChatColor.RED + "RUN ANOTHER SIMULATION, MACHINE! RUN ANOTHER DAMN SIMULATION!");
            return true;
        }

        if (PermissionsBukkit.hasPermission(commandSender, "anticheat.command"))   {
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
                    ACPlayer acp = AntiCheat.getPlayer(player.getUniqueId());
                    if(acp == null)
                    {
                        commandSender.sendMessage(ChatColor.RED + "Erreur joueur non surveillé par Samaritian ! Vigilance !");
                        return true;
                    }

                    KillAura killAura = (KillAura) acp.getCheat("KillAura");
                    if(killAura == null)
                    {
                        commandSender.sendMessage(ChatColor.RED + "Erreur KillAura désactivé");
                        return true;
                    }
                    killAura.nextTest = System.currentTimeMillis();

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
