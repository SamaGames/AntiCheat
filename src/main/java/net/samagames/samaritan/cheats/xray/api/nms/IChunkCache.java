/**
 * @author Aleksey Terzi
 *
 */

package net.samagames.samaritan.cheats.xray.api.nms;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

public interface IChunkCache {
    DataInputStream getInputStream(File folder, int x, int z);

    DataOutputStream getOutputStream(File folder, int x, int z);

    void closeCacheFiles();
}