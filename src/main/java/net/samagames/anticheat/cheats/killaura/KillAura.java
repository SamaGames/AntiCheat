package net.samagames.anticheat.cheats.killaura;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R1.*;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.CheatTask;
import net.samagames.anticheat.cheats.VirtualLocation;
import net.samagames.anticheat.utils.MathUtils;
import net.samagames.anticheat.utils.VectorUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.*;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 22/03/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class KillAura extends CheatTask {

    //* For production
    public final long CHECK_INTERVAL = 2*60*1000;/**/

    /*For tests
    public final long CHECK_INTERVAL = 20*1000;/**/

    public final int CHECK_DURATION = 15; //En tick
    public final double POURCENTAGE_FOR_BAN = 7;

    public final int CHECK_TO_DISPLAY = 10;

    public boolean activeCheck = true;

    public int lastAngle = 0;

    public EntityHuman target = null;
    public Location targetLocation = null;
    public boolean isTouched = false;

    public int numberTouched = 0;
    public int numberDisplayed = 1;
    public HashMap<VirtualLocation, VirtualLocation> touched = new HashMap<>();

    public long nextTest = System.currentTimeMillis();
    public int countDown = 0;

    public HashMap<Integer, Integer> angles = new HashMap<>();
    public List<Vector> positionsTemplate = new ArrayList<>();
    public Location startingPlayerLocation = null;

    public KillAura(final Player player) {
        super(player, true);
        positionsTemplate.add(new Vector(3, 2.5, 1.5));
        positionsTemplate.add(new Vector(2.5, 1, -2));
        positionsTemplate.add(new Vector(0, 0.5, 4));
        positionsTemplate.add(new Vector(0, 4, -2.5));
        positionsTemplate.add(new Vector(0, 4, 2.5));
        positionsTemplate.add(new Vector(3, 0.5, -3));
        positionsTemplate.add(new Vector(-4, 0.5, 2));
        positionsTemplate.add(new Vector(0, 4.5, 0));
        positionsTemplate.add(new Vector(3, 0.2, 3));
        positionsTemplate.add(new Vector(-2, 4.5, 0));

        resetAngles();
    }

    public void resetAngles()
    {
        angles.put(-180, 0);
        angles.put(-90, 0);
        angles.put(90, 0);
        angles.put(180, 0);
    }

    public void onClick(int entityID)
    {
        if(targetLocation == null || target == null)
            return;

        if(target.getId() != entityID)
            return;

        touchedTarget();
    }

    @Override
    public void exec() {

        long time = System.currentTimeMillis();

        if(targetLocation != null && target != null)
        {
            countDown--;
            if(countDown <= 0)
            {
                destroyTarget();
                countDown = 0;
                workingJob();
            }
        }

        if(!activeCheck)
        {
            return;
        }

        if(time > nextTest)
        {
            launchCheck();
        }
    }

    /*** Engine Side ***/

    public void touchedTarget()
    {
        touched.put(new VirtualLocation(targetLocation.clone()), new VirtualLocation(player.getLocation().clone()));
        isTouched = true;
        numberTouched++;

        destroyTarget();

        workingJob();
    }

    public void workingJob()
    {
        if(startingPlayerLocation == null)
            startingPlayerLocation = player.getLocation();

        if(!isTouched && numberDisplayed <= 1)
        {
            return;
        }
        if(!isTouched)
        {
            touched.put(null, new VirtualLocation(player.getLocation().clone()));
        }

        //Bukkit.broadcastMessage("Touched: " + numberTouched + " Displayed: " + numberDisplayed);

        isTouched = false;
        if(numberDisplayed < CHECK_TO_DISPLAY)
        {
            generateTarget(getRandomPlayer(), getRandomLocationAroundPlayer(startingPlayerLocation, 2.5));
            countDown = CHECK_DURATION;
            numberDisplayed++;
            return;
        }

        if(numberTouched >= POURCENTAGE_FOR_BAN)
        {
            AntiCheat.punishmentsManager.automaticBan(player, "ForceField/KillAura", new KillauraCheatLog(player, touched, numberTouched, numberDisplayed));
            numberTouched = 0;
            numberDisplayed = 1;
            startingPlayerLocation = null;
            return;
        }
    }

    public void launchCheck()
    {
        numberTouched = 0;
        numberDisplayed = 1;
        startingPlayerLocation = null;
        resetAngles();

        destroyTarget();

        generateTarget(getRandomPlayer(), getLocationBehondPlayer(player.getLocation(), 2));
        countDown = CHECK_DURATION;

        nextTest = System.currentTimeMillis() + CHECK_INTERVAL;
    }

    public void destroyTarget()
    {
        if(target == null || targetLocation == null)
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
        if(ptarget != null)
        {
            target = ((CraftPlayer) ptarget).getProfile();
            needToDlSkin = false;
        }

        if(target == null)
        {
            target = randomGameProfile();
        }

        if(position == null)
        {
            position = getLocationBehondPlayer(player.getLocation(), 2);
        }

        final EntityPlayer entityHuman = generatePlayer(position, target);
        this.target = entityHuman;
        targetLocation = position;

        if(needToDlSkin)
            sendPacket(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityHuman));

        sendPacket(player, generateSpawnPacket(entityHuman));

        if(needToDlSkin)
            sendPacket(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, entityHuman));
    }

    /*** Locations Side ***/

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
                referential.getZ() + relativeZ);
    }

    public Location getRandomLocationAroundPlayer(Location referential, double radius)
    {
        //int angle = getAngle() + new Random().nextInt(10);
        /*int angle = lastAngle + getAngle();

        double finalYaw = Math.toRadians(angle);
        double finalPitch = Math.toRadians(-45 - new Random().nextInt(20));

        double relativeX = Math.cos(finalPitch) * Math.sin(finalYaw) * radius;
        double relativeZ = Math.sin(finalPitch) * Math.sin(finalYaw) * radius;
        double relativeY = Math.cos(finalPitch) * radius;

        if(relativeY < 1.5)
        {
            relativeY = 1.5;
        }*/
        Vector relativePos = positionsTemplate.get((numberDisplayed-1)%positionsTemplate.size());
        VectorUtils.rotateAroundAxisY(relativePos, -referential.getYaw() * MathUtils.degreesToRadians);

        return referential.clone().add(relativePos);
    }

    /*public int randomAngle()
    {
        return angles.get(new Random().nextInt(angles.size()));
    }*/

    /*** Packet Side ***/

    public EntityPlayer generatePlayer(Location loc, GameProfile gameProfile)
    {
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();
        PlayerInteractManager playerInteractManager = new PlayerInteractManager(world);
        EntityPlayer entityHuman = new EntityPlayer(world.getServer().getServer(), world, gameProfile, playerInteractManager);
        entityHuman.setPosition(loc.getX(),
                loc.getY(),
                loc.getZ());
        entityHuman.setSneaking(true);
        entityHuman.setInvisible(true);
        return entityHuman;
    }

    public void sendPacket(Player p, Packet packet)
    {
        //AntiCheat.instance.protocol.sendPacket(player, packet);
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

    /*** Tools Side ***/

    public int getAngle()
    {
        int usesOfAngle = Integer.MAX_VALUE;
        List<Integer> tempAngles = new ArrayList<>();

        for(int a : angles.keySet())
        {
            int uses = angles.get(a);

            if(uses < usesOfAngle)
            {
                usesOfAngle = uses;
            }
        }

        for(int a : angles.keySet())
        {
            int uses = angles.get(a);

            if(uses <= usesOfAngle)
            {
                tempAngles.add(a);
            }
        }

        Random rr = new Random();
        int angle = tempAngles.get(rr.nextInt(tempAngles.size()));
        angles.put(angle, angles.get(angle)+1);

        return angle;
    }

    public GameProfile randomGameProfile()
    {
        return new GameProfile(UUID.randomUUID(), ""+new Random().nextInt(9999999));
    }

    public Player getRandomPlayer()
    {
        Random random = new Random();
        List<Player> players = player.getWorld().getPlayers();
        players.remove(player);
        if(players.size() == 0)
            return null;

        return players.get(random.nextInt(players.size()));
    }

    private void setPrivateField(@SuppressWarnings("rawtypes") Class type, Object object, String name, Object value) {
        try {
            Field f = type.getDeclaredField(name);
            f.setAccessible(true);
            f.set(object, value);
            f.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
