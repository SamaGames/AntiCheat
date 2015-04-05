package net.samagames.anticheat.speedhack;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R1.*;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.CheatTask;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 22/03/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class KillAura extends CheatTask {

    public final long CHECK_INTERVAL = 1*10*1000;
    public final int CHECK_DURATION = 20;
    public EntityHuman target = null;
    public Location targetLocation = null;
    public int numberTouched = 0;
    public HashMap<VirtualLocation, VirtualLocation> touched = new HashMap<>();
    public long nextTest = System.currentTimeMillis();
    public int countDown = 0;
    private int orientation = -1;

    public KillAura(final Player player) {
        super(player);
        //AntiCheat.instance.protocol.injectPlayer(player);
    }

    public void onClick(int entityID)
    {
        if(targetLocation == null || target == null)
            return;

        if(target.getId() != entityID)
            return;

        touched.put(new VirtualLocation(targetLocation.clone()), new VirtualLocation(player.getLocation().clone()));
        destroyTarget();
        numberTouched++;
        if(numberTouched >= 7)
        {
            AntiCheat.punishmentsManager.automaticBan(player, "ForceField/KillAura", new KillauraCheatLog(player, touched));
            numberTouched = 0;
            return;
        }
        generateRandomTarget();
        countDown = CHECK_DURATION;

    }

    /*public void onClick(PlayerInteractEvent event)
    {
        if(targetLocation == null || target == null)
            return;

        //Check si touche la target
        boolean touch = isTargeting(player, targetLocation, 20, 1.05);

        if(touch)
        {
            touched.put(new VirtualLocation(targetLocation.clone()), new VirtualLocation(player.getLocation().clone()));
            destroyTarget();
            numberTouched++;
            if(numberTouched >= 7)
            {
                AntiCheat.punishmentsManager.automaticBan(player, "ForceField/KillAura", new KillauraCheatLog(player, touched));
                numberTouched = 0;
                return;
            }
            generateRandomTarget();
            countDown = CHECK_DURATION;
        }
    }*/

    public void run()
    {
        super.run();
        long time = System.currentTimeMillis();

        if(targetLocation != null && target != null)
        {
            countDown--;
            if(countDown <= 0)
            {
                destroyTarget();
                countDown = 0;
            }
        }

        if(time > nextTest)
        {
            numberTouched = 0;
            destroyTarget();

            generateTriggerTarget();

            nextTest = time + CHECK_INTERVAL;
        }

        /*Location playerLocation = player.getLocation();

        Location loc = getLocationBehondPlayer(playerLocation, 2);

        try {
            ParticleEffect.VILLAGER_HAPPY.display(0,0,0,0,1,loc,player);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    /*** Engine Side ***/

    public void destroyTarget()
    {
        if(target == null || targetLocation == null)
            return;

        sendPacket(player, generateDestroyPacket(target));
        target = null;
        targetLocation = null;
    }

    public void generateRandomTarget()
    {
        /*UUID uvictim = UUID.randomUUID();
        String nvictim = ""+new Random().nextInt(9999999);*/

        Player victim = getRandomPlayer();
        if (victim == null)
        {
            return;
        }

        Location victimLocation = getRandomLocationAroundPlayer(player.getLocation(), 3);

        final EntityPlayer entityHuman = generatePlayer(victimLocation, new GameProfile(victim.getUniqueId(), victim.getName()));
        target = entityHuman;
        targetLocation = victimLocation;

        //sendPacket(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityHuman));
        sendPacket(player, generateSpawnPacket(entityHuman));
        //sendPacket(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, entityHuman));
        countDown = CHECK_DURATION;
    }

    public void generateTriggerTarget()
    {
        /*UUID uvictim = UUID.randomUUID();
        String nvictim = ""+new Random().nextInt(9999999);*/

        Player victim = getRandomPlayer();
        if (victim == null)
        {
            return;
        }

        Location victimLocation = getLocationBehondPlayer(player.getLocation(), 3);


        final EntityPlayer entityHuman = generatePlayer(victimLocation, new GameProfile(victim.getUniqueId(), victim.getName()));
        target = entityHuman;
        targetLocation = victimLocation;

        //sendPacket(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, entityHuman));
        sendPacket(player, generateSpawnPacket(entityHuman));
        //sendPacket(player, new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, entityHuman));
        countDown = CHECK_DURATION;
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

    /*** Locations Side ***/

    public Location getLocationBehondPlayer(Location referential, double radius)
    {
        float yaw = referential.getYaw();
        float finalYaw = yaw - 90;

        double relativeX = Math.cos(Math.toRadians(finalYaw)) * radius;
        double relativeZ = Math.sin(Math.toRadians(finalYaw)) * radius;
        double relativeY = -1;

        return new Location(referential.getWorld(),
                referential.getX() + relativeX,
                referential.getY() + relativeY,
                referential.getZ() + relativeZ);
    }

    public Location getRandomLocationAroundPlayer(Location referential, double radius)
    {
        Random random = new Random();
        int location = orientation * 90;
        orientation *= -1;
        double finalYaw = Math.toRadians(random.nextInt(360) + location);
        double finalPitch = Math.toRadians(random.nextInt(360 + location));

        double relativeX = Math.cos(finalPitch) * Math.sin(finalYaw) * radius;
        double relativeZ = Math.sin(finalPitch) * Math.sin(finalYaw) * radius;
        double relativeY = Math.cos(finalPitch) * radius;

        if(relativeY < 0.5)
        {
            relativeY = 0.5;
        }

        return new Location(referential.getWorld(),
                referential.getX() + relativeX,
                referential.getY() + relativeY,
                referential.getZ() + relativeZ);
    }

    /**
     * Retourne true si le joueur vise la target
     * @param player le shooter
     * @param target la cible
     * @param maxRange en bloc
     * @param aiming 1 par d�faut, pour augmenter la marge de 20% 1.20
     * @return
     */
    public boolean isTargeting(Player player, Location target, int maxRange, double aiming) {
        Location playerEyes = player.getEyeLocation();

        final Vector direction = playerEyes.getDirection().normalize();

        Location loc = playerEyes.clone();
        Location testLoc;
        double lx, ly, lz;
        double px, py, pz;

        Vector progress = direction.clone().multiply(0.70);
        maxRange = (100 * maxRange / 70);

        int loop = 0;
        while (loop < maxRange) {
            loop++;
            loc.add(progress);
            //if (!wallHack)
            lx = loc.getX();
            ly = loc.getY();
            lz = loc.getZ();

                testLoc = target.clone().add(0, 0.85, 0);
                px = testLoc.getX();
                py = testLoc.getY();
                pz = testLoc.getZ();

                // Touche ou pas
                boolean dX = Math.abs(lx - px) < 0.70 * aiming;
                boolean dY = Math.abs(ly - py) < 1.70 * aiming;
                boolean dZ = Math.abs(lz - pz) < 0.70 * aiming;

                if (dX && dY && dZ) {
                    return true;
                }
        }

        return false;
    }

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
        //entityHuman.setInvisible(true);
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
