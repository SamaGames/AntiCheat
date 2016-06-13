/**
 * @author Aleksey Terzi
 *
 */

package net.samagames.samaritan.cheats.xray.v1_9_R2;

import java.util.HashSet;

import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.PacketPlayOutMapChunk;
import net.minecraft.server.v1_9_R2.PacketPlayOutUnloadChunk;
import net.minecraft.server.v1_9_R2.PlayerChunk;
import net.minecraft.server.v1_9_R2.PlayerChunkMap;

import net.samagames.samaritan.cheats.xray.api.nms.IChunkManager;
import org.bukkit.entity.Player;

public class ChunkManager implements IChunkManager {
    private PlayerChunkMap chunkMap;

    public ChunkManager(PlayerChunkMap chunkMap) {
        this.chunkMap = chunkMap;
    }

    public boolean canResendChunk(int chunkX, int chunkZ) {
        if(!this.chunkMap.isChunkInUse(chunkX, chunkZ)) return false;

        PlayerChunk playerChunk = this.chunkMap.getChunk(chunkX, chunkZ);

        return playerChunk != null && playerChunk.chunk != null && playerChunk.chunk.isReady();
    }

    public void resendChunk(int chunkX, int chunkZ, HashSet<Player> affectedPlayers) {
        if(!this.chunkMap.isChunkInUse(chunkX, chunkZ)) return;

        PlayerChunk playerChunk = this.chunkMap.getChunk(chunkX, chunkZ);

        if(playerChunk == null || playerChunk.chunk == null || !playerChunk.chunk.isReady()) return;

        for(EntityPlayer player : playerChunk.c) {
            player.playerConnection.sendPacket(new PacketPlayOutUnloadChunk(chunkX, chunkZ));
            player.playerConnection.sendPacket(new PacketPlayOutMapChunk(playerChunk.chunk, 0xffff));

            affectedPlayers.add(player.getBukkitEntity());
        }
    }
}