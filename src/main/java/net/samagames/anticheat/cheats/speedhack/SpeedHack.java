package net.samagames.anticheat.cheats.speedhack;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.EnumPlayerAction;
import net.minecraft.server.v1_8_R1.WorldServer;
import net.samagames.anticheat.AntiCheat;
import net.samagames.anticheat.CheatTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class SpeedHack extends CheatTask {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    /// COLOR
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    /*** Constantes ***/
    public double NORMAL_SPEED = 4.317;     // m/s
    public double SNEAK_SPEED = 1.310;       // m/s
    public double SPRINT_SPEED = 5.612;     // m/s
    public double SMOOTH_COEFF = 500.0/50.0;
    public int CHECK_LOOP = 25;
    public double ERROR_ = 1.5D;

    /*** Variables ***/

    public boolean need_Check = true;

    public boolean sneak = player.isSneaking();
    public boolean sprint = player.isSprinting();

    public Location lastLocation = player.getLocation();
    public long lastLocationTime = System.currentTimeMillis();

    public long startTime = System.currentTimeMillis();

    public double velocityDistance = 0.0;

    public double sommeDistance = 0;
    public double sommeTemps = 0;

    public double virtualSpeed = 0.0; // m/s
    public double realSpeed = 0.0;
    /** Main loop **/
    protected Location currentLocation;
    protected Location previousLocation;
    /** Check loop **/
    protected Location ccurrentLocation;
    protected Location cpreviousLocation;
    protected int level = 0;

    public SpeedHack(Player player) {
        super(player, true);

        this.currentLocation = player.getLocation();
        this.previousLocation = player.getLocation();

       /* f = new File(AntiCheat.instance.getDataFolder(), "data.txt");
        try {
            f.createNewFile();
            fw = new FileWriter(f);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    public void playerAction(EnumPlayerAction action)
    {
        AntiCheat.log("[M] " + player.getName() + " Action: " + ANSI_RED + action.toString() + ANSI_RESET);
        if(action.equals(EnumPlayerAction.START_SNEAKING))
        {
            sneak = true;
            return;
        }
        if(action.equals(EnumPlayerAction.STOP_SNEAKING))
        {
            sneak = false;
            return;
        }
        if(action.equals(EnumPlayerAction.START_SPRINTING))
        {
            sprint = true;
            return;
        }
        if(action.equals(EnumPlayerAction.STOP_SPRINTING))
        {
            sprint = false;
            return;
        }
    }

    public void updateVelocity(Vector vector)
    {
        velocityDistance = getHDistanceVector(vector)*1000.0;
    }

    public void updateLocation(double x, double y, double z)
    {
        long time = System.currentTimeMillis();
        Location newLocation = new Location(player.getWorld(), x,y,z);

        double distance = getHDistance(lastLocation, newLocation);

        sommeDistance += distance;
        //AntiCheat.log("[S] " + player.getName() + " D: " + ANSI_YELLOW + sommeDistance + ANSI_RESET);

        double diffTime = (time - lastLocationTime);

        sommeTemps += diffTime;
        //AntiCheat.log("[S] " + player.getName() + " T: " + ANSI_YELLOW + sommeTemps + ANSI_RESET);

        lastLocationTime = time;
        lastLocation = newLocation;

        AntiCheat.log("[S] " + player.getName() + " FRICTION: " + ANSI_CYAN + getSurfaceFriction() + ANSI_RESET);

        if(System.currentTimeMillis() >= startTime + 1000)
        {
            double speed = sommeDistance / sommeTemps;

            speed *= 1000.0;

            realSpeed = speed - velocityDistance;
            AntiCheat.log("[S] " + player.getName() + " ISPEED: " + ANSI_RED + speed + ANSI_RESET);
            AntiCheat.log("[S] " + player.getName() + " VSPEED: " + ANSI_RED + virtualSpeed + ANSI_RESET);
            //AntiCheat.log("[S] " + player.getName() + " VELOCITY: " + ANSI_RED + player.getVelocity().toString() + ANSI_RESET);
            //AntiCheat.log("[S] " + player.getName() + " DISTANCE: " + ANSI_YELLOW + distance + ANSI_RESET);
            //AntiCheat.log("[S] " + player.getName() + " DTIME: " + ANSI_CYAN + diffTime + ANSI_RESET);
            AntiCheat.log("[S] " + player.getName() + " PING: " + ANSI_GREEN + AntiCheat.getPlayer(player.getUniqueId()).getPing() + ANSI_RESET);

            startTime = System.currentTimeMillis();

            sommeTemps = 0;
            sommeDistance = 0;

            need_Check = true;
        }
    }

    @Override
    public void run() {
        super.run();

        if(sneak)
        {
            calculSneak();
        }else if(sprint)
        {
            calculSprint();
        }else
        {
            calculNormal();
        }

        virtualSpeed += velocityDistance;

        if(need_Check)
        {
            check();
            need_Check = false;
        }

/*
        currentLocation = player.getLocation().clone();

        int diffPing = ping - ((CraftPlayer)player).getHandle().ping;
        ping = ((CraftPlayer)player).getHandle().ping;

        if (!hasMoved(previousLocation, currentLocation))
        {
            if(noMove >= 3)
            {
                //AntiCheat.log("--NOMOVE");
                calculStop();
                noMove = 0;
                vitesseAmortie = 0.0;
            }
            noMove++;
            return;
        }
        need_Check++;
        ACPlayer acp = AntiCheat.getPlayer(player.getUniqueId());

        if(need_Check >= CHECK_LOOP)
        {
            check();
            need_Check = 0;

            acp.walkedDistance = 0;
        }



        double distance = getHDistance(previousLocation, currentLocation)/(1-(Math.abs(diffPing*0.4)));

        acp.walkedDistance += distance;

        //vitesseAmortie += ((distance - vitesseAmortie)*(distance - vitesseAmortie))/SMOOTH_COEFF;
        vitesseAmortie += ((distance - vitesseAmortie))/(SMOOTH_COEFF-(10.0/20.0));

        CraftPlayer cp = ((CraftPlayer) player);
        boolean isOnGround = cp.isOnGround();
        boolean isSprinting = cp.isSprinting();
        boolean isSneaking = cp.isSneaking();

        if(isSneaking)
        {
            calculSneak(acp.startedSneak);
        }else if(isSprinting)
        {
            calculSprint(acp.startedSprinting);
        }else
        {
            calculNormal();
        }

        previousLocation = currentLocation.clone();

        /*try {

            fw.write((System.currentTimeMillis()-timeStarted)+"; " + vitesse +"; "+ lol + "\n");
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void asyncExec() {

    }

    public void check()// Executé toute les 15 ticks
    {
        findSpeedHack(realSpeed);
    }

    public double getMaxDistancePerTick() {
        double maxPerSecond = virtualSpeed;

        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {

                double multiplier = (effect.getAmplifier()+1) *0.2;
                AntiCheat.log("Active speed potion level: "+(effect.getAmplifier()+1) + " multiplier: " + multiplier);
                maxPerSecond += multiplier*maxPerSecond;
            } else if (effect.getType().equals(PotionEffectType.SLOW)) {
                double reduction = 15.0 * effect.getAmplifier();
                maxPerSecond = (maxPerSecond * (100.0 - reduction)) / 100.0;
            }
        }

        if (player.isFlying()) {
            maxPerSecond = 11;
        }

        double perMilliSecond = maxPerSecond / 1000.0;
        int ping = AntiCheat.getPlayer(player.getUniqueId()).getPing();

        AntiCheat.log("ping = " + ANSI_GREEN + ping + ANSI_RESET + ", max = " + perMilliSecond * (20 + ping) + " // PerSec : " + maxPerSecond);

        return maxPerSecond;
    }

    public void findSpeedHack(double distance) {
        double max = getMaxDistancePerTick();

        double diff = Math.abs(max - distance);

        if(distance > max)
        {
            if (diff > ERROR_) {

                AntiCheat.log("[M] " + player.getName() + " is speedhacking (d = " + distance + " max: " + max);
                level ++;
                if (level >= 1) {
                    Bukkit.broadcastMessage(ChatColor.RED + "Le joueur "+player.getName()+" speedhack !");
                    level = 0;
                } else {
                    Bukkit.getScheduler().runTaskLater(AntiCheat.instance, new Runnable() {
                        @Override
                        public void run() {
                            if (level > 0)
                                level --;
                        }
                    }, 3*20L);
                }

            }
        }
    }

    public void calculSneak()
    {
        //long time = System.currentTimeMillis() - startedTime;

        //AntiCheat.log("Sneak_");

        //virtualSpeed += ((SNEAK_SPEED - virtualSpeed)*(SNEAK_SPEED - virtualSpeed))/SMOOTH_COEFF;
        virtualSpeed += ((SNEAK_SPEED - virtualSpeed))/SMOOTH_COEFF;
        if(virtualSpeed < SNEAK_SPEED)
        {
            virtualSpeed = SNEAK_SPEED - 0.0001;
        }

    }

    public void calculSprint()
    {
        //long time = System.currentTimeMillis() - startedTime;

        //AntiCheat.log("Sprint-");

        //virtualSpeed -= (-(SPRINT_SPEED - virtualSpeed)*(SPRINT_SPEED - virtualSpeed))/SMOOTH_COEFF;
        virtualSpeed += ((SPRINT_SPEED - virtualSpeed))/SMOOTH_COEFF;

        if(virtualSpeed > SPRINT_SPEED)
        {
            virtualSpeed = SPRINT_SPEED - 0.0001;
        }
    }

    public void calculNormal()
    {
        //AntiCheat.log("NORMAL---");

        //virtualSpeed -= (-(NORMAL_SPEED - virtualSpeed)*(NORMAL_SPEED - virtualSpeed))/SMOOTH_COEFF;
        virtualSpeed += ((NORMAL_SPEED - virtualSpeed))/SMOOTH_COEFF;

        if(virtualSpeed > NORMAL_SPEED)
        {
            virtualSpeed = NORMAL_SPEED - 0.0001;
        }
    }

    public void calculStop()
    {
        //AntiCheat.log("STOP---");

        //virtualSpeed -= (-(0 - virtualSpeed)*(0 - virtualSpeed))/SMOOTH_COEFF;
        virtualSpeed += ((0 - virtualSpeed))/SMOOTH_COEFF;

        if(virtualSpeed < 0)
        {
            virtualSpeed = 0;
        }
    }

    public boolean hasMoved(final Location from, final Location to) {
        return from.getX() != from.getX() || from.getY() != from.getY() || from.getZ() != to.getZ();
    }

    public double getHDistanceVector(Vector vector)
    {
        return Math.sqrt((vector.getX() * vector.getX()) + (vector.getZ() * vector.getZ()));
    }

    public double getHDistance(final Location from, final Location to) { //  m
        double XDiff = from.getX() - to.getX();
        double ZDiff = from.getZ() - to.getZ();
        return Math.sqrt((XDiff * XDiff) + (ZDiff*ZDiff));
    }

    public double getVDistance(final Location from, final Location to) { //  m
        double YDiff = from.getY() - to.getY();
        return Math.abs(YDiff);
    }

    public float getSurfaceFriction()
    {
        float f2 = 1.0F;

        if (((CraftPlayer)player).isOnGround())
        {
            WorldServer w = ((CraftWorld)player.getWorld()).getHandle();
            f2 = 1.0F - w.c(new BlockPosition(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())).frictionFactor;
        }

        return f2;
    }
}
