package simpleci.worker.cache;

import simpleci.worker.job.JobOutputProcessor;

public class NoOpCacheManager implements CacheManager {

    @Override
    public void downloadCache(JobOutputProcessor outputProcessor, DownloadedCacheHandler handler) {

    }

    @Override
    public void uploadCache(JobOutputProcessor outputProcessor, String cachePath) {

    }
}
