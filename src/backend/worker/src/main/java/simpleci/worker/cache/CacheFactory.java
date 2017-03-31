package simpleci.worker.cache;

import simpleci.shared.job.cache.*;
import simpleci.worker.job.JobContext;
import simpleci.shared.job.config.EnvironmentVar;

public class CacheFactory {
    public static CacheManager fromJobContext(JobContext context) {
        String cacheFileName = cacheFileName(context);

        switch (context.jobSettings.cacheOptions.type) {
            case CacheType.AMAZON_S3:
                return new S3CacheManager((S3CacheOptions) context.jobSettings.cacheOptions, cacheFileName);
            case CacheType.GOOGLE_STORAGE:
                return new GoogleStorageCacheManager((GoogleStorageCacheOptions) context.jobSettings.cacheOptions, cacheFileName);
            default:
                return new NoOpCacheManager();
        }
    }

    private static String cacheFileName(JobContext jobContext) {
        String cacheFileName;
        if(jobContext.jobConfig.cache.key.isEmpty()) {
            cacheFileName = String.format("project_%d.tar.gz", jobContext.jobSettings.info.projectId);
        } else {
            String cacheKey = jobContext.jobConfig.cache.key;
            for(EnvironmentVar envVar : jobContext.jobEnvVars.allEnvironment) {
                cacheKey = cacheKey.replace("$" + envVar.name, envVar.value);
            }
            cacheFileName = String.format("project_%d_%s.tar.gz", jobContext.jobSettings.info.projectId, cacheKey);
        }

        return cacheFileName;
    }
}
