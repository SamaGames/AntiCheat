package net.samagames.anticheat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.samagames.anticheat.cheats.Cheats;
import net.samagames.anticheat.commands.CommandSamaritan;
import net.samagames.anticheat.database.PunishmentsManager;
import net.samagames.anticheat.globalListeners.NetworkListener;
import net.samagames.anticheat.globalListeners.PacketListener;
import net.samagames.anticheat.packets.TinyProtocol;
import net.samagames.tools.JsonConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class AntiCheat extends JavaPlugin
{
    private static AntiCheat instance;

    private HashMap<UUID, ACPlayer> acplayers;
    private PunishmentsManager punishmentsManager;
    private TinyProtocol protocol;
    private boolean developpmentExecution;

    public static AntiCheat getInstance()
    {
        return instance;
    }

    public void onEnable()
    {
        instance = this;

        this.acplayers = new HashMap<>();
        this.punishmentsManager = new PunishmentsManager();
        this.protocol = new PacketListener(this);

        this.saveResource("config.json", false);
        JsonObject jsonConfiguration = new JsonConfiguration(new File(this.getDataFolder(), "config.json")).load();
        JsonArray cheatsDisabled = jsonConfiguration.get("cheats-disabled").getAsJsonArray();

        this.developpmentExecution = jsonConfiguration.get("developpment-execution").getAsBoolean();

        for (int i = 0; i < cheatsDisabled.size(); i++)
        {
            Cheats cheat = Cheats.valueOf(cheatsDisabled.get(i).getAsString().toUpperCase());

            if (cheat != null)
            {
                cheat.disable();
                this.log(Level.INFO, "Cheat disabled: " + cheat.getIdentifier());
            }
        }

        Bukkit.getPluginCommand("samaritan").setExecutor(new CommandSamaritan());

        Bukkit.getPluginManager().registerEvents(new NetworkListener(), this);
        Bukkit.getOnlinePlayers().forEach(this::login);
    }

    public void onDisable()
    {
        for (ACPlayer acp : acplayers.values())
            acp.getCheats().values().forEach(net.samagames.anticheat.CheatTask::cancel);

        this.protocol.close();
    }

    public void login(Player player)
    {
        ACPlayer acp = new ACPlayer(player);

        for (Cheats cheat : Cheats.values())
        {
            if (cheat.isCurrentlyOnTest() && !this.developpmentExecution)
                continue;

            try
            {
                acp.addCheat(cheat, cheat.getCheatClass().getConstructor(Player.class).newInstance(player));

                if (this.developpmentExecution)
                    this.log(Level.INFO, "Listening cheat " + cheat.getIdentifier() + " for the player '" + player.getName() + "'.");
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
            {
                e.printStackTrace();
            }
        }

        this.acplayers.put(player.getUniqueId(), acp);
    }

    public void logout(Player player)
    {
        this.acplayers.remove(player.getUniqueId());
    }

    public void log(Level level, String phrase)
    {
        Bukkit.getLogger().log(level, phrase);
    }

    public PunishmentsManager getPunishmentsManager()
    {
        return this.punishmentsManager;
    }

    public ACPlayer getPlayer(UUID player)
    {
        return this.acplayers.get(player);
    }

    public boolean isDeveloppmentExecution()
    {
        return this.developpmentExecution;
    }
}
