package simpleci.worker.cache;

import com.google.common.io.Files;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import simpleci.worker.job.JobOutputProcessor;
import simpleci.shared.job.cache.S3CacheOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

public class S3CacheManager implements CacheManager {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(S3CacheManager.class);

    private final S3CacheOptions settings;
    private final String cacheFileName;

    public S3CacheManager(S3CacheOptions settings, String cacheFileName) {
        this.settings = settings;
        this.cacheFileName = cacheFileName;
    }

    @Override
    public void downloadCache(JobOutputProcessor outputProcessor, DownloadedCacheHandler handler) {
        String tmpCacheFileName = "cache-" + UUID.randomUUID() + ".tar.gz";
        File tmpDir = Files.createTempDir();
        String tmpCacheFilePath = Paths.get(tmpDir.getAbsolutePath(), tmpCacheFileName).toString();

        try {
            outputProcessor.output("Downloading cache " + cacheFileName + " from s3 server " + settings.endPoint + "\n");

            MinioClient minioClient = new MinioClient(settings.endPoint, settings.accessKey, settings.secretKey);
            ObjectStat remoteCacheStat = minioClient.statObject(settings.bucketName, cacheFileName);
            if (remoteCacheStat.length() > 0) {
                minioClient.getObject(settings.bucketName, cacheFileName, tmpCacheFilePath);
                handler.handleDownloadedCache(tmpCacheFilePath);
                outputProcessor.output("Cache downloaded\n");
            } else {
                outputProcessor.output("Remote cache does not exists\n");
            }

        } catch (Exception e) {
            outputProcessor.output("Error download cache: " + e.getMessage() + "\n");
        } finally {
            try {
                FileUtils.deleteDirectory(tmpDir);
            } catch (IOException e) {
                logger.error("", e);
            }

        }
    }

    @Override
    public void uploadCache(JobOutputProcessor outputProcessor, String cachePath) {
        try {
                outputProcessor.output(String.format("Uploading cache file %s to s3 server %s\n", cachePath, settings.endPoint));

                MinioClient minioClient = new MinioClient(settings.endPoint, settings.accessKey, settings.secretKey);
                minioClient.putObject(settings.bucketName, cacheFileName, cachePath);
                outputProcessor.output("Cache uploaded\n");
        } catch (Exception e) {
            outputProcessor.output("Error upload cache: " + e.getMessage() + "\n");
        }
    }

}
