package simpleci.worker.cache;

import simpleci.worker.job.JobOutputProcessor;

public interface CacheManager {
    void downloadCache(JobOutputProcessor outputProcessor, DownloadedCacheHandler handler);
    void uploadCache(JobOutputProcessor outputProcessor, String cachePath);
}

