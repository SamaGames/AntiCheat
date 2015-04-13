package net.samagames.anticheat.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

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
        commandSender.sendMessage(ChatColor.RED + "RUN ANOTHER SIMULATION, MACHINE! RUN ANOTHER DAMN SIMULATION!");
        /*if (! PermissionsBukkit.hasPermission(commandSender, "anticheat.command"))   {

            return true;
        }*/

        return true;
    }
}
