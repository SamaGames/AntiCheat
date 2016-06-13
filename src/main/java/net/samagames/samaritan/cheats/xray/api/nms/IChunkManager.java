/**
 * @author Aleksey Terzi
 *
 */

package net.samagames.samaritan.cheats.xray.api.nms;

import java.util.HashSet;

import org.bukkit.entity.Player;

public interface IChunkManager {
    boolean canResendChunk(int chunkX, int chunkZ);
    void resendChunk(int chunkX, int chunkZ, HashSet<Player> affectedPlayers);
}