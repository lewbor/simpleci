package simpleci.worker.cache;

public interface DownloadedCacheHandler {
    void handleDownloadedCache(String tmpCacheFilePath) throws Exception;
}
