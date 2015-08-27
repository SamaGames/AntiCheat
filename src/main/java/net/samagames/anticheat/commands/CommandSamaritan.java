package net.samagames.anticheat.commands;

import net.samagames.anticheat.ACPlayer;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.cheats.Cheats;
import net.samagames.anticheat.cheats.killaura.KillAura;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSamaritan implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {
        if (strings.length <= 0)
        {
            commandSender.sendMessage(ChatColor.RED + "RUN ANOTHER SIMULATION, MACHINE! RUN ANOTHER DAMN SIMULATION! #RIP");
            return true;
        }

        if (SamaGamesAPI.get().getPermissionsManager().hasPermission(commandSender, "anticheat.check"))
        {
            if (strings.length < 1)
                return false;

            if (strings[0].equals("check"))
            {
                if (strings.length >= 2)
                {
                    Player player = Bukkit.getPlayer(strings[1]);

                    if (player == null)
                    {
                        commandSender.sendMessage(ChatColor.RED + "Erreur nom du joueur");
                        return true;
                    }

                    ACPlayer acp = AntiCheat.getInstance().getPlayer(player.getUniqueId());

                    if (acp == null)
                    {
                        commandSender.sendMessage(ChatColor.RED + "Erreur joueur non surveillé par Samaritian ! Vigilance !");
                        return true;
                    }

                    KillAura killAura = (KillAura) acp.getCheat(Cheats.KILLAURA);

                    if (killAura == null)
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
