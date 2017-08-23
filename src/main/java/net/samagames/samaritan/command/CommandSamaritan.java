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