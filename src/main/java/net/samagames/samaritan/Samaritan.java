package net.samagames.samaritan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.samagames.api.shadows.ShadowsAPI;
import net.samagames.samaritan.cheats.CheatTask;
import net.samagames.samaritan.cheats.EnumCheat;
import net.samagames.samaritan.command.CommandSamaritan;
import net.samagames.samaritan.listeners.ConnectionListener;
import net.samagames.samaritan.player.VirtualPlayer;
import net.samagames.tools.JsonConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class Samaritan extends JavaPlugin
{
    private Logger logger;
    private ShadowsAPI shadows;
    private static Samaritan instance;
    private boolean isDevMode;
    private PunishmentsManager punishmentsManager;
    private ConnectionListener connectionListener;

    @Override
    public void onEnable()
    {
        instance = this;
        logger = getLogger();
        shadows = ShadowsAPI.get();
        punishmentsManager = new PunishmentsManager(this);
        connectionListener = new ConnectionListener(this);
        this.getServer().getPluginManager().registerEvents(connectionListener, this);
        Bukkit.getPluginCommand("samaritan").setExecutor(new CommandSamaritan());


        this.saveResource("config.json", false);
        JsonObject jsonConfiguration = new JsonConfiguration(new File(this.getDataFolder(), "config.json")).load();
        if (jsonConfiguration != null)
        {
            JsonArray cheatsDisabled = jsonConfiguration.get("cheats-disabled").getAsJsonArray();

            this.isDevMode = jsonConfiguration.get("developpement-execution").getAsBoolean();

            for (int i = 0; i < cheatsDisabled.size(); i++)
            {
                EnumCheat cheat = EnumCheat.valueOf(cheatsDisabled.get(i).getAsString().toUpperCase());

                if (cheat != null)
                {
                    cheat.disable();
                    getLogger().log(Level.INFO, "Cheat disabled: " + cheat.getIdentifier());
                }
            }
        }

        for (EnumCheat cheat : EnumCheat.values())
        {
            logger.info("Activating " + cheat.getIdentifier() + "...");
            cheat.getCheatModule().enable(this);
        }
        logger.info("All cheats are ready to be detected!");
    }

    @Override
    public void onDisable()
    {
        for (EnumCheat cheat : EnumCheat.values())
        {
            cheat.getCheatModule().disable(this);
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            for (CheatTask task : VirtualPlayer.getVirtualPlayer(player).getCheats().values())
            {
                if (task == null)
                    continue;
                task.cancel();
            }
        }
    }

    public static Samaritan get()
    {
        return instance;
    }

    public void login(Player player)
    {
        VirtualPlayer virtalPlayer = VirtualPlayer.getVirtualPlayer(player);

        for (EnumCheat cheat : EnumCheat.values())
        {
            try
            {
                CheatTask obj = null;
                if (cheat.getCheatClass() != null)
                    obj = cheat.getCheatClass().getConstructor(Player.class).newInstance(player);
                virtalPlayer.addCheat(cheat, obj);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void logout(Player player)
    {
        VirtualPlayer.removeVirtualPlayer(player);
    }

    public PunishmentsManager getPunishmentsManager()
    {
        return punishmentsManager;
    }

    public boolean isDevModeEnabled()
    {
        return isDevMode;
    }

    public ShadowsAPI getShadows()
    {
        return shadows;
    }
}
