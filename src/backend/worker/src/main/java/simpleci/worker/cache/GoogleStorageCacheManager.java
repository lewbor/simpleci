package simpleci.worker.cache;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;
import simpleci.worker.job.JobOutputProcessor;
import simpleci.shared.job.cache.GoogleStorageCacheOptions;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

public class GoogleStorageCacheManager implements CacheManager {
    final static org.slf4j.Logger logger = LoggerFactory.getLogger(GoogleStorageCacheManager.class);
    private static final String APPLICATION_NAME = "simpleci-worker";

    private final GoogleStorageCacheOptions settings;
    private final String cacheFileName;

    public GoogleStorageCacheManager(GoogleStorageCacheOptions settings, String cacheFileName) {
        this.settings = settings;
        this.cacheFileName = cacheFileName;
    }

    @Override
    public void downloadCache(JobOutputProcessor outputProcessor, DownloadedCacheHandler handler) {
        String tmpCacheFileName = "cache-" + UUID.randomUUID() + ".tar.gz";
        File tmpDir = Files.createTempDir();
        String tmpCacheFilePath = Paths.get(tmpDir.getAbsolutePath(), tmpCacheFileName).toString();

        try {
            outputProcessor.output("Downloading cache file " + cacheFileName + " from google storage\n");
            Storage client = createClient();
            FileOutputStream os = new FileOutputStream(tmpCacheFilePath);
            client.objects().get(settings.bucketName, cacheFileName)
                  .executeMediaAndDownloadTo(os);
            os.close();

            handler.handleDownloadedCache(tmpCacheFilePath);
            outputProcessor.output("Cache downloaded\n");


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
                outputProcessor.output("Uploading cache file " + cacheFileName + " to google storage\n");

                Storage client = createClient();
                File uploadFile = new File(cachePath);
                InputStreamContent contentStream = new InputStreamContent(
                        null, new FileInputStream(uploadFile));
                contentStream.setLength(uploadFile.length());
                StorageObject objectMetadata = new StorageObject()
                        .setName(cacheFileName);

                Storage.Objects.Insert insertRequest = client.objects().insert(
                        settings.bucketName, objectMetadata, contentStream);

                insertRequest.execute();

                outputProcessor.output("Cache uploaded\n");
        } catch (GeneralSecurityException | IOException e) {
            outputProcessor.output("Error upload cache: " + e.getMessage() + "\n");
        }
    }


    private Storage createClient() throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential.fromStream(
                new ByteArrayInputStream(settings.serviceAccount.getBytes(StandardCharsets.UTF_8)))
                                                      .createScoped(Collections.singleton(StorageScopes.CLOUD_PLATFORM));

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
        return new Storage.Builder(
                httpTransport, JSON_FACTORY, null).setApplicationName(APPLICATION_NAME)
                                                  .setHttpRequestInitializer(credential).build();
    }
}
