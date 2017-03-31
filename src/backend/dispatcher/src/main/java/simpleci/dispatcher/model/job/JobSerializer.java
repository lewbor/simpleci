package simpleci.dispatcher.model.job;

import simpleci.dispatcher.model.entity.Job;
import simpleci.dispatcher.model.entity.SshKey;
import simpleci.dispatcher.model.entity.cache.Cache;
import simpleci.dispatcher.model.entity.cache.GoogleStorageCache;
import simpleci.dispatcher.model.entity.cache.S3Cache;
import simpleci.shared.job.JobInfo;
import simpleci.shared.job.JobSettings;
import simpleci.shared.job.RepositorySettings;
import simpleci.shared.job.SshKeyOptions;
import simpleci.shared.job.cache.CacheOptions;
import simpleci.shared.job.cache.GoogleStorageCacheOptions;
import simpleci.shared.job.cache.NoCacheOptions;
import simpleci.shared.job.cache.S3CacheOptions;

public class JobSerializer {

    public static JobSettings toSettings(Job job) {
        JobSettings settings = new JobSettings(
                new JobInfo(
                        job.id,
                        job.build.id,
                        job.build.project.id,
                        job.build.number,
                        job.number,
                        job.stage),
                new RepositorySettings(
                        job.build.project.repositoryUrl,
                        job.build.commit,
                        job.build.commitRange,
                        job.build.branch,
                        job.build.tag),
                makeSshKeyOptions(job.build.project.sshKey),
                makeCacheOptions(job.build.project.cache));

        return settings;
    }

    private static SshKeyOptions makeSshKeyOptions(SshKey sshKey) {
        if(sshKey == null) {
            return new SshKeyOptions();
        }
        return new SshKeyOptions(sshKey.publicKey, sshKey.privateKey);
    }

    private static CacheOptions makeCacheOptions(Cache cache) {
        if (cache instanceof GoogleStorageCache) {
            GoogleStorageCache googleStorageCache = (GoogleStorageCache) cache;
            return new GoogleStorageCacheOptions(
                    googleStorageCache.gcAccount.serviceAccount,
                    googleStorageCache.bucket);
        } else if (cache instanceof S3Cache) {
            S3Cache s3Cache = (S3Cache) cache;
            return new S3CacheOptions(s3Cache.endPoint, s3Cache.bucket, s3Cache.accessKey, s3Cache.secretKey);
        } else {
            return new NoCacheOptions();
        }
    }
}
