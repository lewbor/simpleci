package simpleci.worker.bash;

public final class Locations {
    public static final String HOME_DIR = "/home/simpleci";

    public static final String SSH_DIR = join(HOME_DIR, ".ssh");
    public static final String SSH_CONFIG_FILE = join(SSH_DIR, "config");
    public static final String SSH_PUBLIC_KEY = join(SSH_DIR, "id_rsa.pub");
    public static final String SSH_PRIVATE_KEY = join(SSH_DIR, "id_rsa");
    public static final String BUILD_DIR = join(HOME_DIR, "build");
    public static final String BUILD_SCRIPT = join(HOME_DIR, "build.sh");

    public static final String CACHE_FILE = join(HOME_DIR, "cache.tar.gz");
    public static final String CACHE_MD5_BEFORE = join(HOME_DIR, "md5_before");
    public static final String CACHE_MD5_AFTER = join(HOME_DIR, "md5_after");
    public static final String CACHE_MD5_DIFF = join(HOME_DIR, "md5_diff");

    private static String join(String first, String second) {
        return first + "/" + second;
    }
}
