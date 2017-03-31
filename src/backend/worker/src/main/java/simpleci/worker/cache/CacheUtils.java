package simpleci.worker.cache;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import simpleci.worker.system.SshClient;
import simpleci.worker.bash.Locations;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CacheUtils {

    public static void uploadCacheToDocker(SshClient dockerSsh, String cachePath)
            throws IOException, JSchException, SftpException {
        try (InputStream inStream = new FileInputStream(cachePath)) {
            dockerSsh.uploadFile(inStream, Locations.CACHE_FILE, 0775);
        }
    }


}
