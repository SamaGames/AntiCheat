package net.samagames.samaritan.cheats.killaura;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.*;
import net.samagames.samaritan.cheats.CheatTask;
import net.samagames.samaritan.cheats.EnumCheat;
import net.samagames.samaritan.util.LocationWrapper;
import net.samagames.samaritan.util.MathUtils;
import net.samagames.samaritan.util.VectorUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

// v3: Rewrite this...
@Deprecated
public class KillAuraTask extends CheatTask
{
    private final long CHECK_INTERVAL = 2 * 60 * 1000;
    private final int CHECK_DURATION = 15;
    private final int CHECK_TO_DISPLAY = 10;
    private final double PERCENT_FOR_BAN = 7;

    private HashMap<LocationWrapper, LocationWrapper> touched;
    private HashMap<Integer, Integer> angles;
    private List<Vector> positionsTemplate;
    private EntityHuman target;
    private Location targetLocation;
    private Location startingPlayerLocation;
    private int numberTouched;
    private int numberDisplayed;
    private int countDown;
    private long nextTest;
    private boolean activeCheck;
    private boolean isTouched;

    public KillAuraTask(final Player player)
    {
        super(player, true);

        this.touched = new HashMap<>();
        this.angles = new HashMap<>();
        this.positionsTemplate = new ArrayList<>();
        this.nextTest = System.currentTimeMillis();
        this.numberDisplayed = 1;
        this.activeCheck = true;
        this.isTouched = false;

        this.positionsTemplate.add(new Vector(3, 2.5, 1.5));
        this.positionsTemplate.add(new Vector(2.5, 1, -2));
        this.positionsTemplate.add(new Vector(0, 0.5, 4));
        this.positionsTemplate.add(new Vector(0, 4, -2.5));
        this.positionsTemplate.add(new Vector(0, 4, 2.5));
        this.positionsTemplate.add(new Vector(3, 0.5, -3));
        this.positionsTemplate.add(new Vector(-4, 0.5, 2));
        this.positionsTemplate.add(new Vector(0, 4.5, 0));
        this.positionsTemplate.add(new Vector(3, 0.2, 3));
        this.positionsTemplate.add(new Vector(-2, 4.5, 0));

        this.resetAngles();
    }

    public void resetAngles()
    {
        this.angles.put(-180, 0);
        this.angles.put(-90, 0);
        this.angles.put(90, 0);
        this.angles.put(180, 0);
    }

    public void onClick(int entityID)
    {
        if (this.targetLocation == null || this.target == null)
            return;

        if (this.target.getId() != entityID)
            return;

        touchedTarget();
    }

    @Override
    public void exec()
    {
        long time = System.currentTimeMillis();

        if (this.targetLocation != null && this.target != null)
        {
            this.countDown--;

            if (this.countDown <= 0)
            {
                this.destroyTarget();
                this.countDown = 0;
                this.workingJob();
            }
        }

        if (!this.activeCheck)
            return;

        if (time > this.nextTest)
            launchCheck();
    }

    /***
     * Engine Side
     ***/

    public void touchedTarget()
    {
        this.touched.put(new LocationWrapper(this.targetLocation.clone()), new LocationWrapper(this.player.getLocation().clone()));
        this.isTouched = true;
        this.numberTouched++;

        this.destroyTarget();
        this.workingJob();
    }

    public void workingJob()
    {
        if (this.startingPlayerLocation == null)
            this.startingPlayerLocation = this.player.getLocation();

        if (!this.isTouched && this.numberDisplayed <= 1)
            return;

        if (!this.isTouched)
            this.touched.put(null, new LocationWrapper(player.getLocation().clone()));

        this.isTouched = false;

        if (this.numberDisplayed < this.CHECK_TO_DISPLAY)
        {
            this.generateTarget(this.getRandomPlayer(), this.getRandomLocationAroundPlayer(this.startingPlayerLocation));
            this.countDown = this.CHECK_DURATION;
            this.numberDisplayed++;

            return;
        }

        if (this.numberTouched >= this.PERCENT_FOR_BAN)
        {
            samaritan.getPunishmentsManager().automaticBan(this.player, EnumCheat.KILLAURA, new KillauraCheatLog(this.player, this.touched, this.numberTouched, this.numberDisplayed));

            this.numberTouched = 0;
            this.numberDisplayed = 1;
            this.startingPlayerLocation = null;
        }
    }

    public void launchCheck()
    {
        this.numberTouched = 0;
        this.numberDisplayed = 1;
        this.startingPlayerLocation = null;

        this.resetAngles();
        this.destroyTarget();
        this.generateTarget(this.getRandomPlayer(), this.getLocationBehondPlayer(this.player.getLocation(), 2));

        this.countDown = this.CHECK_DURATION;
        this.nextTest = System.currentTimeMillis() + this.CHECK_INTERVAL;
    }

    public void destroyTarget()
    {
        if (target == null || targetLocation == null)
            return;
        target.die();
        sendPacket(player, generateDestroyPacket(target));
        target = null;
        targetLocation = null;
        //isTouched = false;
    }

    public void generateTarget(Player ptarget, Location position)
    {
        boolean needToDlSkin = true;
        GameProfile target = null;

        if (ptarget != null)
        {
            target = ((CraftPlayer) ptarget).getProfile();
            needToDlSkin = false;
        }

        if (target == null)
            target = this.randomGameProfile();

        if (position == null)
            position = this.getLocationBehondPlayer(this.player.getLocation(), 2);

        final EntityPlayer entityHuman = generatePlayer(position, target);
        this.target = entityHuman;
        this.targetLocation = position;

        if (needToDlSkin)
            this.sendPacket(this.player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityHuman));

        this.sendPacket(this.player, this.generateSpawnPacket(entityHuman));

        if (needToDlSkin)
            this.sendPacket(this.player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityHuman));
    }

    public void testNow()
    {
        this.nextTest = System.currentTimeMillis();
    }

    /***
     * Locations side
     ***/

    public Location getLocationBehondPlayer(Location referential, double radius)
    {
        float yaw = referential.getYaw();
        float finalYaw = yaw - 90;

        double relativeX = Math.cos(Math.toRadians(finalYaw)) * radius;
        double relativeZ = Math.sin(Math.toRadians(finalYaw)) * radius;
        double relativeY = -0.90;

        return new Location(referential.getWorld(),
                referential.getX() + relativeX,
                referential.getY() + relativeY,
                referential.getZ() + relativeZ
        );
    }

    public Location getRandomLocationAroundPlayer(Location referential)
    {
        Vector relativePos = this.positionsTemplate.get((this.numberDisplayed - 1) % this.positionsTemplate.size());
        VectorUtils.rotateAroundAxisY(relativePos, -referential.getYaw() * MathUtils.degreesToRadians);

        return referential.clone().add(relativePos);
    }

    /***
     * Packet side
     ***/

    public EntityPlayer generatePlayer(Location loc, GameProfile gameProfile)
    {
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        PlayerInteractManager playerInteractManager = new PlayerInteractManager(world);

        EntityPlayer entityHuman = new EntityPlayer(world.getServer().getServer(), world, gameProfile, playerInteractManager);
        entityHuman.setPosition(loc.getX(), loc.getY(), loc.getZ());
        entityHuman.setSneaking(true);
        entityHuman.setInvisible(true);

        return entityHuman;
    }

    public void sendPacket(Player p, Packet packet)
    {
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public Packet generateSpawnPacket(EntityHuman entityHuman)
    {
        return new PacketPlayOutNamedEntitySpawn(entityHuman);
    }

    public Packet generateDestroyPacket(EntityHuman human)
    {
        return new PacketPlayOutEntityDestroy(human.getId());
    }

    /***
     * Tools Side
     ***/

    public GameProfile randomGameProfile()
    {
        return new GameProfile(UUID.randomUUID(), "" + new Random().nextInt(9999999));
    }

    public Player getRandomPlayer()
    {
        Random random = new Random();
        List<Player> players = this.player.getWorld().getPlayers();

        players.remove(this.player);

        if (players.isEmpty())
            return null;

        return players.get(random.nextInt(players.size()));
    }
}